package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockMoveListener implements Listener {

    private final LimitedCreative plugin;
    private final CreativeModeHandler creativeModeHandler;

    public BlockMoveListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(final BlockPistonExtendEvent event) {
        this.handleBlockMoves(
                event.getBlocks(),
                event.getDirection(),
                null
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonRetract(final BlockPistonRetractEvent event) {
        this.handleBlockMoves(
                event.getBlocks(),
                event.getDirection(),
                null
        );
    }

    private void handleBlockMoves(
            final List<Block> blocks,
            final BlockFace direction,
            @Nullable final Player player) {
        for (final Block block : blocks) {
            this.creativeModeHandler.handleMoveCreativeBlock(
                    block,
                    block.getRelative(direction).getLocation(),
                    player);
        }
    }
}
