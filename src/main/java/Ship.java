import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ship {

    private ArrayList<Package> packages;
    private String name;
    private int dockID;

    public Ship(String name){
        this.name = name;
        this.packages = new ArrayList<>();
        int numberOfPackages = new Random().nextInt(4) + 1;
        for (int i = 0; i < numberOfPackages; i++) {
            this.packages.add(new Package());
        }
    }

    public List<Package> getPackages() {
        List<Package> packages = new ArrayList<>(this.packages);
        this.packages.clear();
        return packages;
    }

    public String getName() {
        return name;
    }

    public void setDockId(int dockId) {
        this.dockID = dockId;
    }

    public int getDockID() {
        return dockID;
    }
}
