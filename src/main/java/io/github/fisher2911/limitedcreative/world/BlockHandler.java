package io.github.fisher2911.limitedcreative.world;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockHandler {

    BlockHandler() {}

    private final Multimap<Long, BlockPosition> creativeModeBlocks = Multimaps.
            newSetMultimap(new HashMap<>(), HashSet::new);

    // When players place blocks that may create things such as withers, iron golems, etc
    private final Multimap<Long, BlockPosition> buildableCreatureBlocks =
            Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

    public void addCreativeBlockPosition(final BlockPosition blockPosition) {
        this.creativeModeBlocks.put(blockPosition.getChunkKey(), blockPosition);
    }

    public boolean hasCreativeBlockPosition(final BlockPosition blockPosition) {
        return this.creativeModeBlocks.containsEntry(blockPosition.getChunkKey(), blockPosition);
    }

    public boolean removeIfCreativeContains(final BlockPosition blockPosition) {
        return this.creativeModeBlocks.remove(blockPosition.getChunkKey(), blockPosition);
    }

    @Unmodifiable
    public Map<Long, Collection<BlockPosition>> getCreativeModeBlocks() {
        return Collections.unmodifiableMap(this.creativeModeBlocks.asMap());
    }


    public void addMobBlockPosition(final BlockPosition blockPosition) {
        this.buildableCreatureBlocks.put(blockPosition.getChunkKey(), blockPosition);
    }

    public boolean hasMobBlockPosition(final BlockPosition blockPosition) {
        return this.buildableCreatureBlocks.containsEntry(blockPosition.getChunkKey(), blockPosition);
    }

    public boolean removeIfMobContains(final BlockPosition blockPosition) {
        return this.buildableCreatureBlocks.remove(blockPosition.getChunkKey(), blockPosition);
    }

    @Unmodifiable
    public Map<Long, Collection<BlockPosition>> getMobModeBlocks() {
        return Collections.unmodifiableMap(this.buildableCreatureBlocks.asMap());
    }
}
