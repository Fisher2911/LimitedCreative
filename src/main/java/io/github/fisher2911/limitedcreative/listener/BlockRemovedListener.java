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
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.user.User;
import io.github.fisher2911.limitedcreative.user.UserManager;
import org.bukkit.Material;
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
    private final Settings settings;
    private final CreativeModeHandler creativeModeHandler;
    private final UserManager userManager;

    public BlockRemovedListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.settings = Settings.getInstance();
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
        this.userManager = this.plugin.getUserManager();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        final Block block = event.getBlock();

        if (user != null) {
            if (user.isInLimitedCreative() &&
                    this.settings.isBannedBreakBlock(block.getType())) {
                event.setCancelled(true);
                this.plugin.getMessageHandler().
                        sendMessage(
                                player,
                                Messages.BANNED_BLOCK_BREAK
                        );
                return;
            }
        } else {
            return;
        }

        final boolean removed =
                this.creativeModeHandler.removeCreativeBlock(
                        block,
                        player);

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
