package de.maxi.multihomes.home;

import de.maxi.multihomes.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeUtil {

    private Main plugin;
    private Player player;

    public HomeUtil(Main plugin, Player player){
        this.plugin = plugin;
        this.player = player;
    }

    public void addHome(Home home){
        List<Home> list = plugin.getHomes().get(player);
        list.add(home);
        plugin.getHomes().replace(player, list);
    }

    public void removeHome(Home home){
        List<Home> list = plugin.getHomes().get(player);
        list.remove(home);
        plugin.getHomes().replace(player, list);
    }

    public void updateHomeLocation(Home home, Location location){
        removeHome(home);
        home.setLocation(location);
        addHome(home);
    }

    public List<String> getHomeNames() {
        List<String> list = new ArrayList<>();
        for(Home h : plugin.getHomes().get(player)){
            list.add(h.getName());
        }
        return list;
    }

    public Home getHome(String name){
        for(Home h : plugin.getHomes().get(player)){
            if(h.getName().equalsIgnoreCase(name)) return h;
        }
        return null;
    }

    public void tpHome(Home home){
        player.teleport(home.getLocation());
    }

    public Home getDefaultHome(){
        return plugin.getDefaultHomes().get(player);
    }

    public void setDefaultHome(Home home){
        plugin.getDefaultHomes().replace(player, home);
    }

    public boolean exists(Home home){
        return plugin.getHomes().get(player).contains(home);
    }
}
