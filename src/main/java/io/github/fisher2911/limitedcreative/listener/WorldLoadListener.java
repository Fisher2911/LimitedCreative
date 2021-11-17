package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldLoadListener implements Listener {

    private final LimitedCreative plugin;
    private final WorldsBlockHandler worldsBlockHandler;

    public WorldLoadListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWorldLoad(final WorldLoadEvent event) {
        this.worldsBlockHandler.onWorldLoad(event.getWorld());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWorldUnload(final WorldUnloadEvent event) {
        this.worldsBlockHandler.onWorldUnload(event.getWorld());
    }
}
