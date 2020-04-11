package de.maxi.multihomes.commands;

import java.util.ArrayList;
import java.util.List;

import de.maxi.multihomes.Main;
import de.maxi.multihomes.home.HomeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabComplete_Home implements TabCompleter {

	private Main plugin;

	public TabComplete_Home(Main plugin){
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(command.getName().equalsIgnoreCase("home")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				HomeUtil hu = new HomeUtil(plugin, p);
				
				if(args.length == 1) {
					List<String> list = new ArrayList<>();
					list.add("help");
					list.add("list");
					list.add("tp");
					list.add("set");
					list.add("delete");

					List<String> newList = new ArrayList<>();
					if(!args[0].equalsIgnoreCase("")) {
						for(String s : list) {
							if(s.toLowerCase().startsWith(args[0].toLowerCase())) {
								newList.add(s);
							}
						}
					}else {
						newList = list;
					}
					return newList;
				}
				if(args.length == 2){

				}
			}
		}
		return new ArrayList<>();
	}
}
