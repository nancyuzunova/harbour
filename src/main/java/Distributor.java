public class Distributor extends Thread {

    private int warehouseId;

    public Distributor(int id){
        this.warehouseId = id;
    }

    @Override
    public void run() {
        while(true){
            try {
                Harbor.getInstance().takePackageFromWarehouse(warehouseId);
            } catch (InterruptedException e) {
                System.out.println("Distributor interrupted! " + e.getMessage());
            }
        }
    }
}
