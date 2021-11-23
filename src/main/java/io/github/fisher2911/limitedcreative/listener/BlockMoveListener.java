/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import io.github.fisher2911.limitedcreative.creative.Settings;
import io.github.fisher2911.limitedcreative.world.BlockHandler;
import io.github.fisher2911.limitedcreative.world.BlockPosition;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockMoveListener implements Listener {

    private final LimitedCreative plugin;
    private final Settings settings;
    private final WorldsBlockHandler worldsBlockHandler;
    private final CreativeModeHandler creativeModeHandler;

    public BlockMoveListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.settings = Settings.getInstance();
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(final BlockPistonExtendEvent event) {

        final Block piston = event.getBlock();
        final List<Block> blocks = event.getBlocks();

        if (this.isBannedItem(piston, blocks, event)) {
            return;
        }

        this.handleBlockMoves(
                piston,
                blocks,
                event.getDirection(),
                null,
                false
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonRetract(final BlockPistonRetractEvent event) {

        final Block piston = event.getBlock();
        final List<Block> blocks = event.getBlocks();

        if (isBannedItem(piston, blocks, event)) {
            return;
        }

        this.handleBlockMoves(
                piston,
                blocks,
                event.getDirection(),
                null,
                true
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemFall(final EntityChangeBlockEvent event) {
        final Block block = event.getBlock();

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(
                block.getWorld()
        );

        if (blockHandler == null) {
            return;
        }

        if (event.getEntity() instanceof FallingBlock &&
                blockHandler.hasCreativeBlockPosition(BlockPosition.fromLocation(
                        block.getLocation()
                ))) {
            event.setCancelled(true);
        }
    }

    private boolean isBannedItem(
            final Block piston,
            final List<Block> blocks,
            final BlockPistonEvent event) {

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(
                event.getBlock().getWorld()
        );

        if (blockHandler != null &&
                blockHandler.hasCreativeBlockPosition(BlockPosition.fromLocation(piston.getLocation()))) {
            for (final Block block : blocks) {
                if (settings.isBannedPistonItem(block.getType())) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }

        return false;
    }

    private void handleBlockMoves(
            final Block piston,
            final List<Block> unmodifiableBlocks,
            final BlockFace direction,
            @Nullable final Player player,
            boolean removeLast) {

        final List<Block> blocks = new ArrayList<>(unmodifiableBlocks);

        blocks.add(0, piston.getRelative(direction));

        final int size = blocks.size();

        for (int i = 0; i < size; i++) {
            final Block block = blocks.get(i);
            this.creativeModeHandler.handleMoveCreativeBlock(
                    block,
                    block.getRelative(direction).getLocation(),
                    player,
                    i == size - 1 && removeLast);
        }
    }
}
