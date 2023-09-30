package com.minersstudios.mscore.util.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftInventoryView;
import org.jetbrains.annotations.NotNull;

public final class ShulkerBoxMenu extends AbstractContainerMenu {
    private final Container container;
    private CraftInventoryView bukkitEntity;
    private final Inventory player;

    private static final int CONTAINER_SIZE = 27;

    public ShulkerBoxMenu(
            final int syncId,
            final Inventory playerInventory,
            final Container inventory
    ) {
        super(MenuType.SHULKER_BOX, syncId);

        checkContainerSize(inventory, CONTAINER_SIZE);

        this.container = inventory;
        this.player = playerInventory;
        int j;
        int k;

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new ShulkerBoxSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public @NotNull CraftInventoryView getBukkitView() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), new CraftInventory(this.container), this);
        }
        return this.bukkitEntity;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return !this.checkReachable || this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(
            final @NotNull Player player,
            final int slot
    ) {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot1 = this.slots.get(slot);

        if (slot1.hasItem()) {
            final ItemStack itemStack1 = slot1.getItem();
            itemstack = itemStack1.copy();

            if (slot < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemStack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot1.set(ItemStack.EMPTY);
            } else {
                slot1.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(final @NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}

