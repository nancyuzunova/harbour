import java.sql.SQLException;
import java.util.Random;

public class Demo {

    static int i = 0;

    public static void main(String[] args) {

//        try {
//            EntityDAO.getInstance().createTable();
//        } catch (SQLException e) {
//            System.out.println("Table not created!" + e.getMessage());
//        }

        while(true){
           Harbour.getInstance().addShip(new Ship(nameShip()));
        }
    }

    public static String nameShip(){
        String[] names = {"Poseidon", "Odysseus", "Victoria", "Locky", "Black Pearl", "Titanic"};
        return names[new Random().nextInt(names.length)] + ++i;
    }
}
