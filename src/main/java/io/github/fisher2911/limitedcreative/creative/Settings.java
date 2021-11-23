/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.creative;

import io.github.fisher2911.fishcore.util.helper.Utils;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Settings {

    private static final LimitedCreative plugin;
    private static final Settings INSTANCE;

    private Settings() {
    }

    static {
        plugin = LimitedCreative.getPlugin(LimitedCreative.class);
        INSTANCE = new Settings();
        INSTANCE.load();
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    private static final Set<Material> SPAWN_EGGS = Collections.unmodifiableSet(
            Arrays.stream(Material.values()).
                    filter(material -> material.
                            toString().
                            contains("SPAWN_EGG")).collect(
                            Collectors.toCollection(
                                    () -> EnumSet.noneOf(Material.class))));

    private static final Set<Material> POSSIBLE_CREATURE_BLOCKS = Collections.unmodifiableSet(
            EnumSet.of(
                    Material.PUMPKIN,
                    Material.WITHER_SKELETON_SKULL,
                    Material.WITHER_SKELETON_WALL_SKULL,
                    Material.IRON_BLOCK,
                    Material.SOUL_SAND,
                    Material.SNOW_BLOCK
            ));

    private static final String DISABLE_DROP_ITEMS_PATH = "disable-drop-items";
    private static final String DISABLE_PICKUP_ITEMS_PATH = "disable-pickup-items";
    private static final String DISABLE_ENTITY_INTERACT_PATH = "disable-entity-interact";
    private static final String DISABLE_ENTITY_ATTACK_PATH = "disable-entity-attack";
    private static final String REVERT_GAMEMODE_ON_WORLD_CHANGE_PATH = "revert-gamemode-on-world-change";
    private static final String DISABLE_EXPERIENCE_CHANGE_PATH = "disable-experience-change";
    private static final String REMOVE_NBT_FROM_ITEMS_PATH = "remove-nbt-from-items";
    private static final String DISABLE_SPAWN_EGGS_PATH = "disable-spawn-eggs";
    private static final String DISABLE_SNOWMEN_BUILDING_PATH = "disable-snowmen-building";
    private static final String DISABLE_IRON_GOLEM_BUILDING_PATH = "disable-iron-golem-building";
    private static final String DISABLE_WITHER_BUILDING_PATH = "disable-wither-building";

    private static final String BANNED_BLOCKS_PLACE_PATH = "banned-blocks-place";
    private static final String BANNED_BLOCKS_BREAK_PATH = "banned-blocks-break";
    private static final String BANNED_CLICK_ON_BLOCKS = "banned-click-on-blocks";
    private static final String BANNED_CLICK_WITH_ITEMS_PATH = "banned-click-with-items";
    private static final String BANNED_PISTON_PUSH_ITEMS = "banned-piston-push-items";
    private static final String COMMAND_MODE_PATH = "command-mode";
    private static final String COMMANDS_PATH = "commands";

    private boolean disableDropItems;
    private boolean disablePickupItems;
    private boolean disableEntityInteract;
    private boolean revertGamemodeOnWorldChange;
    private boolean disableEntityAttack;
    private boolean disableExperienceChange;
    private boolean removeNbtFromItems;
    private boolean disableSpawnEggs;
    private boolean disableSnowmenBuilding;
    private boolean disableIronGolemBuilding;
    private boolean disableWitherBuilding;
    private CommandMode commandMode;

    private final Set<Material> bannedPlaceBlocks = EnumSet.noneOf(Material.class);
    private final Set<Material> bannedBreakBlocks = EnumSet.noneOf(Material.class);
    private final Set<Material> bannedClickOnBlocks = EnumSet.noneOf(Material.class);
    private final Set<Material> bannedClickWithItems = EnumSet.noneOf(Material.class);
    private final Set<Material> bannedPistonPushItems = EnumSet.noneOf(Material.class);
    private final Map<String, Boolean> commands = new HashMap<>();

    public static boolean isSpawnEgg(final Material material) {
        return SPAWN_EGGS.contains(material);
    }

    public static boolean isSpawnEgg(final ItemStack itemStack) {
        return isSpawnEgg(itemStack.getType());
    }

    public static boolean isCreatureBlock(final Material material) {
        return POSSIBLE_CREATURE_BLOCKS.contains(material);
    }

    public static boolean isCreatureBlock(final ItemStack itemStack) {
        return isCreatureBlock(itemStack.getType());
    }

    public boolean isBannedPlaceBlock(final Material material) {
        return this.bannedPlaceBlocks.contains(material);
    }

    public boolean isBannedPlacedBlock(final ItemStack itemStack) {
        return this.isBannedPlaceBlock(itemStack.getType());
    }

    public boolean isBannedBreakBlock(final Material material) {
        return this.bannedBreakBlocks.contains(material);
    }

    public boolean isBannedBreakBlock(final ItemStack itemStack) {
        return this.isBannedBreakBlock(itemStack.getType());
    }

    public boolean isBannedClickWithItem(final Material material) {
        return this.bannedClickWithItems.contains(material);
    }

    public boolean isBannedClickWithItem(final ItemStack itemStack) {
        return this.isBannedClickWithItem(itemStack.getType());
    }

    public boolean isBannedClickOnBlock(final Material material) {
        return this.bannedClickOnBlocks.contains(material);
    }

    public boolean isBannedClickOnBlock(final ItemStack itemStack) {
        return this.isBannedClickOnBlock(itemStack.getType());
    }

    public boolean isBannedPistonItem(final Material material) {
        return this.bannedPistonPushItems.contains(material);
    }

    public boolean isBannedPistonItem(final ItemStack itemStack) {
        return this.isBannedPistonItem(itemStack.getType());
    }

    public boolean isBannedCommand(final String command) {
            for (final var entry : this.commands.entrySet()) {
                final String cmd = entry.getKey().toLowerCase();
                final boolean exact = entry.getValue();

                if (exact) {
                    final boolean isEqual = cmd.equalsIgnoreCase(command);
                    if (this.commandMode == CommandMode.BLACKLIST) {
                        return isEqual;
                    } else {
                        return !isEqual;
                    }
                }

                final boolean startsWith = command.toLowerCase().startsWith(cmd);

                if (this.commandMode == CommandMode.BLACKLIST) {
                    return startsWith;
                } else {
                    return !startsWith;
                }
            }
        return false;
    }

    public boolean disableEntityInteract() {
        return this.disableEntityInteract;
    }

    public boolean revertGamemodeOnWorldChange() {
        return this.revertGamemodeOnWorldChange;
    }

    public boolean disableEntityAttack() {
        return this.disableEntityAttack;
    }

    public boolean disableDropItems() {
        return this.disableDropItems;
    }

    public boolean disablePickupItems() {
        return this.disablePickupItems;
    }

    public boolean disableExperienceChange() {
        return this.disableExperienceChange;
    }

    public boolean removeNbtFromItems() {
        return this.removeNbtFromItems;
    }

    public boolean disableSpawnEggs() {
        return this.disableSpawnEggs;
    }

    public boolean disableSnowmenBuilding() {
        return this.disableSnowmenBuilding;
    }

    public boolean disableIronGolemBuilding() {
        return this.disableIronGolemBuilding;
    }

    public boolean disableWitherBuilding() {
        return this.disableWitherBuilding;
    }

    private void load() {
        plugin.saveDefaultConfig();

        final FileConfiguration config = plugin.getConfig();

        // drop items
        this.disableDropItems = config.getBoolean(DISABLE_DROP_ITEMS_PATH);
        // pickup items
        this.disablePickupItems = config.getBoolean(DISABLE_PICKUP_ITEMS_PATH);
        // entity interact
        this.disableEntityInteract = config.getBoolean(DISABLE_ENTITY_INTERACT_PATH);
        // gamemode world change
        this.revertGamemodeOnWorldChange = config.getBoolean(REVERT_GAMEMODE_ON_WORLD_CHANGE_PATH);
        // experience change
        this.disableExperienceChange = config.getBoolean(DISABLE_EXPERIENCE_CHANGE_PATH);
        // snowmen building
        this.disableSnowmenBuilding = config.getBoolean(DISABLE_SNOWMEN_BUILDING_PATH);
        // iron golem building
        this.disableIronGolemBuilding = config.getBoolean(DISABLE_IRON_GOLEM_BUILDING_PATH);
        // wither building
        this.disableWitherBuilding = config.getBoolean(DISABLE_WITHER_BUILDING_PATH);
        // entity attack
        this.disableEntityAttack = config.getBoolean(DISABLE_ENTITY_ATTACK_PATH);
        // experience change
        this.disableExperienceChange = config.getBoolean(DISABLE_EXPERIENCE_CHANGE_PATH);
        // remove nbt from items
        this.removeNbtFromItems = config.getBoolean(REMOVE_NBT_FROM_ITEMS_PATH);
        // disable spawn eggs
        this.disableSpawnEggs = config.getBoolean(DISABLE_SPAWN_EGGS_PATH);
        // command mode
        this.commandMode =
                Utils.stringToEnum(
                        Utils.replaceIfNull(
                                config.getString(COMMAND_MODE_PATH),
                                "T"
                        ).toUpperCase(Locale.ROOT),
                        CommandMode.class,
                        CommandMode.BLACKLIST);

        this.addAllMaterials(this.bannedPlaceBlocks, config.getStringList(BANNED_BLOCKS_PLACE_PATH));
        this.addAllMaterials(this.bannedBreakBlocks, config.getStringList(BANNED_BLOCKS_BREAK_PATH));
        this.addAllMaterials(this.bannedClickOnBlocks, config.getStringList(BANNED_CLICK_ON_BLOCKS));
        this.addAllMaterials(this.bannedClickWithItems, config.getStringList(BANNED_CLICK_WITH_ITEMS_PATH));
        this.addAllMaterials(this.bannedPistonPushItems, config.getStringList(BANNED_PISTON_PUSH_ITEMS));

        this.commands.putAll(
                config.getStringList(COMMANDS_PATH).
                        stream().collect(
                                Collectors.toMap(
                                        cmd -> {
                                            if (!cmd.contains(":")) {
                                                plugin.logger().error("Command must have ':' !");
                                                return "";
                                            }
                                            return cmd.split(":")[0];
                                        },
                                        cmd -> {
                                            if (!cmd.contains(":")) {
                                                plugin.logger().error("Command must have ':' !");
                                                return false;
                                            }

                                            return Boolean.getBoolean(cmd.split(":")[1]);
                                        })
                        ));
    }

    private void addAllMaterials(final Set<Material> materials, final List<String> strings) {
        materials.addAll(
                strings.
                        stream().
                        filter(material -> {
                                    try {
                                        Material.valueOf(material);
                                        return true;
                                    } catch (final Exception ignored) {
                                        return false;
                                    }
                                }

                        ).
                        map(Material::valueOf).
                        collect(Collectors.toSet())
        );
    }


}
