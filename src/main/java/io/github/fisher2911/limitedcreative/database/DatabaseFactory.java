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

import io.github.fisher2911.fishcore.util.helper.Utils;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class DatabaseFactory {

    private static final String TYPE_PATH = "type";
    private static final String FILE_NAME = "database.yml";
    private static final String SAVE_INTERVAL_PATH = "save-interval";
    private static final String NAME_PATH = "name";
    private static final String IP_PATH = "ip";
    private static final String PORT_PATH = "port";
    private static final String USERNAME_PATH = "username";
    private static final String PASSWORD_PATH = "password";

    public static Database getDatabase(
            final LimitedCreative plugin) throws FileNotFoundException {

        final File file = Path.of(
                plugin.getDataFolder().getPath(),
                "database.yml").toFile();

        if (!file.exists()) {
            try {
                plugin.saveResource(FILE_NAME, false);
            } catch (final IllegalArgumentException exception) {
                throw new FileNotFoundException("Could not find database.yml!");
            }
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(
                file
        );

        final int saveInterval = config.getInt(SAVE_INTERVAL_PATH) * 20;

        if (saveInterval <= 0) {
            plugin.shutdown("Save interval for database must be greater than 0!");
            throw new IllegalArgumentException();
        }

        final String type = Utils.replaceIfNull(
                config.getString(TYPE_PATH), "");

        return switch (type.toLowerCase(Locale.ROOT)) {
            case "mysql" -> getMySQLDatabase(plugin, config, saveInterval);
            case "sqlite" -> new SQLiteDatabase(plugin, saveInterval);
            default -> throw new IllegalArgumentException();
        };
    }

    private static Database getMySQLDatabase(final LimitedCreative plugin,
                                             final ConfigurationSection config,
                                             final int saveInterval) {

        final String name = config.getString(NAME_PATH);
        final String ip = config.getString(IP_PATH);
        final String port = config.getString(PORT_PATH);
        final String username = config.getString(USERNAME_PATH);
        final String password = config.getString(PASSWORD_PATH);

        final List<String> errorMessages = new ArrayList<>();

        addErrorMessage(
                errorMessages,
                name,
                FILE_NAME
        );

        addErrorMessage(
                errorMessages,
                ip,
                IP_PATH
        );

        addErrorMessage(
                errorMessages,
                port,
                PORT_PATH
        );

        addErrorMessage(
                errorMessages,
                username,
                USERNAME_PATH
        );

        addErrorMessage(
                errorMessages,
                password,
                PASSWORD_PATH
        );

        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errorMessages));
        }

        return new MySQLDatabase(
                plugin,
                name,
                username,
                password,
                ip,
                port,
                saveInterval
        );
    }

    private static void addErrorMessage(
            final Collection<String> messages,
            @Nullable final Object object,
            final String field) {
        if (object == null) {
            messages.add("Could not find field " + field + " in " + FILE_NAME);
        }
    }

}
