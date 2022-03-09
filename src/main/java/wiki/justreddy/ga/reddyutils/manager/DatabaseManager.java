package wiki.justreddy.ga.reddyutils.manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.reddyutils.exceptions.MoreThenOneDatabaseException;

import java.sql.*;

public class DatabaseManager {

    private Connection con;

    private int connections = 0;

    public void connectMysQL(String database, String username, String password, String host, int port) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            connections +=1;
            if(connections > 1){
                throw new MoreThenOneDatabaseException(connections);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void connectH2(JavaPlugin plugin, String storagePath) {
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:" + plugin.getDataFolder().getAbsolutePath() + "/" + storagePath);
            connections +=1;
            if(connections > 1){
                throw new MoreThenOneDatabaseException(connections);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private MongoDatabase database;
    public void connectMongoDB(String URI, String db){
        MongoClientURI connectURI = new MongoClientURI(URI);
        MongoClient mongoClient = new MongoClient(connectURI);
        database = mongoClient.getDatabase(db);
        connections+=1;
        if(connections > 1){
            throw new MoreThenOneDatabaseException(connections);
        }
    }

    public MongoCollection<Document> getCollection(String collection) {
        return database.getCollection(collection);
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isConnected() {
        return con != null;
    }


    public void update(String qry) {
        try {
            getConnection().createStatement().executeUpdate(qry);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public ResultSet getResult(String qry) {
        try {
            return getConnection().createStatement().executeQuery(qry);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public PreparedStatement prepareStatement(String qry) {

        try{
            getConnection().prepareStatement(qry);
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return null;
    }


    public Connection getConnection() {
        return con;
    }
}
