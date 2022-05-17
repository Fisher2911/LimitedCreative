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

import io.github.fisher2911.fishcore.message.MessageHandler;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import io.github.fisher2911.limitedcreative.lang.Messages;
import io.github.fisher2911.limitedcreative.lang.Permissions;
import io.github.fisher2911.limitedcreative.user.User;
import io.github.fisher2911.limitedcreative.user.UserManager;
import io.github.fisher2911.limitedcreative.world.BlockHandler;
import io.github.fisher2911.limitedcreative.world.BlockPosition;
import io.github.fisher2911.limitedcreative.world.WorldsBlockHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CreativeModeHandler {

    private final LimitedCreative plugin;
    private final Settings settings;
    private final MessageHandler messageHandler;
    private final Permission permissions;
    private final UserManager userManager;
    private final WorldsBlockHandler worldsBlockHandler;

    public CreativeModeHandler(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.settings = Settings.getInstance();
        this.messageHandler = this.plugin.getMessageHandler();
        this.permissions = this.plugin.getPermissions();
        this.userManager = this.plugin.getUserManager();
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
    }

    public void setToLimitedCreative(final User user) {
        final Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        if (user.isInLimitedCreative()) {
            this.messageHandler.sendMessage(
                    player,
                    Messages.ALREADY_IN_CREATIVE
            );
            return;
        }

        user.setToLimitedCreative();

        final Map<Integer, ItemStack> survivalInventory = new HashMap<>();

        final PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < player.getInventory().getSize(); i++) {

            final ItemStack itemStack = inventory.getItem(i);

            if (itemStack == null) {
                continue;
            }

            survivalInventory.put(i, itemStack.clone());
        }

        final ItemStack[] armorContents = inventory.getArmorContents();
        final ItemStack[] armor = new ItemStack[armorContents.length];

        for (int i = 0; i < armorContents.length; i++) {

            final ItemStack itemStack = armorContents[i];

            if (itemStack == null) {
                armor[i] = new ItemStack(Material.AIR);
                continue;
            }

            armor[i] = itemStack.clone();
        }

        final ItemStack offHand = inventory.getItemInOffHand().clone();

        user.setSurvivalInventory(
                survivalInventory,
                armor,
                offHand
        );

        if (this.settings.isGlow()) player.setGlowing(true);
        inventory.clear();

        this.permissions.playerAdd(player, Permissions.LIMITED_CREATIVE_ACTIVE);

        this.messageHandler.sendMessage(
                player,
                Messages.SET_TO_CREATIVE
        );

        this.userManager.saveAsync(user.getId());
    }

    public void setBackFromLimitedCreative(final User user) {

        final Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        if (!user.isInLimitedCreative()) {
            this.messageHandler.sendMessage(
                    player,
                    Messages.NOT_IN_CREATIVE
            );
            return;
        }

        user.returnToPreviousMode();

        final PlayerInventory inventory = player.getInventory();

        inventory.clear();

        for (final var entry : user.getSurvivalInventory().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        inventory.setArmorContents(user.getArmorItems());
        inventory.setItemInOffHand(user.getOffHand());

        if (this.settings.isGlow() ||
                (this.settings.isFixGlow() && player.isGlowing())) player.setGlowing(false);

        this.permissions.playerRemove(player, Permissions.LIMITED_CREATIVE_ACTIVE);

        this.messageHandler.sendMessage(
                player,
                Messages.SET_TO_SURVIVAL
        );

        this.userManager.saveAsync(user.getId());
    }

    public void handleBlockAddedToWorld(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

        if (blockHandler == null) {
            return;
        }

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null || !user.isInLimitedCreative()) {
            return;
        }

        final Block block = event.getBlockPlaced();

        final Material type = block.getType();

        if (this.settings.isBannedPlaceBlock(type) ||
                (block.getState() instanceof Container && this.settings.isDisableContainers())) {
            event.setCancelled(true);
            this.messageHandler.sendMessage(
                    player,
                    Messages.BANNED_BLOCK_PLACE
            );
            return;
        }

        final BlockPosition position = BlockPosition.fromLocation(block.getLocation());

        blockHandler.addCreativeBlockPosition(position);
    }

    /**
     * @return true if block is creative block and was removed
     */
    public boolean removeCreativeBlock(
            final Block block,
            @Nullable final Player player) {
        final World world = block.getWorld();

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

        if (blockHandler == null) {
            return false;
        }

        final BlockPosition position = BlockPosition.fromLocation(block.getLocation());

        return blockHandler.removeIfCreativeContains(position);
    }

    public void handleMoveCreativeBlock(
            final Block original,
            final Location to,
            @Nullable final Player player,
            final boolean removePrevious) {

        final World world = original.getWorld();

        if (!world.equals(to.getWorld())) {
            return;
        }

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

        if (blockHandler == null) {
            return;
        }

        final BlockPosition originPosition = BlockPosition.fromLocation(original.getLocation());

        final boolean contains = removePrevious ?
                blockHandler.removeIfCreativeContains(originPosition) :
                blockHandler.hasCreativeBlockPosition(originPosition);

        if (contains) {
            final BlockPosition toPosition = BlockPosition.fromLocation(to);
            blockHandler.addCreativeBlockPosition(toPosition);
        }
    }

    public void handleBlockInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        if (!user.isInLimitedCreative()) {
            return;
        }

        final ItemStack inHand = player.getInventory().getItemInMainHand();

        if (
                (this.settings.disableSpawnEggs() &&
                        Settings.isSpawnEgg(inHand))) {
            event.setCancelled(true);
            this.messageHandler.sendMessage(
                    player,
                    Messages.CANNOT_SPAWN_MOB
            );
            return;
        }

        if (this.settings.isBannedClickWithItem(inHand)) {
            event.setCancelled(true);
            this.messageHandler.sendMessage(
                    player,
                    Messages.CANNOT_CLICK_WITH_ITEM
            );
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if ((block.getState() instanceof Container && this.settings.isDisableContainers()) ||
                this.settings.isBannedClickOnBlock(block.getType())) {
            event.setCancelled(true);
            this.messageHandler.sendMessage(
                    player,
                    Messages.CANNOT_CLICK_BLOCK
            );
        }
    }

    public void handleEntityInteract(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null ||
                !user.isInLimitedCreative() ||
                !this.settings.disableEntityInteract()) {
            return;
        }

        event.setCancelled(true);
    }

    public void handleEntityAttack(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof final Player player)) {
            return;
        }

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null ||
                !user.isInLimitedCreative() ||
                this.settings.disableEntityAttack()) {
            return;
        }

        event.setCancelled(true);
    }

    public void handleItemDrop(final PlayerDropItemEvent event) {
        final User user = this.userManager.getUser(event.getPlayer().getUniqueId());

        if (user == null) {
            return;
        }

        if (user.isInLimitedCreative() &&
                this.settings.disableDropItems()) {
            event.getItemDrop().remove();
        }
    }

    public void handleItemPickup(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        if (user.isInLimitedCreative() &&
                this.settings.disablePickupItems()) {
            event.setCancelled(true);
        }
    }

    public void handleInventoryClick(final InventoryCreativeEvent event) {

        final HumanEntity player = event.getWhoClicked();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        final ItemStack cursor = this.checkStripNBT(user, event.getCursor());
        final ItemStack currentItem = this.checkStripNBT(user, event.getCurrentItem());

        if (cursor != null) event.setCursor(cursor);
        if (currentItem != null) event.setCurrentItem(currentItem);
    }

    @Nullable
    private ItemStack checkStripNBT(final User user, @Nullable final ItemStack itemStack) {
        if (itemStack == null) return null;
        if (!user.isInLimitedCreative() ||
                !this.settings.removeNbtFromItems() ||
                this.settings.getIgnoreNbtMaterials().contains(itemStack.getType())) {
            return null;
        }
        return new ItemStack(itemStack.getType(), itemStack.getAmount());
    }

    public void handleInventoryDrag(final InventoryDragEvent event) {

        if (event.isCancelled()) {
            return;
        }

        final HumanEntity player = event.getWhoClicked();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        if (!user.isInLimitedCreative() ||
                !this.settings.removeNbtFromItems()) {
            return;
        }

        final InventoryView inventoryView = event.getView();

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (final int slot : event.getRawSlots()) {
                final ItemStack itemStack = inventoryView.getItem(slot);

                if (itemStack == null) continue;
                if (this.settings.getIgnoreNbtMaterials().contains(itemStack.getType())) continue;


                event.getView().setItem(slot, new ItemStack(itemStack.getType(), itemStack.getAmount()));
            }
        }, 1);

    }

    public void handleTeleport(final PlayerTeleportEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to == null ||
                Objects.equals(from.getWorld(), to.getWorld())) {
            return;
        }

        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null ||
                !user.isInLimitedCreative() ||
                !this.settings.revertGamemodeOnWorldChange()) {
            return;
        }

        this.setBackFromLimitedCreative(user);
    }

    public void handleExperienceChange(final PlayerExpChangeEvent event) {
        final User user = this.userManager.getUser(event.getPlayer().getUniqueId());

        if (user == null ||
                !user.isInLimitedCreative() ||
                !this.settings.disableExperienceChange()) {
            return;
        }

        event.setAmount(0);
    }

    public void handleMobSpawn(final CreatureSpawnEvent event) {

        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

        final EntityType entityType = event.getEntity().getType();

        switch (entityType) {
            case SNOWMAN -> {
                if (this.settings.disableSnowmenBuilding()) return;
            }
            case IRON_GOLEM -> {
                if (this.settings.disableIronGolemBuilding()) return;
            }
            case WITHER -> {
                if (this.settings.disableWitherBuilding()) return;
            }
        }

        if (spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM &&
                spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_WITHER &&
                spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN) {
            return;
        }

        final Entity entity = event.getEntity();

        final UUID world = entity.getWorld().getUID();

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

        if (blockHandler == null) {
            return;
        }

        final BoundingBox box = entity.getBoundingBox();

        final Vector min = box.getMin();
        final Vector max = box.getMax();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {

                    final BlockPosition blockPosition = new BlockPosition(world, x, y, z);

                    final Block block = blockPosition.toLocation().getBlock();

                    if (Settings.isCreatureBlock(block.getType()) &&
                            blockHandler.hasCreativeBlockPosition(
                                    blockPosition)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    public void handleCommandSend(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null || !user.isInLimitedCreative()) {
            return;
        }

        if (this.settings.isBannedCommand(event.getMessage())) {
            this.messageHandler.sendMessage(
                    player,
                    Messages.BANNED_COMMAND
            );
            event.setCancelled(true);
        }
    }

    public void handleGameModeChange(final PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null ||
                !user.isInLimitedCreative() ||
                event.getNewGameMode() == GameMode.CREATIVE) {
            return;
        }

        this.setBackFromLimitedCreative(user);

        user.setCurrentMode(
                User.Mode.fromGameMode(
                        event.getNewGameMode()));

        user.updateGameMode();
    }
}
