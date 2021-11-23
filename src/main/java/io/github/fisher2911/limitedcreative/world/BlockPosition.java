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

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final BlockPosition that = (BlockPosition) o;
        return this.x == that.x && this.y == that.y && this.z == that.z && Objects.equals(this.world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "BlockPosition{" +
                "world=" + this.world +
                ", x=" + this.x +
                ", y=" + this.y +
                ", z=" + this.z +
                '}';
    }
}
