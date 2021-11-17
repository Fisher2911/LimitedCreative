package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryInteractListener implements Listener {

    private final LimitedCreative plugin;
    private final CreativeModeHandler creativeModeHandler;

    public InventoryInteractListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryCreativeEvent event) {
        this.creativeModeHandler.handleInventoryClick(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryDrag(final InventoryDragEvent event) {
        this.creativeModeHandler.handleInventoryDrag(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemDrop(final PlayerDropItemEvent event) {
        this.creativeModeHandler.handleItemDrop(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemPickup(final EntityPickupItemEvent event) {
        this.creativeModeHandler.handleItemPickup(event);
    }
}
