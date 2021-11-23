/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.command;

import io.github.fisher2911.fishcore.message.MessageHandler;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import io.github.fisher2911.limitedcreative.creative.Settings;
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.lang.Permissions;
import io.github.fisher2911.limitedcreative.lang.Placeholders;
import io.github.fisher2911.limitedcreative.user.User;
import io.github.fisher2911.limitedcreative.user.UserManager;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Command("limitedcreative")
@Alias("lc")
public class CreativeCommand extends CommandBase {

    private final LimitedCreative plugin;
    private final UserManager userManager;
    private final CreativeModeHandler creativeModeHandler;
    private final WorldsBlockHandler worldsBlockHandler;
    private final MessageHandler messageHandler;

    public CreativeCommand(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
        this.creativeModeHandler = this.plugin.getCreativeModeHandler();
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
        this.messageHandler = this.plugin.getMessageHandler();
    }

    @Default
    @Permission(Permissions.LIMITED_CREATIVE_USE)
    public void onDefault(final CommandSender sender) {
        this.messageHandler.sendMessage(
                sender,
                Messages.INVALID_COMMAND_DEFAULT_FORMAT
        );
    }

    @SubCommand("reload")
    @Permission(Permissions.LIMITED_CREATIVE_RELOAD)
    public void reload(final CommandSender sender) {
        this.messageHandler.reload();
        Settings.getInstance().reload();
        this.messageHandler.sendMessage(
                sender,
                Messages.RELOADED
        );
    }

    @SubCommand("enable")
    @Permission(Permissions.LIMITED_CREATIVE_USE)
    public void enableCreative(final CommandSender sender, @Optional @Completion("#players") final String playerName) {
        final User user;

        if (playerName != null && !sender.hasPermission(Permissions.CHANGE_OTHER_PLAYER_MODE)) {
            this.messageHandler.sendMessage(
                    sender,
                    Messages.NO_PERMISSION
            );
            return;
        }

        if (playerName == null && sender instanceof final Player player) {
            user = this.userManager.getUser(player.getUniqueId());;
        } else {

            user = this.getUserIfOnline(sender, playerName);

            this.messageHandler.sendMessage(
                    sender,
                    Messages.ENABLED_OTHER_CREATIVE,
                    Map.of(Placeholders.PLAYER, playerName)
            );
        }

        if (user == null) {
            return;
        }

        this.creativeModeHandler.setToLimitedCreative(user);
    }

    @SubCommand("disable")
    @Permission(Permissions.LIMITED_CREATIVE_USE)
    public void disableCreative(final CommandSender sender, @Optional @Completion("#players") final String playerName) {
        final User user;

        if (playerName != null && !sender.hasPermission(Permissions.CHANGE_OTHER_PLAYER_MODE)) {
            this.messageHandler.sendMessage(
                    sender,
                    Messages.NO_PERMISSION
            );
            return;
        }

        if (playerName == null && sender instanceof final Player player) {
            user = this.userManager.getUser(player.getUniqueId());;
        } else {

            user = this.getUserIfOnline(sender, playerName);

            this.messageHandler.sendMessage(
                    sender,
                    Messages.DISABLED_OTHER_CREATIVE,
                    Map.of(Placeholders.PLAYER, playerName)
            );
        }

        if (user == null) {
            return;
        }

        this.creativeModeHandler.setBackFromLimitedCreative(user);
    }

    @Nullable
    private User getUserIfOnline(final CommandSender sender, final @Nullable String playerName) {
        if (playerName == null) {
            this.messageHandler.sendMessage(
                    sender,
                    Messages.MUST_BE_PLAYER
            );
            return null;
        }

        final Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            this.messageHandler.sendMessage(
                    sender,
                    Messages.PLAYER_NOT_ONLINE,
                    Map.of(Placeholders.PLAYER, playerName)
            );
            return null;
        }

        return this.userManager.getUser(player.getUniqueId());
    }
}
