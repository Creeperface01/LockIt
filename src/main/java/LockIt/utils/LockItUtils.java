package LockIt.utils;

import LockIt.BlockData;
import LockIt.blockEntity.LockBlockEntity;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.MainLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by CreeperFace on 9.6.2017.
 */
public class LockItUtils {

    private static Set<Integer> blockEntityBlocks = new HashSet<Integer>() {
        {
            add(Block.FURNACE);
            add(Block.CHEST);
            add(Block.TRAPPED_CHEST);
            add(Block.ENDER_CHEST);
            add(Block.BREWING_BLOCK);
            add(Block.DISPENSER);
            add(Block.HOPPER_BLOCK);
            add(Block.DROPPER);
            add(Block.ITEM_FRAME_BLOCK);
            add(Block.SIGN_POST);
            add(Block.WALL_SIGN);
        }
    };

    public static void createBlockData(Block block, String player, boolean place) {
        createBlockData(block, player, place, null);
    }

    public static void createBlockData(Block block, String player, boolean place, CompoundTag nbt) {
        if (nbt == null) {
            nbt = createNBT(player, new ArrayList<>(), false, null, new ArrayList<>());
        }
        CompoundTag finalNBT = nbt;

        Position pos = block.getLocation();
        Runnable runnable = () -> {
            BlockEntity blockEntity = pos.getLevel().getBlockEntity(pos);

            if (blockEntity == null) {
                blockEntity = new LockBlockEntity(pos.getLevel().getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), new CompoundTag()
                        .putString("id", "LockIt")
                        .putInt("x", (int) pos.x)
                        .putInt("y", (int) pos.y)
                        .putInt("z", (int) pos.z)
                );
            }

            blockEntity.namedTag.putCompound("lockIt", finalNBT);
            if (blockEntity.chunk != null) {
                blockEntity.chunk.setChanged(true);
            }
        };


        if (place && blockEntityBlocks.contains(block.getId())) {
            Server.getInstance().getScheduler().scheduleDelayedTask(runnable, 1);
        } else {
            runnable.run();
        }
    }

    public static CompoundTag createNBT(String owner, List<String> users, boolean isPublic, String password, List<String> passwordUsers) {
        CompoundTag nbt = new CompoundTag()
                .putString("owner", owner.toLowerCase())
                .putList(new ListTag<StringTag>("users") {
                    {
                        for (String user : users) {
                            add(new StringTag("", user));
                        }
                    }
                })
                .putBoolean("isPublic", isPublic)
                .putList(new ListTag<StringTag>("passwordusers") {
                    {
                        for (String user : passwordUsers) {
                            add(new StringTag("", user));
                        }
                    }
                });

        if (password != null && !password.isEmpty()) {
            nbt.putString("password", password);
        }

        return nbt;
    }

    public static BlockData getBlockData(Position pos) {
        return getBlockData(pos, true);
    }

    public static BlockData getBlockData(Position pos, boolean findPart) {
        BlockEntity blockEntity = getBlockEntity(pos, true);

        if (blockEntity == null) {
            return null;
        }

        try {
            CompoundTag nbt = blockEntity.namedTag.getCompound("lockIt");

            BlockData data = new BlockData();
            data.users = new ArrayList<String>() {
                {
                    for (StringTag tag : nbt.getList("users", StringTag.class).getAll()) {
                        data.users.add(tag.data);
                    }
                }
            };

            data.owner = nbt.getString("owner");
            data.isPublic = nbt.getBoolean("isPublic");
            data.password = nbt.getString("password");

            data.passwordUsers = new ArrayList<String>() {
                {
                    for (StringTag tag : nbt.getList("passwordusers", StringTag.class).getAll()) {
                        data.passwordUsers.add(tag.data);
                    }
                }
            };

            return data;
        } catch (Exception e) {
            MainLogger.getLogger().logException(e);
        }

        return null;
    }

    public static void saveData(Position pos, BlockData data) {
        BlockEntity blockEntity = getBlockEntity(pos, true);
        if (blockEntity == null) {
            return;
        }

        CompoundTag tag = new CompoundTag("lockIt");
        ListTag<StringTag> users = new ListTag<>("users");

        for (String user : data.users) {
            users.add(new StringTag("", user));
        }
        tag.putString("owner", data.owner);
        tag.putBoolean("isPublic", data.isPublic);
        tag.putString("password", data.password);


        ListTag<StringTag> passUsers = new ListTag<>("passwordusers");

        for (String user : data.passwordUsers) {
            passUsers.add(new StringTag("", user));
        }

        blockEntity.namedTag.putCompound("lockIt", tag);
        if (blockEntity.chunk != null) {
            blockEntity.chunk.setChanged(true);
        }
    }

    public static void removeBlockData(Position pos) {
        BlockEntity blockEntity = getBlockEntity(pos, true);

        if (blockEntity != null) {
            if (blockEntity instanceof LockBlockEntity) {
                blockEntity.close();
                return;
            }

            blockEntity.namedTag.remove("lockIt");
        }
    }

    private static BlockEntity getBlockPart(Position pos) {
        Block b;
        if (pos instanceof Block) {
            b = (Block) pos;
        } else {
            b = pos.getLevelBlock();
        }

        BlockEntity blockData = null;

        if (b instanceof BlockDoor) {
            boolean isUp = (b.getDamage() & 8) > 0;

            Block part;

            if (isUp) {
                part = b.down();
            } else {
                part = b.up();
            }

            if (part.getId() == b.getId()) {
                blockData = getBlockEntity(part, false);
            }
        } else if (b instanceof BlockChest) {
            BlockEntityChest entity = (BlockEntityChest) b.level.getBlockEntity(b);

            if (entity != null && entity.isPaired() && entity.getPair() != null) {
                Block pair = entity.getPair().getBlock();

                if (pair instanceof BlockChest) {
                    blockData = getBlockEntity(pair, false);
                }
            }
        }

        return blockData;
    }

    private static BlockEntity getBlockEntity(Position pos, boolean findPart) {
        Level level = pos.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null || !blockEntity.namedTag.contains("lockIt")) {
            if (findPart) {
                return getBlockPart(pos);
            }

            return null;
        }

        return blockEntity;
    }
}
