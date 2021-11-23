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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.fisher2911.limitedcreative.LimitedCreative;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDatabase extends SQLDatabase {

    private final HikariDataSource dataSource;

    MySQLDatabase(
            final LimitedCreative plugin,
            final String name,
            final String username,
            final String password,
            final String ip,
            final String port,
            final int saveInterval) {
        super("mysql", plugin, saveInterval, true);
        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + name);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(1000000000);

        plugin.logger().error("JDBC Url = " + "jdbc:mysql://" + ip + ":" + port + "/" + name);

        this.dataSource = new HikariDataSource(config);
    }

    private static final String CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    WORLD_UUID_COLUMN + " CHAR(36), " +
                    CHUNK_KEY_COLUMN + " BIGINT, " +
                    POSITION_X_COLUMN + " INT, " +
                    POSITION_Y_COLUMN + " INT, " +
                    POSITION_Z_COLUMN + " INT, " +
                    "UNIQUE (" +
                    WORLD_UUID_COLUMN + ", " +
                    CHUNK_KEY_COLUMN + ", " +
                    POSITION_X_COLUMN + ", " +
                    POSITION_Y_COLUMN + "," +
                    POSITION_Z_COLUMN + "))";

    private static final String SAVE_STATEMENT =
            "INSERT INTO " + TABLE_NAME + "(" +
                    WORLD_UUID_COLUMN + ", " +
                    CHUNK_KEY_COLUMN + ", " +
                    POSITION_X_COLUMN + ", " +
                    POSITION_Y_COLUMN + ", " +
                    POSITION_Z_COLUMN + ") " +
                    "VALUES (?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    WORLD_UUID_COLUMN + "=" + WORLD_UUID_COLUMN;

    private static final String LOAD_STATEMENT =
            "SELECT " +
                    POSITION_X_COLUMN + ", " +
                    POSITION_Y_COLUMN + ", " +
                    POSITION_Z_COLUMN + " " +
                    "FROM " + TABLE_NAME + " " +
                    "WHERE " +
                    WORLD_UUID_COLUMN + "=? " +
                    "AND " +
                    CHUNK_KEY_COLUMN + "=?";

    private static final String DELETE_STATEMENT =
            "DELETE FROM " + TABLE_NAME + " " +
                    "WHERE " + WORLD_UUID_COLUMN  + "=? AND " +
                    CHUNK_KEY_COLUMN + "=? AND " +
                    POSITION_X_COLUMN + "=? AND " +
                    POSITION_Y_COLUMN + "=? AND " +
                    POSITION_Z_COLUMN + "=?";


    @Override
    public String getCreateTableStatement() {
        return CREATE_TABLE_STATEMENT;
    }

    @Override
    public String getSaveBlocksStatement() {
        return SAVE_STATEMENT;
    }

    @Override
    public String getLoadBlocksStatement() {
        return LOAD_STATEMENT;
    }

    @Override
    public String getDeleteRemovedBlocksStatement() {
        return DELETE_STATEMENT;
    }

    @Override
    protected Connection getConnection()  {
        try {
            return this.dataSource.getConnection();
        } catch (final SQLException exception) {
            return null;
        }
    }

    @Override
    protected void close() {
        this.dataSource.close();
    }
}
