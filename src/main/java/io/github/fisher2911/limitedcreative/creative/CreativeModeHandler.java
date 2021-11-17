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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreativeModeHandler {

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

    private final LimitedCreative plugin;
    private final MessageHandler messageHandler;
    private final Permission permissions;
    private final UserManager userManager;
    private final WorldsBlockHandler worldsBlockHandler;

    // todo - load blocks
    private static final Set<Material> bannedBlocks = EnumSet.noneOf(Material.class);

    static {
        bannedBlocks.add(Material.BEACON);
    }

    public CreativeModeHandler(final LimitedCreative plugin) {
        this.plugin = plugin;
        this.messageHandler = this.plugin.getMessageHandler();
        this.permissions = this.plugin.getPermissions();
        this.userManager = this.plugin.getUserManager();
        this.worldsBlockHandler = this.plugin.getWorldsBlockHandler();
    }

    public void setToLimitedCreative(final User user) {
        user.setMode(User.Mode.LIMITED_CREATIVE);

        final Player player = user.getPlayer();

        if (player == null) {
            return;
        }

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

        player.setGlowing(true);
        player.setGameMode(GameMode.CREATIVE);
        inventory.clear();

        this.permissions.playerAdd(player, Permissions.LIMITED_CREATIVE_ACTIVE);

        this.messageHandler.sendMessage(
                player,
                Messages.SET_TO_CREATIVE
        );
    }

    public void setToSurvival(final User user) {
        user.setMode(User.Mode.OTHER);

        final Player player = user.getPlayer();

        if (player == null) {
            return;
        }

        player.setGameMode(GameMode.SURVIVAL);

        final PlayerInventory inventory = player.getInventory();

        inventory.clear();

        for (final var entry : user.getSurvivalInventory().entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        inventory.setArmorContents(user.getArmorItems());
        inventory.setItemInOffHand(user.getOffHand());

        player.setGlowing(false);
        this.permissions.playerRemove(player, Permissions.LIMITED_CREATIVE_ACTIVE);

        this.messageHandler.sendMessage(
                player,
                Messages.SET_TO_SURVIVAL
        );
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

        if (bannedBlocks.contains(type)) {
            event.setCancelled(true);
            return;
        }

        if (block.getState() instanceof Container) {
            event.setCancelled(true);
            return;
        }

        final BlockPosition position = BlockPosition.fromLocation(block.getLocation());

        if (POSSIBLE_CREATURE_BLOCKS.contains(type)) {
            blockHandler.addMobBlockPosition(position);
        }

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

        if (blockHandler.removeIfCreativeContains(position)) {
            blockHandler.removeIfMobContains(position);
            return true;
        }

        return false;
    }

    public void handleMoveCreativeBlock(
            final Block original,
            final Location to,
            @Nullable final Player player) {

        final World world = original.getWorld();

        if (!world.equals(to.getWorld())) {
            return;
        }

        final BlockHandler blockHandler = this.worldsBlockHandler.getBlockHandler(world);

        if (blockHandler == null) {
            return;
        }

        final BlockPosition originPosition = BlockPosition.fromLocation(original.getLocation());

        if (blockHandler.removeIfCreativeContains(originPosition)) {
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

        if (SPAWN_EGGS.contains(inHand.getType())) {
            event.setCancelled(true);
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getState() instanceof Container) {
            event.setCancelled(true);
        }
    }

    public void handleEntityInteract(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null || !user.isInLimitedCreative()) {
            return;
        }

        event.setCancelled(true);
    }

    public void handleEntityAttack(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof final Player player)) {
            return;
        }

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null || !user.isInLimitedCreative()) {
            return;
        }

        event.setCancelled(true);
    }

    public void handleItemDrop(final PlayerDropItemEvent event) {
        final User user = this.userManager.getUser(event.getPlayer().getUniqueId());

        if (user == null) {
            return;
        }

        if (user.isInLimitedCreative()) {
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

        if (user.isInLimitedCreative()) {
            event.setCancelled(true);
        }
    }

    public void handleInventoryClick(final InventoryCreativeEvent event) {

        final HumanEntity player = event.getWhoClicked();

        final User user = this.userManager.getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        if (!user.isInLimitedCreative()) {
            return;
        }

        final ItemStack itemStack = event.getCursor();
        final ItemStack currentItem = event.getCurrentItem();

        event.setCursor(new ItemStack(itemStack.getType(), itemStack.getAmount()));

        if (currentItem != null) {
            event.setCurrentItem(new ItemStack(currentItem.getType(), currentItem.getAmount()));
        }
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

        if (!user.isInLimitedCreative()) {
            return;
        }

        final InventoryView inventoryView = event.getView();

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (final int slot : event.getRawSlots()) {
                final ItemStack itemStack = inventoryView.getItem(slot);

                if (itemStack == null) {
                    continue;
                }

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
                !user.isInLimitedCreative()) {
            return;
        }

        this.setToSurvival(user);
    }

    public void handleExperienceChange(final PlayerExpChangeEvent event) {
        final User user = this.userManager.getUser(event.getPlayer().getUniqueId());

        if (user == null || user.isInLimitedCreative()) {
            return;
        }

        event.setAmount(0);
    }

    public void handleMobSpawn(final CreatureSpawnEvent event) {

        final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();

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
                    if (blockHandler.hasCreativeBlockPosition(
                            new BlockPosition(
                                    world,
                                    x,
                                    y,
                                    z))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
