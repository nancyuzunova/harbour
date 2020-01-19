import java.io.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Harbour {

    public static final int NUMBER_OF_DOCS = 5;
    public static final int NUMBER_OF_WAREHOUSES = 2;
    private static Harbour mInstance;

    private BlockingQueue<Ship>[] docks;
    private BlockingQueue<Package>[] warehouses;
    private Map<Integer, Entity> diary;
    private Reporter reporter;

    class Reporter extends Thread{

        File file1 = new File("statistics1.txt");
        File file2 = new File("statistics2.txt");
        File file3 = new File("statistics3.txt");
        File file4 = new File("statistics4.txt");

        @Override
        public void run() {
            while(true){
                try{
                    Thread.sleep(24*100);//24 hours
                    printFirstStatistics();
                    printSecondStatistics();
                    printThirdStatistics();
                    printLastStatistics();
                } catch (InterruptedException e) {
                    System.out.println("Reporter interrupted!");
                }
            }
        }

        private void printLastStatistics() {
            try(PrintWriter pr = new PrintWriter(new FileOutputStream(file4, true))) {
                String boat = StatisticsDAO.getInstance().getShipWithMostPackages();
                pr.println("The winner is " + boat);
            } catch (SQLException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void printThirdStatistics() {
            try(PrintWriter pr = new PrintWriter(new FileOutputStream(file3, true))) {
                Map<String, Integer> map = StatisticsDAO.getInstance().getPackagesByCrane();
                for(Map.Entry<String, Integer> e : map.entrySet()){
                    pr.println(e.getKey() + " : " + e.getValue() + " packages");
                }
            } catch (SQLException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void printFirstStatistics() {
            try(PrintWriter pr = new PrintWriter(new FileOutputStream(file1, true))) {
                pr.println("================STATISTICS 1===============");
                TreeMap<Integer, TreeSet<Entity>> result = StatisticsDAO.getInstance().getAllUnloadedPackages();
                for(Map.Entry<Integer, TreeSet<Entity>> entry : result.entrySet()){
                    pr.println("Dock " + entry.getKey());
                    for(Entity e : entry.getValue()){
                        pr.println("\t" + e.getPackageID() + ", " + e.getShipName() + ", " + e.getCrane() + ", " + e.getUnloadDate());
                    }
                }
            } catch (FileNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        private void printSecondStatistics(){
            try(PrintWriter pr = new PrintWriter(new FileOutputStream(file2, true))) {
                Map<Integer, Integer> map = StatisticsDAO.getInstance().getNumberOfShips();
                for(Map.Entry<Integer, Integer> e : map.entrySet()){
                    pr.println("Dock " + e.getKey() + " : " + e.getValue() + " ships");
                }
            } catch (SQLException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Harbour() {
        this.docks = new BlockingQueue[NUMBER_OF_DOCS];
        this.warehouses = new BlockingQueue[NUMBER_OF_WAREHOUSES];
        for (int i = 0; i <NUMBER_OF_DOCS; i++) {
            docks[i] = new ArrayBlockingQueue<>(5);
        }
        for (int i = 0; i < NUMBER_OF_WAREHOUSES; i++) {
            this.warehouses[i] = new ArrayBlockingQueue<>(10);
        }
        this.diary = new HashMap<>();
        Crane crane1 = new Crane("Crane1");
        Crane crane2 = new Crane("Crane2");
        crane1.start();
        crane2.start();
        Distributor distributor1 = new Distributor(0);
        Distributor distributor2 = new Distributor(1);
        distributor1.start();
        distributor2.start();
        reporter = new Reporter();
        reporter.setDaemon(true);
        reporter.start();
    }

    public synchronized static Harbour getInstance() {
        if (mInstance == null) {
            mInstance = new Harbour();
        }
        return mInstance;
    }

    public void addShip(Ship ship){
        int dockNumber = new Random().nextInt(this.docks.length);
        try {
            this.docks[dockNumber].put(ship);
            System.out.println(ship.getName() + " entered the dock.");
        } catch (InterruptedException e) {
            System.out.println("Ship not in dock! Error happened! " + e.getMessage());
        }
    }

    //invoked by Crane
    public void addPackage(Package p) throws InterruptedException {
        if(new Random().nextBoolean()){
            this.warehouses[0].put(p);
        }
        else{
            this.warehouses[1].put(p);
        }
        System.out.println("Package " + p.getID() + " was unloaded.");
    }

    public void unloadShip() {
        try {
            //find dock with ship
            Ship ship = getShipToUnload();
            if(ship != null) {
                //get packages from the ship
                List<Package> packages = ship.getPackages();
                Thread.sleep(packages.size()*2000);
                for (Package p : packages) {
                    //put packages into warehouse
                    addPackage(p);
                    Entity entity = new Entity();
                    entity.setCrane(Thread.currentThread().getName());
                    entity.setPackageID(p.getID());
                    entity.setShipName(ship.getName());
                    entity.setUnloadDate(LocalDateTime.now());
                    entity.setDockID(ship.getDockID());
                    this.diary.put(entity.getId(), entity);
                    EntityDAO.getInstance().addEntity(entity);
                    System.out.println(Thread.currentThread().getName() + " unloaded a ship!");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Unloading ship failed! " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Adding entity failed");
        }
    }

    public void takePackageFromWarehouse(int warehouse) throws InterruptedException {
        Thread.sleep(5000);
        this.warehouses[warehouse].take();
        System.out.println("Distributor took out packages from warehouse " + warehouse);
    }

    private Ship getShipToUnload() throws InterruptedException {
        for (int i = 0; i < NUMBER_OF_DOCS; i++) {
            if(!docks[i].isEmpty()){
                Ship ship = docks[i].take();
                ship.setDockId(i);
                return ship;
            }
        }
        return null;
    }
}
