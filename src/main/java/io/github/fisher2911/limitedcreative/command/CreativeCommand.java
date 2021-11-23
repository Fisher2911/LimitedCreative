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
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.lang.Permissions;
import io.github.fisher2911.limitedcreative.user.User;
import io.github.fisher2911.limitedcreative.user.UserManager;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @SubCommand("enable")
    @Permission(Permissions.LIMITED_CREATIVE_USE)
    public void enableCreative(final Player player) {
        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        this.creativeModeHandler.setToLimitedCreative(user);
    }

    @SubCommand("disable")
    @Permission(Permissions.LIMITED_CREATIVE_USE)
    public void disableCreative(final Player player) {
        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        this.creativeModeHandler.setBackFromLimitedCreative(user);
    }
}
