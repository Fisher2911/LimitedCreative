package io.github.fisher2911.limitedcreative;

import io.github.fisher2911.fishcore.FishCore;
import io.github.fisher2911.fishcore.message.MessageHandlerRegistry;
import io.github.fisher2911.limitedcreative.command.CreativeCommand;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.listener.BlockMoveListener;
import io.github.fisher2911.limitedcreative.listener.BlockPlaceListener;
import io.github.fisher2911.limitedcreative.listener.BlockRemovedListener;
import io.github.fisher2911.limitedcreative.listener.ExperienceChangeListener;
import io.github.fisher2911.limitedcreative.listener.InventoryInteractListener;
import io.github.fisher2911.limitedcreative.listener.MobSpawnListener;
import io.github.fisher2911.limitedcreative.listener.PlayerInteractListener;
import io.github.fisher2911.limitedcreative.listener.PlayerJoinListener;
import io.github.fisher2911.limitedcreative.listener.PlayerTeleportListener;
import io.github.fisher2911.limitedcreative.listener.WorldLoadListener;
import io.github.fisher2911.limitedcreative.user.UserManager;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.MessageHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.List;

public final class LimitedCreative extends FishCore {

    private UserManager userManager;
    private CreativeModeHandler creativeModeHandler;
    private WorldsBlockHandler worldsBlockHandler;
    private io.github.fisher2911.fishcore.message.MessageHandler messageHandler;
    private CommandManager commandManager;
    private Permission permissions;

    @Override
    public void onEnable() {
        this.setupPermissions();
        this.initClasses();
    }

    @Override
    public void onDisable() {

    }

    private void initClasses() {
        this.messageHandler = MessageHandlerRegistry.REGISTRY.get(this.getClass());
        this.worldsBlockHandler = new WorldsBlockHandler();
        this.userManager = new UserManager(new HashMap<>(), this);
        this.creativeModeHandler = new CreativeModeHandler(this);
        this.registerCommands();
        this.registerListeners();

        Bukkit.getScheduler().runTaskLater(this,
                () -> {
                    Bukkit.getWorlds().forEach(this.worldsBlockHandler::onWorldLoad);
                }, 1);
    }

    private void registerCommands() {
        this.commandManager = new CommandManager(this, true);
        final MessageHandler messageHandler = this.commandManager.getMessageHandler();

        messageHandler.register("cmd.no.console", sender ->
                this.messageHandler.sendMessage(
                        sender,
                        Messages.MUST_BE_PLAYER
                ));

        messageHandler.register("cmd.no.permission", sender ->
                this.messageHandler.sendMessage(
                        sender,
                        Messages.NO_PERMISSION
                )
        );

        this.commandManager.register(new CreativeCommand(this));
    }

    private void registerListeners() {
        List.of(
                        new PlayerJoinListener(this),
                        new BlockMoveListener(this),
                        new BlockPlaceListener(this),
                        new BlockRemovedListener(this),
                        new InventoryInteractListener(this),
                        new MobSpawnListener(this),
                        new PlayerInteractListener(this),
                        new ExperienceChangeListener(this),
                        new PlayerTeleportListener(this),
                        new WorldLoadListener(this)).
                forEach(this::registerListener);
    }

    private void setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp == null) {
            this.getLogger().severe("Could not find vault permissions provider, shutting down...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.permissions = rsp.getProvider();
    }

    public io.github.fisher2911.fishcore.message.MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public CreativeModeHandler getCreativeModeHandler() {
        return creativeModeHandler;
    }

    public WorldsBlockHandler getWorldsBlockHandler() {
        return worldsBlockHandler;
    }
}
