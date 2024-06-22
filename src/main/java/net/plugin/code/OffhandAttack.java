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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class OffhandAttack extends JavaPlugin {

    private static OffhandAttack instance;

    private static OffhandAttack plugin;

    public static OffhandAttack getInstance() {
        return instance;
    }
    

    private boolean hasProtocolLib() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("ProtocolLib");
        return plugin != null && plugin.isEnabled();
    }
    
    @Override
    public void onEnable() {
    	instance = this;
    	if (hasProtocolLib()) {
    		File file_treski = new File(getDataFolder(), "default_config.yml");
            if (!file_treski.exists()) {
            	try {
					file_treski.createNewFile();
				} catch (IOException e) {
					getLogger().log(Level.SEVERE, "ERROR: Failed to create file", e);
				}
                Map<String, Object> data = new HashMap<>();
                data.put("Attack speed penalty", 30);
                data.put("Damage penalty", 30);
                data.put("Default for all players", true);
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
    		getLogger().info("OffhandAttack plugin is enabled. Enjoy the fun, my little Barbarians.");
    	} else {
    		getLogger().warning("You don't have ProtocolLib on your server! Disabling OffhandAttack plugin...");
    		Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("OffhandAttack"));

		}
    }

    @Override
    public void onDisable() {
    	getLogger().info("OffhandAttack plugin is disabled.");
    }
}