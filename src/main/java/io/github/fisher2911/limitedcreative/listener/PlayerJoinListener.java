package io.github.fisher2911.limitedcreative.listener;

import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.user.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final LimitedCreative plugin;
    private final UserManager userManager;

    public PlayerJoinListener(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.userManager = this.plugin.getUserManager();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
       this.userManager.loadUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.userManager.remove(event.getPlayer().getUniqueId());
    }
}
