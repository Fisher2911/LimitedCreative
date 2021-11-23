/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative;

import io.github.fisher2911.fishcore.FishCore;
import io.github.fisher2911.fishcore.message.MessageHandlerRegistry;
import io.github.fisher2911.fishcore.util.PositionUtil;
import io.github.fisher2911.limitedcreative.command.CreativeCommand;
import io.github.fisher2911.limitedcreative.creative.CreativeModeHandler;
import io.github.fisher2911.limitedcreative.database.Database;
import io.github.fisher2911.limitedcreative.database.DatabaseFactory;
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.listener.*;
import io.github.fisher2911.limitedcreative.user.UserManager;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.MessageHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

public final class LimitedCreative extends FishCore {

    private Database database;
    private UserManager userManager;
    private CreativeModeHandler creativeModeHandler;
    private WorldsBlockHandler worldsBlockHandler;
    private io.github.fisher2911.fishcore.message.MessageHandler messageHandler;
    private CommandManager commandManager;
    private Permission permissions;

    @Override
    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
        this.setupPermissions();
        this.initClasses();
        this.database.load();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.userManager.saveAllUsers();
        this.database.saveAll();
        this.database.shutdown();
    }

    private void initClasses() {
        this.messageHandler = MessageHandlerRegistry.REGISTRY.get(this.getClass());
        this.worldsBlockHandler = new WorldsBlockHandler(this);
        try {
            this.database = DatabaseFactory.getDatabase(this);
        } catch (final FileNotFoundException exception) {
            this.shutdown("Error setting up database, shutting down plugin...");
        }
        this.userManager = new UserManager(new HashMap<>(), this);
        this.creativeModeHandler = new CreativeModeHandler(this);
        this.registerCommands();
        this.registerListeners();

        Bukkit.getScheduler().runTaskLater(this,
                () -> Bukkit.getWorlds().forEach(world -> {
                    for (final Chunk chunk : world.getLoadedChunks()) {
                        this.database.loadChunkBlocks(
                                world.getUID(),
                                PositionUtil.getChunkKey(chunk)
                        );
                    }
                }), 1);

        this.loadAll();
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
                        new WorldLoadListener(this),
                        new CommandSendListener(this),
                        new ChunkLoadListener(this),
                        new GamemodeChangeListener(this)).
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

    public Database getDatabase() {
        return database;
    }

    private void loadAll() {
        this.userManager.loadOnlineAsync();
        this.database.shutdown();
        this.database.load();
    }

    public void shutdown(final String errorMessage) {
        if (!errorMessage.isBlank()) {
            this.logger().error(errorMessage);
        }
        this.onDisable();
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
