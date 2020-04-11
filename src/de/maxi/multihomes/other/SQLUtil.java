package de.maxi.multihomes.other;

import de.maxi.multihomes.Main;
import de.maxi.multihomes.home.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLUtil {

    private Main plugin;
    private Connection con;

    public SQLUtil(Main plugin){
        this.plugin = plugin;
        this.con = plugin.getSqlCon().getConnection();
    }

    public void insertPlayer(Player player){
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO homes_users(uuid, name) VALUES(?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePlayer(Player player){
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE homes_users SET name = ? WHERE uuid = ?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerSet(Player player){
        UUID uuid = player.getUniqueId();
        try{
            PreparedStatement ps = con.prepareStatement("SELECT * FROM homes_users WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int getPlayerID(Player p){
        UUID uuid = p.getUniqueId();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM homes_users WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("id");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Player getPlayer(int player_id){
        try {
            PreparedStatement ps = con.prepareStatement("SELECT uuid FROM homes_users WHERE id = ?");
            ps.setInt(1, player_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                return Bukkit.getPlayer(uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertHome(Player player, Home home){
        int owner_id = getPlayerID(player);
        Location location = home.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        String world = location.getWorld().getName();
        String name = home.getName();
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO homes_homes(owner_id, name, x, y, z, yaw, pitch, world) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, owner_id);
            ps.setString(2, name);
            ps.setDouble(3, x);
            ps.setDouble(4, y);
            ps.setDouble(5, z);
            ps.setFloat(6, yaw);
            ps.setFloat(7, pitch);
            ps.setString(8, world);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void removeALLHomes(Player player){
        int owner_id = getPlayerID(player);
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM homes_homes WHERE owner_id = ?");
            ps.setInt(1, owner_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getHomeID(Player player, Home home){
        int owner_id = getPlayerID(player);
        String name = home.getName();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM homes_homes WHERE owner_id = ? AND name = ?");
            ps.setInt(1, owner_id);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Home getHome(int home_id){
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM homes_homes WHERE id = ?");
            ps.setInt(1, home_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Player p = getPlayer(rs.getInt("owner_id"));
                Location loc = p.getLocation();
                loc.setX(rs.getDouble("x"));
                loc.setY(rs.getDouble("y"));
                loc.setZ(rs.getDouble("z"));
                loc.setYaw(rs.getFloat("yaw"));
                loc.setPitch(rs.getFloat("pitch"));
                loc.setWorld(Bukkit.getWorld(rs.getString("world")));
                String name = rs.getString("name");

                return new Home(name, loc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Home> getHomeList(Player player){
        int player_id = getPlayerID(player);
        List<Home> list = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM homes_homes WHERE owner_id = ?");
            ps.setInt(1, player_id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(getHome(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertDefaultHome(Player player, Home home){
        int player_id = getPlayerID(player);
        int home_id = getHomeID(player, home);
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO homes_default(player_id, home_id) VALUES(?, ?)");
            ps.setInt(1, player_id);
            ps.setInt(2, home_id);
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateDefaultHome(Player player, Home home){
        int player_id = getPlayerID(player);
        int home_id = getHomeID(player, home);
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE homes_default SET home_id = ? WHERE player_id = ?");
            ps.setInt(1, home_id);
            ps.setInt(2, player_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDefaultHomeSet(Player player){
        int player_id = getPlayerID(player);
        try{
            PreparedStatement ps = con.prepareStatement("SELECT * FROM homes_default WHERE player_id = ?");
            ps.setInt(1, player_id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int getDefaultHomeID(Player player){
        int player_id = getPlayerID(player);
        try {
            PreparedStatement ps = con.prepareStatement("SELECT home_id FROM homes_default WHERE player_id = ?");
            ps.setInt(1, player_id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("home_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
