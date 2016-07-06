package LockIt;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.math.Vector3;

import java.util.*;

public class LevelData {

    public Map<Integer, Set<BlockData>> blocks = new HashMap<>();

    /*public final List<BlockData> chests = new ArrayList<>();
    public final List<BlockData> furnaces = new ArrayList<>();
    public final List<BlockData> brewings = new ArrayList<>();
    public final List<BlockData> dispensers = new ArrayList<>();
    public final List<BlockData> hoppers = new ArrayList<>();
    public final List<BlockData> droppers = new ArrayList<>();
    public final List<BlockData> signs = new ArrayList<>();
    public final List<BlockData> ironDoors = new ArrayList<>();
    public final List<BlockData> oakDoors =  new ArrayList<>();
    public final List<BlockData> birchDoors =  new ArrayList<>();
    public final List<BlockData> spruceDoors = new ArrayList<>();
    public final List<BlockData> jungleDoors = new ArrayList<>();
    public final List<BlockData> acaciaDoors = new ArrayList<>();
    public final List<BlockData> darkOakDoors = new ArrayList<>();
    public final List<BlockData> trapDoors = new ArrayList<>();
    public final List<BlockData> ironTrapDoors = new ArrayList<>();
    public final List<BlockData> oakFenceGates = new ArrayList<>();
    public final List<BlockData> birchFenceGates =  new ArrayList<>();
    public final List<BlockData> spruceFenceGates = new ArrayList<>();
    public final List<BlockData> jungleFenceGates = new ArrayList<>();
    public final List<BlockData> acaciaFenceGates = new ArrayList<>();
    public final List<BlockData> darkOakFenceGates = new ArrayList<>();
    public final List<BlockData> trappedChests = new ArrayList<>();
    public final List<BlockData> itemFrames = new ArrayList<>();*/

    public void setData(int type, List<BlockData> data) {
        /*List<BlockData> field;

        try{
            field = (List<BlockData>) this.getClass().getField(type.trim()).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
            return;
        }

        field.addAll(data);*/

        blocks.get(type).addAll(data);
    }

    public Collection<BlockData> getData(int type) {
        /*List<BlockData> field;

        try{
            field = (List<BlockData>) this.getClass().getField(type.trim()).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
            return null;
        }

        return field;*/
        return blocks.get(type);
    }

    public BlockData createBlockData(Block b, String owner) {
        BlockData data = new BlockData(b, owner.toLowerCase());

        blocks.get(b.getId()).add(data);

        return data;
    }

    public void removeBlockData(Block b, BlockData data) {
        blocks.get(b.getId()).remove(data);
    }

    public BlockData getBlockData(Block b) {
        return getBlockData(b, true);
    }

    public BlockData getBlockData(Block b, boolean findPart) {
        BlockData data = find(b, blocks.get(b.getId()));

        if (findPart && data == null) {
            return getBlockPart(b);
        }

        return data;

        /*switch(b.getId()){
            case Item.CHEST:
                return find(b, chests);
            case Item.BURNING_FURNACE:
            case Item.FURNACE:
                return find(b, furnaces);
            case Item.BREWING_STAND_BLOCK:
                return find(b, brewings);
            case Item.DISPENSER:
                return find(b, dispensers);
            case Item.HOPPER_BLOCK:
                return find(b, hoppers);
            case Item.DROPPER:
                break;
            case Item.WALL_SIGN:
            case Item.SIGN_POST:
                return find(b, signs);
            case Item.WOODEN_DOOR_BLOCK:
                return find(b, oakDoors);
            case Item.BIRCH_DOOR_BLOCK:
                return find(b, birchDoors);
            case Item.SPRUCE_DOOR_BLOCK:
                return find(b, spruceDoors);
            case Item.JUNGLE_DOOR_BLOCK:
                return find(b, jungleDoors);
            case Item.ACACIA_DOOR_BLOCK:
                return find(b, acaciaDoors);
            case Item.DARK_OAK_DOOR_BLOCK:
                return find(b, darkOakDoors);
            case Item.TRAPDOOR:
                return find(b, trapDoors);
            case Item.IRON_TRAPDOOR:
                return find(b, ironTrapDoors);
            case Item.FENCE_GATE:
                return find(b, oakFenceGates);
            case Item.FENCE_GATE_BIRCH:
                return find(b, birchFenceGates);
            case Item.FENCE_GATE_SPRUCE:
                return find(b, spruceFenceGates);
            case Item.FENCE_GATE_JUNGLE:
                return find(b, jungleFenceGates);
            case Item.FENCE_GATE_ACACIA:
                return find(b, acaciaFenceGates);
            case Item.FENCE_GATE_DARK_OAK:
                return find(b, darkOakFenceGates);
            case Item.TRAPPED_CHEST:
                return find(b, trappedChests);
        }

        return null;*/
    }

    private BlockData find(Block b, Collection<BlockData> list) {
        if (list == null) {
            return null;
        }

        for (BlockData data : list) {
            if (b.equals(data)) {
                return data;
            }
        }

        return null;
    }

    private BlockData getBlockPart(Block b) {
        BlockData blockData = null;

        if (b instanceof BlockDoor) {
            boolean isUp = (b.getDamage() & 8) > 0;

            Block part;

            if (isUp) {
                part = b.getSide(Vector3.SIDE_DOWN);
            } else {
                part = b.getSide(Vector3.SIDE_UP);
            }

            if (part.getId() == b.getId()) {
                blockData = getBlockData(part, false);
            }
        } else if (b instanceof BlockChest) {
            BlockEntityChest entity = (BlockEntityChest) b.level.getBlockEntity(b);

            if (entity != null && entity.isPaired() && entity.getPair() != null) {
                Block pair = entity.getPair().getBlock();

                if (pair instanceof BlockChest) {
                    blockData = getBlockData(pair, false);
                }
            }
        }

        return blockData;
    }
}
