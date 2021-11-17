package io.github.fisher2911.limitedcreative.world;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WorldsBlockHandler {

    private final Map<UUID, BlockHandler> blockHandlerMap = new HashMap<>();

    public WorldsBlockHandler() {}

    public WorldsBlockHandler(final Set<UUID> worlds) {
        this.populateMap(worlds);
    }

    private void populateMap(final Set<UUID> worlds) {
        for (final UUID uuid : worlds) {
            this.blockHandlerMap.put(uuid, new BlockHandler());
        }
    }

    public void onWorldLoad(final World world) {
        final UUID uuid = world.getUID();

        if (this.blockHandlerMap.containsKey(uuid)) {
            return;
        }

        this.blockHandlerMap.put(uuid, new BlockHandler());
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

}
