public class Crane extends Thread {

    public Crane(String name){
        super(name);
    }

    @Override
    public void run() {
        while(true){
            Harbor.getInstance().unloadShip();
        }
    }
}
