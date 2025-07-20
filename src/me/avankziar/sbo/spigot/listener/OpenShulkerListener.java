package me.avankziar.sbo.spigot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import me.avankziar.sbo.spigot.SBO;

public class OpenShulkerListener implements Listener 
{

    private final Map<UUID, OpenShulkerData> openShulkerMap = new HashMap<>();
    private final Map<UUID, Long> lastOpenTimestamps = new HashMap<>();

    private boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getType() == Material.SHULKER_BOX
        		|| item.getType() == Material.BLACK_SHULKER_BOX
        		|| item.getType() == Material.BLUE_SHULKER_BOX
        		|| item.getType() == Material.BROWN_SHULKER_BOX
        		|| item.getType() == Material.CYAN_SHULKER_BOX
        		|| item.getType() == Material.GRAY_SHULKER_BOX
        		|| item.getType() == Material.GREEN_SHULKER_BOX
        		|| item.getType() == Material.LIGHT_BLUE_SHULKER_BOX
        		|| item.getType() == Material.LIGHT_GRAY_SHULKER_BOX
        		|| item.getType() == Material.LIME_SHULKER_BOX
        		|| item.getType() == Material.MAGENTA_SHULKER_BOX
        		|| item.getType() == Material.ORANGE_SHULKER_BOX
        		|| item.getType() == Material.PINK_SHULKER_BOX
                || item.getType() == Material.PURPLE_SHULKER_BOX
                || item.getType() == Material.RED_SHULKER_BOX
                || item.getType() == Material.WHITE_SHULKER_BOX
                || item.getType() == Material.YELLOW_SHULKER_BOX;
    }

    private String generateContentHash(Inventory inventory) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack item : inventory.getContents()) {
            if (item == null) sb.append("null;");
            else sb.append(item.getType()).append(":").append(item.getAmount()).append(":")
                    .append(item.getItemMeta() != null ? item.getItemMeta().hashCode() : 0).append(";");
        }
        return Integer.toHexString(sb.toString().hashCode());
    }

    private boolean hasIllegalItems(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) return true;
            // Weitere verbotene Items hier hinzufügen
        }
        return false;
    }

    private void debug(Player player, String message) {
        //player.sendMessage("§7[Debug] " + message); // REMOVE
    }
    
    public boolean onCooldown(UUID uuid)
    {
    	if(!lastOpenTimestamps.containsKey(uuid)) return false;
    	if(lastOpenTimestamps.get(uuid) > System.currentTimeMillis()) return true;
    	return false;
    }
    
    public void addCooldown(UUID uuid, long millisec)
    {
    	lastOpenTimestamps.put(uuid, System.currentTimeMillis()+millisec);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        addCooldown(event.getPlayer().getUniqueId(), 5000);
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event)
    {
        addCooldown(event.getPlayer().getUniqueId(), 3000);
    }
    
    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event)
    {
    	if(isShulkerBox(event.getPlayer().getInventory().getItemInMainHand()))
    	{
    		debug(event.getPlayer(), "InteractAtEntity MainHand"); // REMOVE
    		event.setCancelled(true);
    	}
    	if(isShulkerBox(event.getPlayer().getInventory().getItemInOffHand()))
    	{
    		debug(event.getPlayer(), "InteractAtEntity OffHand"); // REMOVE
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEntityEvent event)
    {
    	if(isShulkerBox(event.getPlayer().getInventory().getItemInMainHand()))
    	{
    		debug(event.getPlayer(), "InteractEntity MainHand"); // REMOVE
    		event.setCancelled(true);
    	}
    	if(isShulkerBox(event.getPlayer().getInventory().getItemInOffHand()))
    	{
            debug(event.getPlayer(), "InteractEntity OffHand"); // REMOVE
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.useItemInHand() == Event.Result.DENY) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isShulkerBox(item)) return;
        if (!player.hasPermission("shulkerboxopen.ininventory")) {
            debug(player, "Keine Berechtigung für Öffnen per Luftklick."); // REMOVE
            return;
        }
        RayTraceResult trace = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                5,
                0.4,  // Radius für Treffer
                e -> e != player // Ignoriere dich selbst
            );

        if (trace != null && trace.getHitEntity() != null) {
            event.setCancelled(true);
            debug(player, "Öffnen blockiert – Entity im Sichtfeld."); // REMOVE
            return;
        }

        if (onCooldown(player.getUniqueId())) 
        {
            player.sendMessage("§cBitte warte kurz, bevor du eine weitere Shulkerbox öffnest.");
            event.setCancelled(true);
            debug(player, "Zu schnelles Öffnen verhindert."); // REMOVE
            return;
        }
        addCooldown(event.getPlayer().getUniqueId(), 1000);

        event.setCancelled(true);
        openVirtualShulker(player, item.clone(), EquipmentSlot.HAND, -1);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
    	debug((Player) event.getWhoClicked(), "ClickType: " + event.getClick().name());
    	
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        
        ClickType click = event.getClick();
        ItemStack clicked = event.getCurrentItem();
        
        if(click == ClickType.SWAP_OFFHAND)
        {
        	if (!isShulkerBox(clicked))
        	{
        		debug(player, "Keine Shulker."); // REMOVE
        		return;
        	}
        	if (!player.hasPermission("shulkerboxopen.dropshulkerinventory")) {
                debug(player, "Keine Berechtigung für shulkerinventar drop."); // REMOVE
                return;
            }
        	if(event.getClickedInventory().getType() != InventoryType.PLAYER)
        	{
        		debug(player, "Drop vom Shulker nur im eigenen Inventar möglich. "+event.getClickedInventory().getType().toString()); // REMOVE
                return;
        	}
        	if (!(clicked.getItemMeta() instanceof BlockStateMeta))
    		{
        		debug(player, "Kein BlockStateMeta"); // REMOVE
        		return;
    		}
        	BlockStateMeta meta = (BlockStateMeta) clicked.getItemMeta();
            BlockState state = meta.getBlockState();
            if (!(state instanceof ShulkerBox))
        	{
            	debug(player, "Keine SkulkerBox"); // REMOVE
            	return;
        	}
            ShulkerBox shulker = (ShulkerBox) state;
            if (onCooldown(player.getUniqueId())) {
                player.sendMessage("§cBitte warte kurz, bevor du eine weitere Shulkerbox öffnest.");
                event.setCancelled(true);
                debug(player, "Zu schnelles Öffnen verhindert."); // REMOVE
                return;
            }

            event.setCancelled(true); // Kein tatsächlicher Offhand-Tausch
            event.setResult(Result.DENY);

            ItemStack shulkerItem = clicked.clone(); // WICHTIG: Clone, keine Referenz

            Inventory shulkerInv = shulker.getInventory();
            ItemStack[] contents = shulkerInv.getStorageContents();

            Location dropLoc = player.getLocation().add(0, 1, 0);

            int count = 0;
            for (ItemStack content : contents) 
            {
            	if (content == null || content.getType() == Material.AIR) continue;
                if (content != null && content.getType() != Material.AIR) count++;
                Item droppedItem = player.getWorld().dropItem(dropLoc, content.clone());
                droppedItem.setVelocity(new Vector(0, 0, 0));
                droppedItem.setOwner(player.getUniqueId());
                droppedItem.setPickupDelay(0);
                droppedItem.setTicksLived(1); // Damit er nicht direkt despawnt
                // Aufgabe: Eigentümer nach 2 Minuten entfernen
                Bukkit.getScheduler().runTaskLater(SBO.getPlugin(), () -> 
                {
                    if (!droppedItem.isDead()) {
                        droppedItem.setOwner(null); // Besitzer aufheben
                    }
                }, 2400L); // 2 Minuten = 20 Ticks * 60 Sekunden * 2
            }
            debug(player, "Shulkerbox hatte "+count+" Items.");
            
            // Jetzt das Inventar leeren und speichern
            shulkerInv.clear();
            shulker.update();
            meta.setBlockState(shulker);
            shulkerItem.setItemMeta(meta);

            // Item wieder ins Inventar zurücksetzen
            player.getInventory().setItem(event.getSlot(), shulkerItem);
            debug(player, "Shulker-Inhalt bei Offhand-Swap gedroppt."); // REMOVE
            return;
        } else
        {
        	debug(player, "Kein F-Taste"); // REMOVE
        }
        
        UUID uuid = player.getUniqueId();

        // Wenn bereits eine Shulker geöffnet ist → Öffnen blockieren
        if (openShulkerMap.containsKey(uuid)) {
        	if (isShulkerBox(event.getCurrentItem())) {
                event.setCancelled(true);
                event.setResult(Result.DENY);
                debug(player, "Es ist bereits eine Shulkerbox geöffnet."); // REMOVE
                return;
            }
        	// Kein Drop per Q oder STRG+Q erlaubt
            if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
                debug(player, "Drop während geöffneter Shulkerbox verhindert.");
                return;
            }
            
            // Shulker in Shulker per Cursor
            if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                ItemStack cursor = event.getCursor();
                if (isShulkerBox(cursor)) {
                    event.setCancelled(true);
                    debug(player, "Shulker in Shulker verhindert."); // REMOVE
                    return;
                }
            }

            return; // Wenn Shulker offen, weitere Verarbeitung blockieren
        }
        
        // Nur Shulkerklicks im Spielerinventar abfangen
        if (!isShulkerBox(clicked)) return;

        if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
            debug(player, "Klick nicht im Player-Inventar."); // REMOVE
            return;
        }

        if (!player.hasPermission("shulkerboxopen.airclick")) {
            debug(player, "Keine Berechtigung für Inventarklick."); // REMOVE
            return;
        }
        
        if(click != ClickType.SHIFT_RIGHT)
        {
        	debug(player, "Zum Shulkerbox öffnen nutze Shift-Click."); // REMOVE
            return;
        }
        
        // Keine virtuelle Shulker öffnen, wenn ein anderes Inventar (z. B. Kiste) offen ist
        if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
            debug(player, "Shulker darf nicht geöffnet werden, während ein anderes Inventar offen ist."); // REMOVE
            return;
        }
        
        if (onCooldown(player.getUniqueId())) {
            player.sendMessage("§cBitte warte kurz, bevor du eine weitere Shulkerbox öffnest.");
            event.setCancelled(true);
            debug(player, "Zu schnelles Öffnen verhindert."); // REMOVE
            return;
        }

        event.setCancelled(true);
        addCooldown(player.getUniqueId(), 1000);
        openVirtualShulker(player, clicked.clone(), null, event.getSlot());
    }

    private void openVirtualShulker(Player player, ItemStack sourceItem, EquipmentSlot handSlot, int inventorySlot) {
        if (!(sourceItem.getItemMeta() instanceof BlockStateMeta meta)) return;

        new BukkitRunnable()
		{
        	int tick = 0;
			@Override
			public void run()
			{
				if (player.getOpenInventory().getType() != InventoryType.CRAFTING) 
				{
				    player.closeInventory();
				}
				if(tick != 0 && tick % 15 == 0)
				{
					cancel();
					return;
				}
				if(tick != 0 && tick % 5 == 0)
				{
					BlockState state = meta.getBlockState();
			        if (!(state instanceof ShulkerBox shulker)) return;

			        // Inventar vorbereiten
			        Inventory virtualInv = Bukkit.createInventory(player, 27, sourceItem.getItemMeta().getDisplayName());
			        for (int i = 0; i < 27; i++) {
			            virtualInv.setItem(i, shulker.getInventory().getItem(i));
			        }

			        // Entferne das Item temporär
			        if (handSlot != null) {
			            player.getInventory().setItem(handSlot, new ItemStack(Material.AIR));
			        } else {
			            player.getInventory().setItem(inventorySlot, new ItemStack(Material.AIR));
			        }

			        // Speichere das Original + Platz
			        String hash = generateContentHash(virtualInv);
			        openShulkerMap.put(player.getUniqueId(), new OpenShulkerData(sourceItem, handSlot, inventorySlot, hash));

			        player.openInventory(virtualInv);
			        cancel();
			        return;
				}
				tick++;
			}
		}.runTaskTimer(SBO.getPlugin(), 0, 1L);
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!openShulkerMap.containsKey(uuid)) return;

        OpenShulkerData data = openShulkerMap.remove(uuid);
        Inventory newInv = event.getInventory();

        if (!(data.original().getItemMeta() instanceof BlockStateMeta meta)) return;
        BlockState state = meta.getBlockState();
        if (!(state instanceof ShulkerBox shulker)) return;

        String newHash = generateContentHash(newInv);
        if (!newHash.equals(data.hash()) && hasIllegalItems(newInv)) {
            player.sendMessage("§cIllegale Inhalte erkannt – Shulker-Inhalt wurde verworfen.");
            return;
        }

        for (int i = 0; i < 27; i++) {
            shulker.getInventory().setItem(i, newInv.getItem(i));
        }

        shulker.update();
        meta.setBlockState(shulker);
        ItemStack updatedItem = data.original().clone();
        updatedItem.setItemMeta(meta);

        // Zurücksetzen am Originalplatz
        if (data.handSlot() != null) {
            ItemStack current = player.getInventory().getItem(data.handSlot());
            if (current == null || current.getType() == Material.AIR) {
                player.getInventory().setItem(data.handSlot(), updatedItem);
            } else {
                Item item = player.getWorld().dropItem(player.getLocation(), current);
                item.setVelocity(new Vector(0, 0, 0));
                item.setOwner(player.getUniqueId());
                item.setPickupDelay(0);
                item.setTicksLived(1);
                player.getInventory().setItem(data.handSlot(), updatedItem);
            }
        } else {
            ItemStack current = player.getInventory().getItem(data.inventorySlot());
            if (current == null || current.getType() == Material.AIR) {
                player.getInventory().setItem(data.inventorySlot(), updatedItem);
            } else {
            	Item item = player.getWorld().dropItem(player.getLocation(), current);
                item.setVelocity(new Vector(0, 0, 0));
                item.setOwner(player.getUniqueId());
                item.setPickupDelay(0);
                item.setTicksLived(1);
                player.getInventory().setItem(data.inventorySlot(), updatedItem);
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();

        if (openShulkerMap.containsKey(uuid)) {
            for (int rawSlot : event.getRawSlots()) {
                if (rawSlot < event.getView().getTopInventory().getSize()) {
                    if (isShulkerBox(event.getOldCursor())) {
                        event.setCancelled(true);
                        event.setResult(Result.DENY);
                        debug((Player) event.getWhoClicked(), "Shulker in Shulker per Drag verhindert."); // REMOVE
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (openShulkerMap.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            debug(event.getPlayer(), "Drop während geöffnetem Inventar."); // REMOVE
        }
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
    	if (!(event.getEntity() instanceof Player player)) return;
        if (openShulkerMap.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            debug(player, "Pickup während geöffnetem Inventar."); // REMOVE
        }
    }

    @EventHandler
    public void onHotbarSwap(PlayerItemHeldEvent event) {
        if (openShulkerMap.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            debug(event.getPlayer(), "Hotbar-Wechsel während geöffnetem Inventar."); // REMOVE
        }
    }

    private record OpenShulkerData(ItemStack original, EquipmentSlot handSlot, int inventorySlot, String hash) {}
}
