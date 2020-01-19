import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EntityDAO {

    private static EntityDAO mInstance;

    private EntityDAO() {

    }

    public synchronized static EntityDAO getInstance() {
        if (mInstance == null) {
            mInstance = new EntityDAO();
        }
        return mInstance;
    }

    public void createTable() throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        String sql = "CREATE TABLE port_shipments(" +
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "boat_name VARCHAR(50) NOT NULL," +
                "dock_id INT," +
                "crane_id VARCHAR(10) NOT NULL," +
                "unloading_time TIMESTAMP," +
                "package_id INT);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        statement.close();
    }

    public void addEntity(Entity e) throws SQLException {
        Connection connection = DBManager.getInstance().getConnection();
        String sql = "INSERT INTO port_shipments (boat_name, dock_id, crane_id, unloading_time, package_id) " +
                "VALUES(?,?,?,?,?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, e.getShipName());
        statement.setInt(2, e.getDockID());
        statement.setString(3, e.getCrane());
        statement.setTimestamp(4, Timestamp.valueOf(e.getUnloadDate()));
        statement.setInt(5, e.getPackageID());
        statement.executeUpdate();
        statement.close();
    }
}
