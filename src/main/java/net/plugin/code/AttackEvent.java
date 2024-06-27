package net.plugin.code;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Multimap;

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

public class AttackEvent extends OffhandAttack implements Listener {
	
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
			NamespacedKey key = new NamespacedKey("offhand_atk", "1");
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.has(key, PersistentDataType.STRING)) {
                return true;
            } else {
            	return false;
            }
		} else {
			return false;
		}
	}
	///// THIS CLASS DEFINES PLAYER'S GENERIC ATTACK DAMAGE, SUBTRACTS VALUE PROVIDED BY MAINHAND ITEM FROM IT AND ADDS VALUE PROVIDED BY OFFHAND ITEM /////
	private double getDmgOffhand(Player player) {
        double attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        double finaldmg = 0.0;
        if (attribute != 0) {
        	finaldmg = (attribute - getRawDmg(player.getInventory().getItemInMainHand().getItemMeta().getAttributeModifiers(EquipmentSlot.HAND)) + getRawDmg(player.getInventory().getItemInOffHand().getItemMeta().getAttributeModifiers(EquipmentSlot.HAND)));
        } else {
        	finaldmg = getRawDmg(player.getInventory().getItemInOffHand().getItemMeta().getAttributeModifiers(EquipmentSlot.HAND));
        }
		return finaldmg;
    }
	
	private double getRawDmg(Multimap<Attribute, AttributeModifier> map) {
		if (map == null) {
			return 0.0;
		} else {
			double dmg = 0.0;
			for (Attribute attr : map.keys()) {
                if (attr == Attribute.GENERIC_ATTACK_DAMAGE) {
                    for (AttributeModifier modifier : map.get(attr)) {
                    	dmg += modifier.getAmount();
                    }
                }
            }
			return dmg;
		}
	}


	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		Random random = new Random();
		Location ent_loc = entity.getLocation();
		LivingEntity ent_liv = (LivingEntity) entity;
		if (player.getCooldown(Material.KNOWLEDGE_BOOK) == 0) {
			if (weaponCheck(player.getInventory().getItemInOffHand())) {
				animateOffHand(player);
				if (invulnerableCheck(entity)) {
					float pit = 0.9f + (1.1f - 0.9f) * random.nextFloat();
					ent_loc.getWorld().playSound(ent_loc, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0f, pit);
				} else {
					ent_liv.damage(getDmgOffhand(player), player);
				}
			}
		}
	}
}
