package net.plugins.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.World;

public class AttackEvent implements Listener {

	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		Random random = new Random();
		Location ent_loc = entity.getLocation();
		World wow = player.getWorld();
		LivingEntity ent_liv = (LivingEntity) entity;
		ItemStack item = player.getInventory().getItemInOffHand();
		float pit = 0.9f + (1.1f - 0.9f) * random.nextFloat();
		if (player.getCooldown(Material.KNOWLEDGE_BOOK) == 0) {
			if (weaponCheck(item)) {
				animateOffHand(player);
				if (invulnerableCheck(entity)) {
					wow.playSound(ent_loc.add(0, 1.2, 0), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0f, pit);
				} else {
					player.setCooldown(Material.KNOWLEDGE_BOOK, getCdOffhand(item));
					if (getDmgOffhand(item) != 0) {
						if (getAtkType(player) == 0) {
							ent_liv.damage(getDmgOffhand(item), player);
							wow.spawnParticle(Particle.SWEEP_ATTACK, ent_loc.add(0, 1.2, 0), 1, 0.05, 0.05, 0.05);
							wow.playSound(ent_loc.add(0, 1.2, 0), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, pit);
							List<LivingEntity> nearbyMobs = wow.getNearbyEntities(ent_loc, 1.0, 0.25, 1.0)
				                    .stream()
				                    .filter(near -> near instanceof LivingEntity)
				                    .filter(near -> !near.getName().equals(player.getName()))
				                    .map(near -> (LivingEntity) near)
				                    .collect(Collectors.toList());
							for (LivingEntity nearby : nearbyMobs) {
								nearby.damage(1.0, player);
							}
						} else if (getAtkType(player) == 2) {
							ent_liv.damage((getDmgOffhand(item) * 1.5), player);
							wow.spawnParticle(Particle.CRIT, ent_loc.add(0, 1.2, 0), 10, 0.6, 0.6, 0.6);
							wow.playSound(ent_loc.add(0, 1.2, 0), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, pit);
						} else if (getAtkType(player) == 1) {
							Vector dir = player.getLocation().getDirection().normalize().multiply(1.1);
							ent_liv.damage(getDmgOffhand(item), player);
							ent_liv.setVelocity(dir);
							wow.playSound(ent_loc.add(0, 1.2, 0), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, pit);
						} else if (getAtkType(player) == 3) {
							ent_liv.damage(getDmgOffhand(item), player);
						}
					}
				}
			}
		}
	}

	public void animateOffHand(Player player) {
		 PacketContainer packet = RedginaldPlugin.protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
		 packet.getIntegers().write(0, player.getEntityId());
		 packet.getIntegers().write(1, 3); // 3 is code for second hand swing animation.
		 for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			 RedginaldPlugin.protocolManager.sendServerPacket(onlinePlayer, packet);
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
			NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
			if (compound.containsKey("offhand_atk")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private double getDmgOffhand(ItemStack item) {
		NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
		if (compound.containsKey("offhand_atk")) {
			return compound.getDouble("offhand_atk");
		} else {
			return 0;	
		}
	}
	
	// 0 = sweep
	// 1 = knock
	// 2 = crit
	// 3 = common (no sweep & other effects, used for axe-like weapons
	private short getAtkType(Player player) {
		if (!player.isSprinting()) {
			double loc1 = player.getLocation().getY();
			try {
				Thread.sleep(200);
			} catch (InterruptedException  e) {}
			double loc2 = player.getLocation().getY();
				if (loc1 > loc2) {
					return 2;
				} else {
					NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(player.getInventory().getItemInMainHand()));
					if (compound.containsKey("offhand_sweep")) {
						if (compound.getShort("offhand_sweep") == 0) {
							return 3;
						}
					} else {
						return 0;
					}
				}
		} else {
			return 1;
		}
		return 5;
		
	}
	private int getCdOffhand(ItemStack item) {
		NbtCompound compound = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
		if (compound.containsKey("offhand_cd")) {
			return compound.getInteger("offhand_cd");
		} else {
			return 0;	
		}
	}
	/*
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
	*/
}
