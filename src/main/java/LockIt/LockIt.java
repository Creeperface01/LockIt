package LockIt;

import LockIt.blockEntity.LockBlockEntity;
import LockIt.utils.LockItUtils;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemLeather;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LockIt extends PluginBase {

    @Getter
    private static final String prefix = TextFormat.YELLOW + "[" + TextFormat.GREEN + "LockIt" + TextFormat.YELLOW + "] " + TextFormat.WHITE;

    @Getter
    private static final List<Integer> protectableBlocks = new ArrayList<>();

    private final MainListener mainListener = new MainListener(this);

    /*@Getter
    private final Map<String, LevelData> worlds = new HashMap<>();*/

    private static LockIt instance = null;

    /**
     * Configuration
     */

    @Getter
    private boolean autoLock = true;

    @Getter
    private int savePeriod = -1;

    @Getter
    private int infoItem = Item.LEATHER;

    /**
     *
     */

    @Override
    public void onLoad() {
        instance = this;
        BlockEntity.registerBlockEntity("LockIt", LockBlockEntity.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadConfig();

        /*for(Level level : getServer().getLevels().values()) {
            File world = new File(getDataFolder() + "/worlds/" + level.getFolderName());
            world.mkdirs();

            LevelData data = ConfigParser.parseConfig(world);

            if (data == null) {
                getLogger().warning("Could not load LockIt data from level "+level.getName());
            } else {
                setLevelData(level, data);
            }
        }

        getServer().getScheduler().scheduleDelayedRepeatingTask(new Runnable() {

            @Override
            public void run() {
                LockIt plugin = LockIt.getInstance();
                Map<String, LevelData> data = new HashMap<>(plugin.getWorlds());

                for (Map.Entry<String, LevelData> e : data.entrySet()) {
                    ConfigParser.save(new File(getDataFolder() + "/worlds/" + e.getKey()), e.getValue(), true);
                }
            }
        }, savePeriod, savePeriod);*/

        getServer().getPluginManager().registerEvents(mainListener, this);
    }

    /*@Override
    public void onDisable() {
        for (Map.Entry<String, LevelData> e : worlds.entrySet()) {
            ConfigParser.save(new File(getDataFolder() + "/worlds/" + e.getKey()), e.getValue(), false);
        }
    }*/

    /*public LevelData getLevelData(Level level) {
        return worlds.get(level.getFolderName());
    }

    public void setLevelData(Level level, LevelData data) {
        worlds.put(level.getFolderName(), data);
    }*/

    public void loadConfig() {
        autoLock = getConfig().getBoolean("enable_auto_lock");
        savePeriod = getConfig().getInt("auto_save_interval") * 20;
        Item item = Item.fromString(getConfig().getString("info_item", "leather"));
        if (item == null) {
            item = new ItemLeather();
        }

        infoItem = item.getId();

        ConfigSection blocks = getConfig().getSection("blocks");

        if (blocks.getBoolean("chest")) protectableBlocks.add(Item.CHEST);
        if (blocks.getBoolean("furnace")) {
            protectableBlocks.add(Item.FURNACE);
            protectableBlocks.add(Item.BURNING_FURNACE);
        }
        if (blocks.getBoolean("brewing")) protectableBlocks.add(Item.BREWING_STAND_BLOCK);
        if (blocks.getBoolean("dispenser")) protectableBlocks.add(Item.DISPENSER);
        if (blocks.getBoolean("hopper")) protectableBlocks.add(Item.HOPPER_BLOCK);
        //if(blocks.getBoolean("dropper")) protectableBlocks.add(Item.DROPPER);
        if (blocks.getBoolean("item_frame")) protectableBlocks.add(Item.ITEM_FRAME_BLOCK);
        if (blocks.getBoolean("sign")) {
            protectableBlocks.add(Item.WALL_SIGN);
            protectableBlocks.add(Item.SIGN_POST);
        }
        if (blocks.getBoolean("oak_door")) protectableBlocks.add(Item.WOODEN_DOOR_BLOCK);
        if (blocks.getBoolean("iron_door")) protectableBlocks.add(Item.IRON_DOOR_BLOCK);
        if (blocks.getBoolean("spruce_door")) protectableBlocks.add(Item.SPRUCE_DOOR_BLOCK);
        if (blocks.getBoolean("birch_door")) protectableBlocks.add(Item.BIRCH_DOOR_BLOCK);
        if (blocks.getBoolean("jungle_door")) protectableBlocks.add(Item.JUNGLE_DOOR_BLOCK);
        if (blocks.getBoolean("acacia_door")) protectableBlocks.add(Item.ACACIA_DOOR_BLOCK);
        if (blocks.getBoolean("dark_oak_door")) protectableBlocks.add(Item.DARK_OAK_DOOR_BLOCK);
        if (blocks.getBoolean("wooden_trap_door")) protectableBlocks.add(Item.TRAPDOOR);
        if (blocks.getBoolean("iron_trap_door")) protectableBlocks.add(Item.IRON_TRAPDOOR);
        if (blocks.getBoolean("oak_fence_gate")) protectableBlocks.add(Item.FENCE_GATE);
        if (blocks.getBoolean("birch_fence_gate")) protectableBlocks.add(Item.FENCE_GATE_BIRCH);
        if (blocks.getBoolean("spruce_fence_gate")) protectableBlocks.add(Item.FENCE_GATE_SPRUCE);
        if (blocks.getBoolean("jungle_fence_gate")) protectableBlocks.add(Item.FENCE_GATE_JUNGLE);
        if (blocks.getBoolean("acacia_fence_gate")) protectableBlocks.add(Item.FENCE_GATE_ACACIA);
        if (blocks.getBoolean("dark_oak_fence_gate")) protectableBlocks.add(Item.FENCE_GATE_DARK_OAK);
        if (blocks.getBoolean("trapped_chest")) protectableBlocks.add(Item.TRAPPED_CHEST);
    }

    public boolean canAccess(BlockData blockData, Player p) {
        /*if (p.hasPermission("lockit.deny")) {
            System.out.println("permission deny");
            return false;
        }*/

        if (p.hasPermission("lockit.access") || blockData == null) {
            return true;
        }

        if (blockData.password != null) {
            return blockData.passwordUsers.contains(p.getName().toLowerCase()) || blockData.owner.toLowerCase().equals(p.getName().toLowerCase());
        }

        return blockData.isPublic || blockData.owner.equalsIgnoreCase(p.getName()) || blockData.users.contains(p.getName().toLowerCase());
    }

    public boolean canAccess(Block b, Player p) {
        if (!protectableBlocks.contains(b.getId())) {
            return true;
        }

        BlockData blockData = LockItUtils.getBlockData(b);

        if (blockData == null) {
            return true;
        }

        return canAccess(blockData, p);
    }

    public boolean isLocked(Block b) {
        return LockItUtils.getBlockData(b) != null;
    }

    public static LockIt getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "You can use this command in game only");
            return false;
        }

        switch (cmd.getName().toLowerCase()) {
            case "lock":
                ActionData data = new ActionData();
                data.action = ActionData.LOCK;

                mainListener.players.put(sender.getName(), data);
                sender.sendMessage(TextFormat.GRAY + "Click on a block to protect it!");
                break;
            case "unlock":
                data = new ActionData();
                data.action = ActionData.UNLOCK;

                if (args.length >= 1) {
                    data.data = new String[]{args[0]};
                }

                mainListener.players.put(sender.getName(), data);
                sender.sendMessage(TextFormat.GRAY + "Click on a block to remove a protection!");
                break;
            case "public":
                data = new ActionData();
                data.action = ActionData.PUBLICLOCK;

                mainListener.players.put(sender.getName(), data);
                sender.sendMessage(TextFormat.GRAY + "Click on a block to make it public");
                break;
            case "passlock":
                if (args.length != 2) {
                    sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/passlock <password> <password>");
                    break;
                }

                if (!args[0].equals(args[1])) {
                    sender.sendMessage(TextFormat.RED + "Both passwords must be equal!");
                    break;
                }

                data = new ActionData();
                data.action = ActionData.PASSLOCK;
                data.data = new String[]{args[0]};

                mainListener.players.put(sender.getName(), data);
                sender.sendMessage(TextFormat.GRAY + "Click on a block to protect it!");
                break;
            case "modify":
                if (args.length < 1) {
                    sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/modify <add/remove/removeall/public> [players...]");
                    break;
                }

                data = new ActionData();

                switch (args[0].toLowerCase()) {
                    case "add":
                        if (args.length < 2) {
                            sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/modify add <players...>");
                            return false;
                        }

                        String[] players = new String[args.length - 1];
                        System.arraycopy(args, 1, players, 0, args.length - 1);

                        data.action = ActionData.ADD;
                        data.data = players;
                        break;
                    case "remove":
                        if (args.length < 2) {
                            sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/modify remove <players...>");
                            return false;
                        }

                        players = new String[args.length - 1];
                        System.arraycopy(args, 1, players, 0, args.length - 1);

                        data.action = ActionData.REMOVE;
                        data.data = players;
                        break;
                    case "removeall":
                        data.action = ActionData.REMOVEALL;
                        break;
                    case "public":
                        data.action = ActionData.PUBLIC;
                        break;
                    default:
                        sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/modify <add/remove/removeall/public> <players...>");
                        return false;
                }

                sender.sendMessage(TextFormat.GRAY + "Click on a protection to apply the modification");
                mainListener.players.put(sender.getName(), data);
                break;
            case "password":
                if (args.length != 1) {
                    sender.sendMessage(TextFormat.GRAY + "use " + TextFormat.YELLOW + "/password <password>");
                    return false;
                }

                data = new ActionData();
                data.action = ActionData.PASSWORD_REQUEST;
                data.data = new String[]{args[0]};
                mainListener.players.put(sender.getName(), data);
                sender.sendMessage(TextFormat.GRAY + "Click on a password protected block to open it");
                break;
        }

        return true;
    }
}