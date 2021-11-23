/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.world;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WorldsBlockHandler {

    private final LimitedCreative plugin;
    private final Map<UUID, BlockHandler> blockHandlerMap = new HashMap<>();

    public WorldsBlockHandler(final LimitedCreative plugin) {
        this.plugin = plugin;
    }

    public WorldsBlockHandler(final LimitedCreative plugin, final Set<UUID> worlds) {
        this(plugin);
        this.populateMap(worlds);
    }

    private void populateMap(final Set<UUID> worlds) {
        for (final UUID uuid : worlds) {
            this.blockHandlerMap.put(uuid, new BlockHandler(this.plugin));
        }
    }

    public void onWorldLoad(final World world) {
        final UUID uuid = world.getUID();

        if (this.blockHandlerMap.containsKey(uuid)) {
            return;
        }

        this.blockHandlerMap.put(uuid, new BlockHandler(this.plugin));
    }

    public void onWorldUnload(final World world) {
        final UUID uuid = world.getUID();

        this.blockHandlerMap.remove(uuid);
    }

    public @Nullable BlockHandler getBlockHandler(final UUID world) {
        return this.blockHandlerMap.get(world);
    }

    public @Nullable BlockHandler getBlockHandler(final World world) {
        return this.blockHandlerMap.get(world.getUID());
    }

    public BlockHandler getAndAddBlockHandler(final UUID world) {
        BlockHandler blockHandler = this.blockHandlerMap.get(world);

        if (blockHandler == null) {
            blockHandler = new BlockHandler(this.plugin);
            this.blockHandlerMap.put(world,  blockHandler);
        }

        return blockHandler;
    }

    public BlockHandler getAndAddBlockHandler(final World world) {
        return this.getAndAddBlockHandler(world.getUID());
    }

}
