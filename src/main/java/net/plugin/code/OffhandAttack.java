package net.plugin.code;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class OffhandAttack extends JavaPlugin {
	
    public static OffhandAttack instance;

    public static ProtocolManager protocolManager;
    
    
    public static OffhandAttack getInstance() {
        return instance;
    }  
    

    @Override
    public void onEnable() {
    	instance = this;
    	
    	instance.getCommand("ohatk").setExecutor(new Executor());
    	getServer().getPluginManager().registerEvents(new AttackEvent(), this);
    	TabCompleter cmpltr = getCommand("ohatk").getTabCompleter();
        if (cmpltr == null) {
        	getCommand("ohatk").setTabCompleter(new TabComplete());
        }
    	if (hasProtocolLib()) {
    		protocolManager = ProtocolLibrary.getProtocolManager();
    		File file_treski = new File(getDataFolder(), "default_config.yml");
            if (!file_treski.exists()) {
            	try {
					file_treski.createNewFile();
				} catch (IOException e) {
					getLogger().log(Level.SEVERE, "ERROR: Failed to create file", e);
				}
                Map<String, Object> data = new HashMap<>();
                data.put("Attack speed penalty (INTEGER)", 30);
                data.put("Damage penalty (INTEGER)", 30);
                data.put("Default for all players (1 for true, 0 for false)", 1);
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(options);
                try (FileWriter writer = new FileWriter(file_treski)) {
                    yaml.dump(data, writer);
                    getLogger().info("Check out the config file: " + file_treski);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File playersfile = new File(getDataFolder(), "players_config.yml");
            if (!playersfile.exists()) {
            	try {
            		playersfile.createNewFile();
				} catch (IOException e) {
					getLogger().log(Level.SEVERE, "ERROR: Failed to create file", e);
				}
            }
    		getLogger().info("OffhandAttack plugin is enabled. Enjoy the fun, my little Barbarians.");
    	} else {
    		getLogger().warning("You don't have ProtocolLib on your server! Disabling OffhandAttack plugin...");
    		Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("OffhandAttack"));

		}
    }
    
    private boolean hasProtocolLib() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("ProtocolLib");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public void onDisable() {
    	protocolManager = null;
    	getLogger().info("OffhandAttack plugin is disabled.");
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}