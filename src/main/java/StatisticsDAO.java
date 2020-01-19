import java.sql.*;
import java.util.*;

public class StatisticsDAO {

    private static StatisticsDAO mInstance;

    private StatisticsDAO() {
    }

    public synchronized static StatisticsDAO getInstance() {
        if (mInstance == null) {
            mInstance = new StatisticsDAO();
        }
        return mInstance;
    }

    public TreeMap<Integer, TreeSet<Entity>> getAllUnloadedPackages() throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        TreeMap<Integer, TreeSet<Entity>> map = new TreeMap<>();
        String sql = "SELECT dock_id, package_id, boat_name, crane_id, unloading_time FROM port_shipments;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        while(result.next()) {
            Entity entity = new Entity();
            entity.setDockID(result.getInt("dock_id"));
            entity.setCrane(result.getString("crane_id"));
            entity.setPackageID(result.getInt("package_id"));
            entity.setShipName(result.getString("boat_name"));
            entity.setUnloadDate(result.getTimestamp("unloading_time").toLocalDateTime());
            int dock = result.getInt("dock_id");
            if(!map.containsKey(dock)){
                map.put(dock, new TreeSet<Entity>((e1, e2) -> e1.getUnloadDate().compareTo(e2.getUnloadDate())));
            }
            map.get(dock).add(entity);
        }
        return map;
    }

    public Map<Integer, Integer> getNumberOfShips() throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        String sql = "SELECT dock_id, COUNT(*) AS total FROM port_shipments GROUP BY dock_id ORDER BY dock_id;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        Map<Integer, Integer> map = new HashMap<>();
        while(result.next()){
            int dock = result.getInt("dock_id");
            map.put(dock, result.getInt("total"));
        }
        statement.close();
        return map;
    }

    public Map<String, Integer> getPackagesByCrane() throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        String sql = "SELECT crane_id, COUNT(package_id) AS total FROM port_shipments GROUP BY crane_id;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        Map<String, Integer> map = new HashMap<>();
        while(result.next()){
            map.put(result.getString("crane_id"), result.getInt("total"));
        }
        statement.close();
        return map;
    }

    public String getShipWithMostPackages() throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        String sql = "SELECT boat_name, COUNT(package_id) AS total FROM port_shipments GROUP BY boat_name ORDER BY total DESC LIMIT 1;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        String winner = "";
        while(result.next()){
            winner = result.getString("boat_name");
        }
        return winner;
    }
}
