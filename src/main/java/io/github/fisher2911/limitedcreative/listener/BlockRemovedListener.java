package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockRemovedListener implements Listener {

    private final LimitedCreative plugin;
    private final CreativeModeHandler creativeModeHandler;

    public BlockRemovedListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final boolean removed =
                this.creativeModeHandler.removeCreativeBlock(event.getBlock(), event.getPlayer());

        if (removed) {
            event.setDropItems(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlocksExplode(final BlockExplodeEvent event) {
        this.handleExplode(event.blockList(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(final EntityExplodeEvent event) {
        this.handleExplode(event.blockList(), null);
    }

    private void handleExplode(final List<Block> blocks, @Nullable final Player player) {
        for (final Block block : blocks) {
            if (this.creativeModeHandler.removeCreativeBlock(block, player)) {
                block.setType(Material.AIR);
            }
        }
    }
}
