/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.database;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.concurrent.ThreadPool;
import io.github.fisher2911.limitedcreative.world.BlockHandler;
import io.github.fisher2911.limitedcreative.world.BlockPosition;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public abstract class SQLDatabase implements Database {

    private static final int BATCH_SIZE = 100;
    protected final String type;
    protected final LimitedCreative plugin;
    protected final WorldsBlockHandler worldsBlockHandler;
    protected final int saveInterval;

    protected final boolean closeConnectionsAfterUse;
    protected boolean isEnabled;

    public SQLDatabase(
            final String type,
            final LimitedCreative plugin,
            final int saveInterval,
            final boolean closeConnectionsAfterUse) {
        this.type = type;
        this.plugin = plugin;
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
        this.saveInterval = saveInterval;
        this.closeConnectionsAfterUse = closeConnectionsAfterUse;
    }

    public void load() {
        this.createTables();
        this.beginSaveAtInterval();

        this.isEnabled = true;
    }

    public void shutdown() {
        if (this.isEnabled) {
            this.close();
            this.isEnabled = false;
        }
    }

    private void createTables() {
        final Connection connection = this.getConnection();

        if (connection == null) {
            this.logConnectionNull();
            return;
        }

        try (final PreparedStatement statement = connection.prepareStatement(this.getCreateTableStatement())) {

            statement.executeUpdate();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            this.closeConnection(connection);
        }
    }

    @Override
    public void saveChunkBlocks(final UUID worldUUID, final long chunkKey) {
        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(worldUUID);

        if (blockHandler == null) {
            return;
        }

        final Collection<BlockPosition> blockPositions = blockHandler.getCreativeModeBlocksInChunk(chunkKey);

        final Connection connection = this.getConnection();

        if (connection == null) {
            this.logConnectionNull();
            return;
        }

        try (final PreparedStatement statement = connection.prepareStatement(this.getSaveBlocksStatement())) {

            final String uuidString = worldUUID.toString();

            if (this.isSQLite()) {
                connection.setAutoCommit(false);
            }

            int i = 0;

            for (final BlockPosition blockPosition : blockPositions) {

                final int x = blockPosition.x();
                final int y = blockPosition.y();
                final int z = blockPosition.z();

                statement.setString(1, uuidString);
                statement.setLong(2, chunkKey);
                statement.setInt(3, x);
                statement.setInt(4, y);
                statement.setInt(5, z);

                statement.addBatch();
                i++;

                if (i >= BATCH_SIZE) {
                    statement.executeBatch();
                    i = 0;
                }
            }

            statement.executeBatch();
            if (this.isSQLite()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            this.closeConnection(connection);
        }

    }

    @Override
    public void loadChunkBlocks(final UUID worldUUID, final long chunkKey) {

        ResultSet results = null;

        final Connection connection = this.getConnection();

        if (connection == null) {
            this.logConnectionNull();
            return;
        }

        try (final PreparedStatement statement = connection.prepareStatement(this.getLoadBlocksStatement())) {

            final BlockHandler blockHandler = this.worldsBlockHandler.getAndAddBlockHandler(worldUUID);

            final String uuidString = worldUUID.toString();

            statement.setString(1, uuidString);
            statement.setLong(2, chunkKey);

            results = statement.executeQuery();

            while (results.next()) {
                final int x = results.getInt(POSITION_X_COLUMN);
                final int y = results.getInt(POSITION_Y_COLUMN);
                final int z = results.getInt(POSITION_Z_COLUMN);

                blockHandler.addCreativeBlockPosition(
                        new BlockPosition(
                                worldUUID,
                                x,
                                y,
                                z
                        )
                );
            }

        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (final SQLException exception) {
                    exception.printStackTrace();
                }
            }
            this.closeConnection(connection);
        }
    }

    @Override
    public void deleteRemovedBlock(final BlockPosition blockPosition) {

        final UUID worldUUID = blockPosition.world();

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(worldUUID);

        if (blockHandler == null) {
            return;
        }

        final Connection connection = this.getConnection();

        try (final PreparedStatement statement = connection.prepareStatement(
                this.getDeleteRemovedBlocksStatement())) {

            if (this.isSQLite()) {
                connection.setAutoCommit(false);
            }

            final int x = blockPosition.x();
            final int y = blockPosition.y();
            final int z = blockPosition.z();

            statement.setString(1, worldUUID.toString());
            statement.setLong(2, blockPosition.getChunkKey());
            statement.setInt(3, x);
            statement.setInt(4, y);
            statement.setInt(5, z);

            statement.executeUpdate();

        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            this.closeConnection(connection);
        }
    }

    @Override
    public void saveAll() {
        for (final World world : Bukkit.getWorlds()) {
            final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

            if (blockHandler == null) {
                return;
            }

            final Iterator<Long> it = blockHandler.getModifiedChunks().iterator();

            while (it.hasNext()) {

                final long chunkKey = it.next();

                this.saveChunkBlocks(
                        world.getUID(),
                        chunkKey
                );

                it.remove();
            }
        }
    }

    @Override
    public void beginSaveAtInterval() {
        Bukkit.getScheduler().runTaskTimer(
                this.plugin,
                () -> ThreadPool.submit(this::saveAll),
                this.saveInterval,
                this.saveInterval
        );
    }

    private void closeConnection(final Connection connection) {
        if (this.closeConnectionsAfterUse) {
            try {
                connection.close();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void logConnectionNull() {
        this.plugin.logger().error("Connection to database not found!");
    }

    public abstract String getCreateTableStatement();

    public abstract String getSaveBlocksStatement();

    public abstract String getLoadBlocksStatement();

    public abstract String getDeleteRemovedBlocksStatement();

    @UnknownNullability
    protected abstract Connection getConnection();

    protected abstract void close();

    private boolean isSQLite() {
        return this.type.equalsIgnoreCase("sqlite");
    }
}
