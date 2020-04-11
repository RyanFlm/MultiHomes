package de.maxi.multihomes.commands;

import de.maxi.multihomes.Main;
import de.maxi.multihomes.home.Home;
import de.maxi.multihomes.home.HomeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Cmd_Home implements CommandExecutor{
	
	private Main plugin;
	private YamlConfiguration msgConfig;

	public Cmd_Home(Main plugin){
		this.plugin = plugin;
		this.msgConfig = plugin.getMsgCfg();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(msgConfig.getString("justPlayer"));
			return true;
		}
		
		Player p = (Player) sender;

		if(args.length <= 0 || args[0].isEmpty()){
			if(p.hasPermission("multihomes.default")){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getDefaultHome();
				if(hu.exists(h)){
					hu.tpHome(h);
					p.sendMessage(msgConfig.getString("home.teleport").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.NA").replaceAll("%home%", plugin.getCfg().getString("default-homename")));
				}
				return true;
			}
		}

		if(args[0].equalsIgnoreCase("help")){
			if(!p.hasPermission("multihomes.help")){
				p.sendMessage("noPermission");
				return true;
			}

			for(String line : msgConfig.getStringList("help")){
				p.sendMessage(line);
			}
			return true;
		}

		if(args[0].equalsIgnoreCase("list")){
			if(!p.hasPermission("multihomes.list")){
				p.sendMessage("noPermission");
				return true;
			}

			List<String> list = new HomeUtil(plugin, p).getHomeNames();
			String homes = "";
			for(String h : list){
				homes += h;
				if(list.indexOf(h) < list.size()-1){
					homes += "' ";
				}
			}
			String msg = msgConfig.getString("list").replaceAll("%amount%", String.valueOf(list.size()));
			p.sendMessage(msg.replaceAll("%homes%", homes));
			return true;
		}

		if(args[0].equalsIgnoreCase("tp")){
			if(!p.hasPermission("multihomes.tp")){
				p.sendMessage(msgConfig.getString("noPermission"));
				return true;
			}

			if(args.length == 2){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getHome(args[1]);
				if(hu.exists(h)) {
					hu.tpHome(h);
					p.sendMessage(msgConfig.getString("home.teleport").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.NA").replaceAll("%home%", args[1]));
				}
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("set")){
			if(!p.hasPermission("multihomes.set")){
				p.sendMessage(msgConfig.getString("noPermission"));
				return true;
			}

			if(args.length == 1){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getDefaultHome();
				if(!hu.exists(h)){
					Home home = new Home(plugin.getCfg().getString("default-homename"), p.getLocation());
					hu.addHome(home);
					hu.setDefaultHome(home);
					p.sendMessage(msgConfig.getString("home.create").replaceAll("%home%", home.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.isSet").replaceAll("%home%", plugin.getCfg().getString("default-homename")));
				}
				return true;
			}

			if(args.length == 2){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = new Home(args[1], p.getLocation());
				if(!hu.exists(h)) {
					hu.addHome(h);
					p.sendMessage(msgConfig.getString("home.create").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.isSet").replaceAll("%home%", args[1]));
				}
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("delete")){
			if(!p.hasPermission("multihomes.delete")){
				p.sendMessage(msgConfig.getString("noPermission"));
				return true;
			}

			if(args.length == 2){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getHome(args[1]);
				if(hu.exists(h)){
					hu.removeHome(h);
					p.sendMessage(msgConfig.getString("home.delete").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.NA").replaceAll("%home%", args[1]));
				}
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("relocate")){
			if(!p.hasPermission("multihomes.relocate")){
				p.sendMessage("noPermission");
				return true;
			}

			if(args.length == 2){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getHome(args[1]);
				if(hu.exists(h)) {
					hu.updateHomeLocation(h, p.getLocation());
					p.sendMessage(msgConfig.getString("home.relocate").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.NA"));
				}
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("default")){
			if(!p.hasPermission("multihomes.default")){
				p.sendMessage(msgConfig.getString("noPermission"));
				return true;
			}

			if(args.length == 1){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getDefaultHome();
				if(hu.exists(h)) {
					hu.tpHome(h);
					p.sendMessage(msgConfig.getString("home.teleport").replaceAll("%home%", h.getName()));
				}else{
					p.sendMessage(msgConfig.getString("home.NA").replaceAll("%home%", plugin.getCfg().getString("default-homename")));
				}
				return true;
			}
			if(args.length == 2){
				HomeUtil hu = new HomeUtil(plugin, p);
				Home h = hu.getHome(args[1]);
				if(hu.exists(h)){
					hu.setDefaultHome(h);
					p.sendMessage(msgConfig.getString("home.default").replaceAll("%home%", args[1]));
				}else{
					p.sendMessage(msgConfig.getString("home.NA").replaceAll("%home%", args[1]));
				}
				return true;
			}
		}


		if(args[0].equalsIgnoreCase("version")){

		}

		p.sendMessage(msgConfig.getString("unknownCommand"));
		return true;
	}
	
}
