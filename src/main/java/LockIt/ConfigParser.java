package LockIt;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class ConfigParser {

    public static LevelData parseConfig(File folder) {
        LevelData data = new LevelData();

        for (int i : LockIt.getInstance().getProtectableBlocks()) {
            data.blocks.put(i, new HashSet<>());
        }

        if (!folder.isDirectory() || !folder.exists()) {
            return null;
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });

        for (File f : files) {
            Config config = new Config(f, Config.YAML);
            List<Object> all = (List) config.get("blocks");
            List<BlockData> blocks = new ArrayList<>();

            for (Object value : all) {
                try {
                    Map<String, Object> para = (Map<String, Object>) value;

                    List<String> users = new ArrayList<>();

                    for (Object owner : ((List<Object>) para.get("users"))) {
                        users.add((String) owner);
                    }

                    BlockData bData = new BlockData(new Vector3((int) para.get("x"), (int) para.get("y"), (int) para.get("z")), (String) para.get("owner"), users);
                    bData.isPublic = Boolean.valueOf(String.valueOf(para.get("public")));

                    if (para.containsKey("password")) {
                        bData.password = (Integer) para.get("password");
                    }

                    blocks.add(bData);
                } catch (Exception e) {
                    System.out.println("Invalid or corrupted data in " + f.getName());
                    e.printStackTrace();
                }
            }

            try {
                int type = Integer.valueOf(f.getName().substring(0, f.getName().length() - 4));

                data.setData(type, blocks);
            } catch (NumberFormatException e) {
                System.out.println("Invalid name of file " + f.getName());
            }
        }

        return data;
    }

    public static void save(File folder, LevelData data) {
        save(folder, data, false);
    }

    public static void save(File folder, LevelData data, boolean async) {
        for (Map.Entry<Integer, Set<BlockData>> entry : data.blocks.entrySet()) {
            Set<BlockData> bDatas = entry.getValue();

            if (bDatas.isEmpty()) {
                continue;
            }

            Config cfg = new Config(folder + "/" + entry.getKey().toString() + ".yml", Config.YAML);

            List<ConfigSection> sections = new ArrayList<>();

            for (BlockData bData : bDatas) {
                ConfigSection blockDataSection = new ConfigSection();
                blockDataSection.set("x", bData.getFloorX());
                blockDataSection.set("y", bData.getFloorY());
                blockDataSection.set("z", bData.getFloorZ());
                blockDataSection.set("owner", bData.owner);
                blockDataSection.set("users", bData.users);
                blockDataSection.set("public", bData.isPublic);

                if (bData.password != null) {
                    blockDataSection.set("password", bData.password);
                }

                sections.add(blockDataSection);
            }

            ConfigSection mainSection = new ConfigSection();
            mainSection.set("blocks", sections);

            cfg.setAll(mainSection);
            cfg.save(async);
        }
    }
}
