import java.time.LocalDateTime;

public class Entity {

    private static int uID = 1;

    private int id;
    private int packageID;
    private int dockID;
    private String shipName;
    private String crane;
    private LocalDateTime unloadDate;

    public Entity() {
        this.id = uID++;
    }

    public int getId() {
        return id;
    }

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public int getDockID() {
        return dockID;
    }

    public void setDockID(int dockID) {
        this.dockID = dockID;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getCrane() {
        return crane;
    }

    public void setCrane(String crane) {
        this.crane = crane;
    }

    public LocalDateTime getUnloadDate() {
        return unloadDate;
    }

    public void setUnloadDate(LocalDateTime unloadDate) {
        this.unloadDate = unloadDate;
    }
}
