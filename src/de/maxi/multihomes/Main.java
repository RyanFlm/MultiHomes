package de.maxi.multihomes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.maxi.multihomes.commands.TabComplete_Home;
import de.maxi.multihomes.events.Player_Join;
import de.maxi.multihomes.commands.Cmd_Home;
import de.maxi.multihomes.events.Player_Quit;
import de.maxi.multihomes.home.Home;
import de.maxi.multihomes.other.SQLConnector;
import de.maxi.multihomes.other.SQLUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private YamlConfiguration config;
	private File cfgFile;
	private YamlConfiguration msgConfig;
	private File msgFile;
	private SQLConnector sqlCon;
	private Connection con;

	private HashMap<Player, Home> defaultHomes;
	private HashMap<Player, List<Home>> homes;
	
	@Override
	public void onEnable() {
		//load all files and data
		if(!getDataFolder().exists()) this.getDataFolder().mkdir();
		loadCfg();
		loadMsg();
		loadHomeDB();
		if(sqlCon.isConnected()) initHomeDB();

		this.defaultHomes = new HashMap<>();
		this.homes = new HashMap<>();

		for(Player p : Bukkit.getOnlinePlayers()){
			loadALLHomes(p);
		}

		//registry for commands
		this.getCommand("home").setExecutor(new Cmd_Home(this));
		this.getCommand("home").setTabCompleter(new TabComplete_Home(this));
			
		//registry for events
		Bukkit.getServer().getPluginManager().registerEvents(new Player_Join(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Player_Quit(this), this);

		//initial plugin metrics
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("savemethod", () -> config.getString("save-method")));

		super.onEnable();
	}
	
	
	@Override
	public void onDisable(){

		for(Player p : Bukkit.getOnlinePlayers()){
			saveAllHomes(p);
		}

		if(sqlCon.isConnected()) sqlCon.disconnect();

		super.onDisable();
	}

	public File getCfgFile(){
		return cfgFile;
	}
	public YamlConfiguration getCfg(){
		return config;
	}
	public File getMsgFile(){
		return msgFile;
	}
	public YamlConfiguration getMsgCfg(){
		return msgConfig;
	}

	public void loadCfg(){
		cfgFile = checkFile(getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(cfgFile);
	}
	public void loadMsg(){
		List<String> languages = new ArrayList<>();
		languages.add("en");
		String lang = config.getString("lang");

		if(languages.contains(lang)){
			msgFile = checkFile(getDataFolder(), "lang/" + lang + ".yml");
		}else{
			msgFile = new File(getDataFolder(), "lang/" + config.getString("lang") + ".yml");
		}
		msgConfig = YamlConfiguration.loadConfiguration(msgFile);
	}
	public void loadHomeDB(){
		String saveMethod = config.getString("save-method");
		this.sqlCon = new SQLConnector(this);

		if(saveMethod.equalsIgnoreCase("SQLite")){
			this.sqlCon.initSQLite(getDataFolder().getAbsolutePath() + "/homes.db");
		}else if(saveMethod.equalsIgnoreCase("MySQL")){
			String host = config.getString("mysql.host");
			int port = config.getInt("mysql.port");
			String database = config.getString("mysql.database");
			String username = config.getString("mysql.username");
			String password = config.getString("mysql.password");
			this.sqlCon.initMySQL(host, port, database, username, password);
		}

		this.con = sqlCon.getConnection();
	}
	public void initHomeDB(){
		try {
			PreparedStatement ps1 = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS homes_users (" +
							"id INTEGER PRIMARY KEY, " +
							"uuid TEXT NOT NULL, " +
							"name TEXT NOT NULL)");
			ps1.executeUpdate();
			PreparedStatement ps2 = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS homes_homes (" +
							"id INTEGER PRIMARY KEY, " +
							"owner_id INTEGER NOT NULL, " +
							"name TEXT NOT NULL, " +
							"x DOUBLE NOT NULL, " +
							"y DOUBLE NOT NULL, " +
							"z DOUBLE NOT NULL, " +
							"yaw FLOAT NOT NULL, " +
							"pitch FLOAT NOT NULL, " +
							"world TEXT NOT NULL)");
			ps2.executeUpdate();
			PreparedStatement ps3 = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS homes_default (" +
							"player_id INTEGER NOT NULL UNIQUE, " +
							"home_id INTEGER NOT NULL UNIQUE)");
			ps3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public SQLConnector getSqlCon(){
		return sqlCon;
	}
	public HashMap<Player, List<Home>> getHomes(){
	    return homes;
    }
    public HashMap<Player, Home> getDefaultHomes(){
	    return defaultHomes;
    }

	private File checkFile(File folder, String child){
		File file = new File(folder, child);
		if(!file.exists()){
			saveResource(child, false);
		}
		return file;
	}

	public void reloadAllHomes(Player player){
		saveAllHomes(player);
		loadALLHomes(player);
	}

	public void saveAllHomes(Player player){
		SQLUtil su = new SQLUtil(this);

		su.removeALLHomes(player);
		for(Home h : getHomes().remove(player)) {
			su.insertHome(player, h);
		}

		if(su.isDefaultHomeSet(player)){
			su.updateDefaultHome(player, getDefaultHomes().remove(player));
		}else {
			su.insertDefaultHome(player, getDefaultHomes().remove(player));
		}
	}

	public void loadALLHomes(Player player){
		SQLUtil su = new SQLUtil(this);

		if(su.isPlayerSet(player)) {
			su.updatePlayer(player);
		}else{
			su.insertPlayer(player);
		}

		getHomes().put(player, su.getHomeList(player));
		getDefaultHomes().put(player, su.getHome(su.getDefaultHomeID(player)));
	}
}