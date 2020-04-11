package de.maxi.multihomes.events;

import de.maxi.multihomes.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Player_Join implements Listener {

	private Main plugin;

	public Player_Join(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void a(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		plugin.loadALLHomes(p);
	}
}
