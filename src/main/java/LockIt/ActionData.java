package LockIt;

public class ActionData {

    public static final int LOCK = 0;
    public static final int PUBLICLOCK = 1;
    public static final int UNLOCK = 2;
    public static final int PASSLOCK = 3;
    public static final int PASSWORD_REQUEST = 10;

    public static final int ADD = 5;
    public static final int REMOVE = 6;
    public static final int REMOVEALL = 7;
    public static final int PUBLIC = 8;
    public static final int PRIVATE = 9;

    public int action = -1;
    public String[] data = null;
}
