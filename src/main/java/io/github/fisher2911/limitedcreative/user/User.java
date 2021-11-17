package io.github.fisher2911.limitedcreative.user;

import io.github.fisher2911.fishcore.util.helper.IdHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User implements IdHolder<UUID> {

    private final UUID uuid;
    private Mode mode;
    private final Map<Integer, ItemStack> survivalInventory = new HashMap<>();
    private ItemStack[] armorItems = new ItemStack[4];
    private ItemStack offHand = new ItemStack(Material.AIR);

    public User(final UUID uuid) {
        this.uuid = uuid;
        this.mode = Mode.OTHER;
    }

    public User(final UUID uuid, final Mode mode) {
        this.uuid = uuid;
        this.mode = mode;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    public Mode getMode() {
        return mode;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public boolean isInLimitedCreative() {
        return this.mode == Mode.LIMITED_CREATIVE;
    }

    public void setSurvivalInventory(
            final Map<Integer, ItemStack> survivalInventory,
            final ItemStack[] armorItems,
            final ItemStack offHand) {
        this.survivalInventory.clear();
        this.survivalInventory.putAll(survivalInventory);
        this.armorItems = armorItems;
        this.offHand = offHand;
    }

    @Unmodifiable
    public Map<Integer, ItemStack> getSurvivalInventory() {
        return Collections.unmodifiableMap(this.survivalInventory);
    }

    public ItemStack[] getArmorItems() {
        return armorItems;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public enum Mode {

        LIMITED_CREATIVE,

        OTHER
    }
}
