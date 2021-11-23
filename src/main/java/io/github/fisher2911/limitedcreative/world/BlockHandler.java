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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.concurrent.ThreadPool;
import io.github.fisher2911.limitedcreative.database.Database;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockHandler {

    private final LimitedCreative plugin;

    BlockHandler(final LimitedCreative plugin) {
        this.plugin = plugin;
    }

    private final Set<Long> modifiedChunks = new HashSet<>();

    private final Multimap<Long, BlockPosition> creativeModeBlocks = Multimaps.
            newSetMultimap(new ConcurrentHashMap<>(), HashSet::new);

    private final Multimap<Long, BlockPosition> removedBlocks = Multimaps.
            newSetMultimap(new ConcurrentHashMap<>(), HashSet::new);

    public void addCreativeBlockPosition(final BlockPosition blockPosition) {
        final long chunkKey = blockPosition.getChunkKey();
        this.creativeModeBlocks.put(chunkKey, blockPosition);
        this.modifiedChunks.add(chunkKey);
    }

    public boolean hasCreativeBlockPosition(final BlockPosition blockPosition) {
        return this.creativeModeBlocks.containsEntry(blockPosition.getChunkKey(), blockPosition);
    }

    public boolean removeIfCreativeContains(final BlockPosition blockPosition) {
        final long chunkKey = blockPosition.getChunkKey();
        if (this.creativeModeBlocks.remove(chunkKey, blockPosition)) {
            final Database database = this.plugin.getDatabase();

            ThreadPool.submit(() -> database.deleteRemovedBlock(
                    blockPosition
            ));
            return true;
        }
        return false;
    }

    @Unmodifiable
    public Map<Long, Collection<BlockPosition>> getCreativeModeBlocks() {
        return Collections.unmodifiableMap(this.creativeModeBlocks.asMap());
    }

    @Unmodifiable
    public Collection<BlockPosition> getCreativeModeBlocksInChunk(final long chunkKey) {
        return Collections.unmodifiableCollection(this.creativeModeBlocks.get(chunkKey));
    }

    public Collection<Long> getModifiedChunks() {
        return this.modifiedChunks;
    }

    public Collection<BlockPosition> getRemovedBlocksInChunk(final long chunkKey) {
        return this.removedBlocks.get(chunkKey);
    }

    public void removeModifiedChunk(final long chunkKey) {
        this.modifiedChunks.remove(chunkKey);
    }
}
