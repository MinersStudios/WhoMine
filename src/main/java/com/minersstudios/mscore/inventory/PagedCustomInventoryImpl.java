package com.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

/**
 * PagedCustomInventoryImpl is an abstract class that provides a base
 * implementation for paged custom inventories. It extends CustomInventoryImpl
 * and implements the PagedCustomInventory interface.
 *
 * @param <S> Self-type, the specific implementation of the paged custom
 *            inventory
 */
@SuppressWarnings("unchecked")
abstract class PagedCustomInventoryImpl<S extends PagedCustomInventoryImpl<S>> extends CustomInventoryImpl<S> implements PagedCustomInventory {
    protected int page;
    protected int pagesCount;
    protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons;
    protected final @NotNull Map<Integer, S> pages;

    protected PagedCustomInventoryImpl(
            final @NotNull Component title,
            final @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);

        this.staticButtons = new HashMap<>(this.size);
        this.pages = new HashMap<>();
    }

    @Override
    public @NotNull S build() {
        this.updateStaticButtons();
        return (S) this;
    }

    @Override
    public @NotNull Map<Integer, StaticInventoryButton> staticButtons() {
        return this.staticButtons;
    }

    @Override
    public @NotNull S staticButtons(final @NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException {
        buttons.forEach(this::staticButtonAt);
        return (S) this;
    }

    @Override
    public boolean hasStaticButtons() {
        return !this.staticButtons.isEmpty();
    }

    @Override
    public @Nullable InventoryButton buttonAt(final @Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        final StaticInventoryButton staticButton = this.staticButtons.get(slot);
        return staticButton == null
                ? this.buttons.getOrDefault(slot, null)
                : staticButton.getButton(this);
    }

    @Override
    public @NotNull S staticButtonAt(
            final @Range(from = 0, to = LAST_SLOT) int slot,
            final @Nullable StaticInventoryButton button
    ) throws IllegalArgumentException {
        this.validateSlot(slot);
        this.staticButtons.put(slot, button);
        return (S) this;
    }

    @Override
    public @NotNull Map<Integer, S> getPages() {
        return this.pages;
    }

    @Override
    public @Nullable S getPage(final @Range(from = 0, to = Integer.MAX_VALUE) int page) {
        return this.pages.getOrDefault(page, null);
    }

    @Override
    public int getPageIndex() {
        return this.page;
    }

    protected void setPageIndex(final @Range(from = 0, to = Integer.MAX_VALUE) int page) {
        this.page = page;
    }

    @Override
    public int getNextPageIndex() {
        final int next = this.page + 1;
        return next >= this.pagesCount ? -1 : next;
    }

    @Override
    public int getPreviousPageIndex() {
        final int previous = this.page - 1;
        return previous < 0 ? -1 : previous;
    }

    @Override
    public int getPagesCount() {
        return this.pagesCount;
    }

    protected void setPagesCount(final @Range(from = 0, to = Integer.MAX_VALUE) int pagesCount) {
        for (final var pagedInventory : this.pages.values()) {
            pagedInventory.pagesCount = pagesCount;
        }
    }

    @Override
    public @NotNull S addPage() {
        int page = this.pagesCount;
        var pagedInventory = this.clone();

        pagedInventory.setPageIndex(page);
        this.pages.put(page, pagedInventory);
        this.updateStaticButtons(page);
        this.setPagesCount(this.pages.size());
        return pagedInventory;
    }

    @Override
    public void updateStaticButtons() {
        if (!this.hasStaticButtons()) {
            return;
        }

        for (final var entry : this.staticButtons.entrySet()) {
            final int slot = entry.getKey();
            final StaticInventoryButton button = entry.getValue();

            for (final var pagedInventory : this.pages.values()) {
                pagedInventory.setItem(
                        slot,
                        button == null
                        ? ItemStack.empty()
                        : button.getButton(pagedInventory).item()
                );
            }
        }
    }

    @Override
    public void updateStaticButtons(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        if (this.hasStaticButtons()) {
            final S pagedInventory = this.pages.get(page);

            for (final var entry : this.staticButtons.entrySet()) {
                pagedInventory.setItem(
                        entry.getKey(),
                        entry.getValue().getButton(pagedInventory).item()
                );
            }
        }
    }

    @Override
    public void open(final @NotNull Player player) {
        final S pagedInventory = this.getPage(0);

        if (pagedInventory != null) {
            player.openInventory(pagedInventory);
        }
    }
}
