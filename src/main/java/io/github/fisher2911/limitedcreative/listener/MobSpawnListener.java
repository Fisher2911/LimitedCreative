package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {

    private final LimitedCreative plugin;
    private final CreativeModeHandler creativeModeHandler;

    public MobSpawnListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMobSpawn(final CreatureSpawnEvent event) {
        this.creativeModeHandler.handleMobSpawn(event);
    }
}
