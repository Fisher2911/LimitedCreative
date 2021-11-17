package io.github.fisher2911.limitedcreative.command;

import io.github.fisher2911.fishcore.message.MessageHandler;
import io.github.fisher2911.fishcore.message.MessageHandlerRegistry;
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

        this.creativeModeHandler.setToSurvival(user);
    }
}
