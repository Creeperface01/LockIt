package LockIt;

import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class BlockData extends Vector3 {

    public String owner;
    public List<String> users;
    public boolean isPublic = false;
    public String password = null;

    public List<String> passwordUsers = new ArrayList<>();

    public BlockData() {

    }

    public BlockData(Vector3 pos, String owner) {
        this(pos, owner, new ArrayList<>());
    }

    public BlockData(Vector3 pos, String owner, List<String> users) {
        setComponents(pos.x, pos.y, pos.z);
        this.owner = owner;
        this.users = users;
    }
}
