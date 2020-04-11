package de.maxi.multihomes.other;

import de.maxi.multihomes.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnector {

    private YamlConfiguration msgConfig;

    private Connection con;

    public SQLConnector(Main plugin){
        this.msgConfig = plugin.getMsgCfg();
        this.con = null;
    }

    public void initMySQL(String host, int port, String database, String username, String password){
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            this.con = DriverManager.getConnection(url, username, password);
            Class.forName(driver);
            System.out.println(msgConfig.getString("sql.connect"));
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(msgConfig.getString("sql.error"));
            e.printStackTrace();
        }
    }

    public void initSQLite(String dbPath){
        String url = "jdbc:sqlite:" + dbPath;
        try{
            this.con = DriverManager.getConnection(url);
            System.out.println(msgConfig.getString("sql.connect"));
        }catch(SQLException e){
            System.err.println(msgConfig.getString("sql.error"));
            e.printStackTrace();
        }
    }

    public void disconnect(){
        if (isConnected()) {
            try {
                this.con.close();
                System.out.println(msgConfig.getString("sql.disconnect"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return this.con != null;
    }

    public Connection getConnection() {
        return this.con;
    }

}
