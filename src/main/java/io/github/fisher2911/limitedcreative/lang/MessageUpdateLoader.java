/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.lang;

import io.github.fisher2911.fishcore.message.Message;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MessageUpdateLoader {

    private final LimitedCreative plugin;

    private static final List<Message> updateMessages = List.of(
            Messages.NOT_IN_CREATIVE,
            Messages.PLAYER_NOT_ONLINE,
            Messages.DISABLED_OTHER_CREATIVE,
            Messages.ENABLED_OTHER_CREATIVE,
            Messages.RELOADED
    );

    public MessageUpdateLoader(final LimitedCreative plugin) {
        this.plugin = plugin;
    }

    public void updateMessageFile() {
        final File file = Path.of(
                this.plugin.getDataFolder().getPath(),
                "messages.yml"
        ).toFile();

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean addedMessage = false;

        for (final Message message : updateMessages) {
            final String key = message.getKey();
            if (config.contains(key)) {
                continue;
            }

            addedMessage = true;

            config.set(key, message.getMessage().stripIndent().replace("\n", ""));

        }

        if (addedMessage) {
            try {
                config.save(file);
            } catch (final IOException exception) {
                this.plugin.logger().warn("Could not load updated messages.");
            }
        }
    }
}
