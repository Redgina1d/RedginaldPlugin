package net.plugin.code;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Multimap;

import java.awt.print.Printable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import org.bukkit.NamespacedKey;

public class AttackEvent implements Listener {
	
	public static final NamespacedKey DMG_MELEE_PHYSICAL = new NamespacedKey(OffhandAttack.instance, "dmg_mel_phy");
	public static final NamespacedKey DMG_MELEE_MAGICAL = new NamespacedKey(OffhandAttack.instance, "dmg_mel_mgc");
	public static final NamespacedKey DMG_MELEE_POISON = new NamespacedKey(OffhandAttack.instance, "dmg_mel_psn");
	public static final NamespacedKey DMG_MELEE_FIRE = new NamespacedKey(OffhandAttack.instance, "dmg_mel_fir");
	public static final NamespacedKey DMG_MELEE_ICE = new NamespacedKey(OffhandAttack.instance, "dmg_mel_ice");
	public static final NamespacedKey DMG_MELEE_LIGHT = new NamespacedKey(OffhandAttack.instance, "dmg_mel_lig");
	public static final NamespacedKey DMG_MELEE_PURE = new NamespacedKey(OffhandAttack.instance, "dmg_mel_pur");

	public static final NamespacedKey ATK_CD = new NamespacedKey(OffhandAttack.instance, "atk_cd");
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		Random random = new Random();
		Location ent_loc = entity.getLocation();
		LivingEntity ent_liv = (LivingEntity) entity;
		if (player.getCooldown(Material.KNOWLEDGE_BOOK) == 0) {
			animateOffHand(player);
			if (weaponCheck(player.getInventory().getItemInOffHand())) {
				if (invulnerableCheck(entity)) {
					float pit = 0.9f + (1.1f - 0.9f) * random.nextFloat();
					ent_loc.getWorld().playSound(ent_loc, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0f, pit);
				} else {
					player.setCooldown(Material.KNOWLEDGE_BOOK, getCdOffhand(player));
					for (int i = 0; i <= getDmgOffhand(player).length; i++ ) {
						if (getDmgOffhand(player)[i] != 0) {
							
						}
					}
					ent_liv.damage(getDmgOffhand(player), player);
					player.sendMessage("Damage dealt: " + Double.toString(getDmgOffhand(player)));
					player.sendMessage("Cool Dawn: " + getCdOffhand(player));
				}
			}
		}
	}

	 public void animateOffHand(Player player) {
	        try {
	            PacketContainer packet = OffhandAttack.protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
	            packet.getIntegers().write(0, player.getEntityId());
	            packet.getIntegers().write(1, 3); // 3 is code for second hand swing animation.
	            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
	            	OffhandAttack.protocolManager.sendServerPacket(onlinePlayer, packet);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	private boolean invulnerableCheck(Entity entity) {
		if (entity instanceof Player) {
			if (((Player) entity).getGameMode() == GameMode.CREATIVE && ((Player) entity).getGameMode() == GameMode.SPECTATOR) {
				return true;
			} else {
				return false;
			}
		} else if (entity.isInvulnerable()) {
			return true;
		} else {
			return false;
		}
	}
	private boolean weaponCheck(ItemStack item) {
		if (item.getType() != Material.AIR && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			NamespacedKey key = new NamespacedKey(OffhandAttack.instance, "offhand_atk");
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.has(key)) {
                return true;
            } else {
            	return false;
            }
		} else {
			return false;
		}
	}
	private double[] getDmgOffhand(Player player) {
		double[] finaldmg = new double[7];
		ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
		if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            finaldmg[0] = data.get(DMG_MELEE_PHYSICAL, PersistentDataType.DOUBLE);
            finaldmg[1] = data.get(DMG_MELEE_MAGICAL, PersistentDataType.DOUBLE);
            finaldmg[2] = data.get(DMG_MELEE_POISON, PersistentDataType.DOUBLE);
            finaldmg[3] = data.get(DMG_MELEE_FIRE, PersistentDataType.DOUBLE);
            finaldmg[4] = data.get(DMG_MELEE_ICE, PersistentDataType.DOUBLE);
            finaldmg[5] = data.get(DMG_MELEE_LIGHT, PersistentDataType.DOUBLE);
            finaldmg[6] = data.get(DMG_MELEE_PURE, PersistentDataType.DOUBLE);
		}
		return finaldmg;
    }

	private boolean isCrit(Player player) {
		double loc1 = player.getLocation().getY();
		try {
			Thread.sleep(200);
		} catch (InterruptedException  e) {
		}
		double loc2 = player.getLocation().getY();
		if (loc1 > loc2) {
			return true;
		} else {
			return false;
		}
	}
	
	private int getCdOffhand(Player player) {
		double atr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue();
		int cd = 0;
		ItemMeta meta = player.getInventory().getItemInOffHand().getItemMeta();
		if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            double d = data.get(ATK_CD, PersistentDataType.DOUBLE);
            cd = (int) ((int) (1 / atr) + (int) (1 / d) * 10);
		}
		return cd;
	} 
	private int[] getGeneralYmlData() {
		OffhandAttack plugin = (OffhandAttack) Bukkit.getPluginManager().getPlugin("OffhandAttack");

		File def_config = new File(plugin.getDataFolder(), "default_config.yml");
		Yaml yml = new Yaml();
		int[] arr = new int[2];
		try (InputStream inputStream = new FileInputStream(def_config)) {
			 Map<String, Object> data = yml.load(inputStream);
			 Object a = data.get("Attack speed penalty (INTEGER)");
			 Object d = data.get("Damage penalty (INTEGER)");
			 Object def = data.get("Default for all players (1 for true, 0 for false)");
			 arr[0] = (int) a;
			 arr[1] = (int) d;
			 arr[2] = (int) def;
		} catch (Exception e) {
		}
		return arr;
	}
	private int[] getPlayerYmlData(Player player) {
		OffhandAttack plugin = (OffhandAttack) Bukkit.getPluginManager().getPlugin("OffhandAttack");
		
		File def_config = new File(plugin.getDataFolder(), "default_config.yml");
		Yaml yml = new Yaml();
		int[] arr = new int[2];
		try (InputStream inputStream = new FileInputStream(def_config)) {
			 Map<String, Object> data = yml.load(inputStream);
			 Object pen_s = data.get("Attack speed penalty (INTEGER)");
			 Object pen_d = data.get("Damage penalty (INTEGER)");
			 Object def = data.get("Default for all players (1 for true, 0 for false)");
			 arr[0] = (int) pen_s;
			 arr[1] = (int) pen_d;
			 arr[2] = (int) def;
		} catch (Exception e) {
		}
		return arr;
	}
	private double getRawSpeed(Multimap<Attribute, AttributeModifier> map) {
		if (map == null) {
			return 0.0;
		} else {
			double speed = 0.0;
			for (Attribute attr : map.keys()) {
                if (attr == Attribute.GENERIC_ATTACK_SPEED) {
                    for (AttributeModifier modifier : map.get(attr)) {
                    	speed += modifier.getAmount();
                    }
                }
            }
			return speed;
		}
	}
}
