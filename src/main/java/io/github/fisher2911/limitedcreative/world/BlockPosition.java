package io.github.fisher2911.limitedcreative.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public record BlockPosition(UUID world, int x, int y, int z) {

    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    public static BlockPosition fromLocation(final Location location) {

        final UUID world = location.getWorld() == null ? null : location.getWorld().getUID();

        return new BlockPosition(
                world,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public long getChunkKey() {
        final int chunkX = this.x / 16;
        final int chunkZ = this.z / 16;
        return chunkX & 0xffffffffL | (chunkZ & 0xffffffffL) << 32;
    }
}
