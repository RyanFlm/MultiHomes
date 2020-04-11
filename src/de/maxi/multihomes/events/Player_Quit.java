package de.maxi.multihomes.events;

import de.maxi.multihomes.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Player_Quit implements Listener {

    private Main plugin;

    public Player_Quit(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void a(PlayerQuitEvent e){
        Player p = e.getPlayer();
        plugin.saveAllHomes(p);
    }

}
