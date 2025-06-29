package me.avankziar.sbo.spigot;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.avankziar.sbo.general.database.YamlHandler;
import me.avankziar.sbo.general.database.YamlManager;
import me.avankziar.sbo.spigot.listener.OpenShulkerListener;
import me.avankziar.sbo.spigot.metric.Metrics;

public class SBO extends JavaPlugin
{
	public static Logger logger;
	private static SBO plugin;
	public static String pluginname = "ShulkerBoxOpen";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	
	public void onEnable()
	{
		plugin = this;
		logger = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=SBO
		logger.info(" ███████╗██████╗  ██████╗  | API-Version: "+plugin.getDescription().getAPIVersion());
		logger.info(" ██╔════╝██╔══██╗██╔═══██╗ | Author: "+plugin.getDescription().getAuthors().toString());
		logger.info(" ███████╗██████╔╝██║   ██║ | Plugin Website: "+plugin.getDescription().getWebsite());
		logger.info(" ╚════██║██╔══██╗██║   ██║ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		logger.info(" ███████║██████╔╝╚██████╔╝ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		logger.info(" ╚══════╝╚═════╝  ╚═════╝  | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		/*setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.SPIGOT, pluginname, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		} else
		{
			logger.severe("MySQL is not set in the Plugin " + pluginname + "!");
			Bukkit.getPluginManager().getPlugin(pluginname).getPluginLoader().disablePlugin(this);
			return;
		}*/
		
		setupListeners();
		//setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		yamlHandler = null;
		yamlManager = null;
		if(getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	getServer().getServicesManager().unregisterAll(plugin);
	    }
		logger.info(pluginname + " is disabled!");
		logger = null;
	}

	public static SBO getPlugin()
	{
		return plugin;
	}
	
	public static void shutdown()
	{
		SBO.getPlugin().onDisable();
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new OpenShulkerListener(), plugin);
	}
	
	
	
	public void setupBstats()
	{
		int pluginId = 0;
        new Metrics(this, pluginId);
	}
}