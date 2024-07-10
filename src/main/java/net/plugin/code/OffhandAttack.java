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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class OffhandAttack extends JavaPlugin implements CommandExecutor {

    protected static OffhandAttack instance;

    public static OffhandAttack getInstance() {
        return instance;
    }
    
    private ProtocolManager protocolManager;

    private boolean hasProtocolLib() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("ProtocolLib");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public void onEnable() {
    	instance = this;
    	instance.getCommand("ohatk").setExecutor(instance);
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

    @Override
    public void onDisable() {
    	protocolManager = null;
    	getLogger().info("OffhandAttack plugin is disabled.");
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public void animateOffHand(Player player) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
            packet.getIntegers().write(0, player.getEntityId());
            packet.getIntegers().write(1, 3); // 3 is code for second hand swing animation.
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                protocolManager.sendServerPacket(onlinePlayer, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}