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

import io.github.fisher2911.fishcore.util.helper.IdHolder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
    private Mode currentMode;
    private Mode previousMode;
    private final Map<Integer, ItemStack> survivalInventory;
    private ItemStack[] armorItems = new ItemStack[4];
    private ItemStack offHand = new ItemStack(Material.AIR);

    public User(final UUID uuid) {
        this.uuid = uuid;
        final Player player = this.getPlayer();

        if (player == null) {
            this.previousMode = Mode.SURVIVAL;
            this.currentMode = Mode.SURVIVAL;
        } else {
            this.previousMode = Mode.fromGameMode(player.getGameMode());
            this.currentMode = Mode.fromGameMode(player.getGameMode());
        }

        this.survivalInventory = new HashMap<>();
    }

    public User(final UUID uuid, final Mode currentMode, final Mode previousMode) {
        this.uuid = uuid;
        this.currentMode = currentMode;
        this.previousMode = previousMode;
        this.survivalInventory = new HashMap<>();
    }

    public User(
            final UUID uuid,
            final Mode currentMode,
            final Mode previousMode,
            final ItemStack[] armorItems,
            final ItemStack offHand,
            final Map<Integer, ItemStack> survivalInventory) {
        this.uuid = uuid;
        this.currentMode = currentMode;
        this.previousMode = previousMode;
        this.armorItems = armorItems;
        this.offHand = offHand;
        this.survivalInventory = survivalInventory;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(final Mode currentMode) {
        final Player player = this.getPlayer();
        if (player != null) {
            this.previousMode = Mode.fromGameMode(player.getGameMode());
        } else {
            this.previousMode = this.currentMode;
        }
        this.currentMode = currentMode;

       this.updateGameMode();
    }


    public void updateGameMode() {
        final Player player = this.getPlayer();

        if (player != null) {
            player.setGameMode(this.currentMode.getGameMode());
        }
    }

    public Mode getPreviousMode() {
        return previousMode;
    }

    public void setPreviousMode(final Mode previousMode) {
        this.previousMode = previousMode;
    }

    public boolean isInLimitedCreative() {
        return this.currentMode == Mode.LIMITED_CREATIVE;
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

    public void setToLimitedCreative() {
        this.setCurrentMode(Mode.LIMITED_CREATIVE);
    }

    public void returnToPreviousMode() {

        final Mode tempCurrent = this.currentMode;

        this.currentMode = this.previousMode;

        this.updateGameMode();

        this.previousMode = tempCurrent;
    }

    public boolean hasPermission(final String permission) {
        final Player player = this.getPlayer();
        if (player == null) return false;
        return player.hasPermission(permission);
    }

    public enum Mode {

        LIMITED_CREATIVE(GameMode.CREATIVE),

        SURVIVAL(GameMode.SURVIVAL),

        CREATIVE(GameMode.CREATIVE),

        SPECTATOR(GameMode.SPECTATOR),

        ADVENTURE(GameMode.ADVENTURE);

        private final GameMode gameMode;

        Mode(final GameMode gameMode) {
            this.gameMode = gameMode;
        }

        public GameMode getGameMode() {
            return gameMode;
        }

        public static Mode fromGameMode(final GameMode gameMode) {
            return switch (gameMode) {
                case SURVIVAL -> SURVIVAL;
                case CREATIVE -> CREATIVE;
                case ADVENTURE -> ADVENTURE;
                case SPECTATOR -> SPECTATOR;
            };
        }

    }
}
