package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final LimitedCreative plugin;
    private final CreativeModeHandler creativeModeHandler;

    public BlockPlaceListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        this.creativeModeHandler.handleBlockAddedToWorld(event);
    }
}
