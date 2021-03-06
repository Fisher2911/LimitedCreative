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
