public class Package {

    private static int uid = 1;
    private int id;

    public int getID() {
        return id;
    }

    public Package(){
        this.id = uid++;
    }
}
