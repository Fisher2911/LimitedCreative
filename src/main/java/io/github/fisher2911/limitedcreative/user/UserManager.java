/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.user;

import io.github.fisher2911.fishcore.logger.Logger;
import io.github.fisher2911.fishcore.util.helper.Utils;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.creative.Settings;
import io.github.fisher2911.limitedcreative.lang.Permissions;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserManager {

    private final LimitedCreative plugin;
    private final Logger logger;

    private static final String ID_PATH = "id";
    private static final String CURRENT_MODE_PATH = "current-mode";
    private static final String PREVIOUS_MODE_PATH = "previous-mode";
    private static final String ARMOR_PATH = "armor";
    private static final String OFF_HAND_PATH = "off-hand";
    private static final String SURVIVAL_INVENTORY_PATH = "survival-inventory";

    private final Map<UUID, User> users;

    public UserManager(final Map<UUID, User> map, final LimitedCreative plugin) {
        this.users = map;
        this.plugin = plugin;
        this.logger = this.plugin.logger();
    }

    @Nullable
    public User getUser(final UUID uuid) {
        return this.users.get(uuid);
    }

    public void add(final User user) {
        this.users.put(user.getId(), user);
    }

    public void remove(final UUID uuid) {
        this.users.remove(uuid);
    }

    public User loadUser(final UUID uuid) {
        this.users.put(uuid, new User(uuid));

        final File file = this.getUserFile(uuid);

        if (!file.exists()) {
            final User user = new User(uuid);
            return user;
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        final User.Mode currentMode = Utils.stringToEnum(
                Utils.replaceIfNull(
                        config.getString(CURRENT_MODE_PATH),
                        ""),
                User.Mode.class,
                User.Mode.SURVIVAL);

        final User.Mode previousMode = Utils.stringToEnum(
                Utils.replaceIfNull(
                        config.getString(PREVIOUS_MODE_PATH),
                        ""),
                User.Mode.class,
                User.Mode.SURVIVAL);

        final ItemStack[] armor = Utils.replaceIfNull(
                config.getObject(ARMOR_PATH, ItemStack[].class),
                new ItemStack[4]
        );

        final ItemStack offHand = Utils.replaceIfNull(
                config.getItemStack(OFF_HAND_PATH),
                new ItemStack(Material.AIR)
        );

        final Map<Integer, ItemStack> survivalInventory = new HashMap<>();

        final ConfigurationSection inventorySection = config.getConfigurationSection(SURVIVAL_INVENTORY_PATH);

        if (inventorySection != null) {
            for (final String key : inventorySection.getKeys(false)) {

                if (!NumberUtils.isNumber(key)) {
                    continue;
                }

                final int slot = Integer.parseInt(key);
                final ItemStack itemStack = inventorySection.getItemStack(key);

                survivalInventory.put(slot, itemStack);
            }
        }

        return new User(uuid, currentMode, previousMode, armor, offHand, survivalInventory);
    }

    public void loadUserAsync(final UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(
                this.plugin,
                () -> {
                    final User user = this.loadUser(uuid);
                    Bukkit.getScheduler().runTask(this.plugin,
                            () -> {
                                this.add(user);
                                final User.Mode currentMode = user.getCurrentMode();

                                if (currentMode == User.Mode.LIMITED_CREATIVE) {
                                    if (!Settings.getInstance().isCreativeOnJoin() &&
                                    !user.hasPermission(Permissions.BYPASS_CREATIVE_ON_JOIN)) {
                                        this.plugin.
                                                getCreativeModeHandler().
                                                setBackFromLimitedCreative(user);
                                        return;
                                    }
                                } else if (!user.hasPermission(Permissions.BYPASS_CREATIVE_ON_JOIN)) {
                                    if (Settings.getInstance().isCreativeOnJoin()) {
                                        this.plugin.getCreativeModeHandler().
                                                setToLimitedCreative(user);
                                        return;
                                    }
                                }

                                user.updateGameMode();
                            });
                });
    }

    public void loadOnlineAsync() {
        final Set<UUID> users = Bukkit.
                getOnlinePlayers().
                stream().
                map(Player::getUniqueId).
                collect(Collectors.toSet());

        for (final UUID uuid : users) {
            this.loadUserAsync(uuid);
        }
    }

    public void saveAllUsers() {
        for (final User user : this.users.values()) {
            this.saveUser(user);
        }
    }

    public void saveUser(final UUID uuid) {
        final User user = this.getUser(uuid);

        if (user == null) {
            return;
        }

        this.saveUser(user);
    }

    public void saveUser(final User user) {

        final UUID id = user.getId();

        final File file = this.getUserFile(id);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException exception) {
                this.logger.configWarning("Error creating user file for " + user.getId());
                return;
            }
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set(ID_PATH, id.toString());
        config.set(CURRENT_MODE_PATH, user.getCurrentMode().toString());
        config.set(PREVIOUS_MODE_PATH, user.getPreviousMode().toString());
        config.set(OFF_HAND_PATH, user.getOffHand());
        config.set(ARMOR_PATH, user.getArmorItems());

        for (final var entry : user.getSurvivalInventory().entrySet()) {
            config.set(SURVIVAL_INVENTORY_PATH + "." + entry.getKey(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (final IOException exception) {
            this.logger.configWarning("Error saving user " + id);
        }
    }

    private File getUserFile(final UUID uuid) {
        final File file = Path.of(this.plugin.getDataFolder().getPath(),
                "users",
                uuid.toString() + ".yml").toFile();

        file.getParentFile().mkdirs();

        return file;
    }

    public void saveAsync(final UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
                () -> this.saveUser(uuid));
    }

    public void saveAllAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
                this::saveAllUsers);
    }

}
