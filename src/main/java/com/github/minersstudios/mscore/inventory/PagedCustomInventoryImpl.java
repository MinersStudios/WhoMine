package com.github.minersstudios.mscore.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

/**
 * PagedCustomInventoryImpl is an abstract class that provides a base implementation for paged custom inventories.
 * It extends CustomInventoryImpl and implements the PagedCustomInventory interface.
 *
 * @param <S> Self type, the specific implementation of the paged custom inventory.
 */
@SuppressWarnings("unchecked")
abstract class PagedCustomInventoryImpl<S extends PagedCustomInventoryImpl<S>> extends CustomInventoryImpl<S> implements PagedCustomInventory {
    protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons = new HashMap<>();
    protected final @NotNull Map<Integer, S> pages = new HashMap<>();
    protected int page;
    protected int pagesSize;

    protected PagedCustomInventoryImpl(
            @NotNull Component title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);
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
    public @NotNull S staticButtons(@NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException {
        buttons.forEach(this::staticButtonAt);
        return (S) this;
    }

    @Override
    public boolean hasStaticButtons() {
        return !this.staticButtons.isEmpty();
    }

    @Override
    public @Nullable InventoryButton buttonAt(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        StaticInventoryButton staticButton = this.staticButtons.get(slot);
        return staticButton == null
                ? this.buttons.getOrDefault(slot, null)
                : staticButton.getButton(this);
    }

    @Override
    public @NotNull S staticButtonAt(
            @Range(from = 0, to = LAST_SLOT) int slot,
            @Nullable StaticInventoryButton button
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
    public @Nullable S getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        return this.pages.getOrDefault(page, null);
    }

    @Override
    public int getPageIndex() {
        return this.page;
    }

    protected void setPageIndex(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        this.page = page;
    }

    @Override
    public int getNextPageIndex() {
        int next = this.page + 1;
        return next >= this.pagesSize ? -1 : next;
    }

    @Override
    public int getPreviousPageIndex() {
        int previous = this.page - 1;
        return previous < 0 ? -1 : previous;
    }

    @Override
    public int getPagesSize() {
        return this.pagesSize;
    }

    protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
        for (var pagedInventory : this.pages.values()) {
            pagedInventory.pagesSize = pagesSize;
        }
    }

    @Override
    public @NotNull S addPage() {
        int page = this.pagesSize;
        var pagedInventory = this.clone();

        pagedInventory.setPageIndex(page);
        this.pages.put(page, pagedInventory);
        this.updateStaticButtons(page);
        this.setPagesSize(this.pages.size());
        return pagedInventory;
    }

    @Override
    public void updateStaticButtons() {
        if (this.hasStaticButtons()) {
            this.staticButtons.forEach((slot, button) -> {
                for (var pagedInventory : this.pages.values()) {
                    pagedInventory.setItem(slot, button == null ? EMPTY_ITEM : button.getButton(pagedInventory).item());
                }
            });
        }
    }

    @Override
    public void updateStaticButtons(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        if (this.hasStaticButtons()) {
            S pagedInventory = this.pages.get(page);

            for (var entry : this.staticButtons.entrySet()) {
                pagedInventory.setItem(entry.getKey(), entry.getValue().getButton(pagedInventory).item());
            }
        }
    }

    @Override
    public void open(@NotNull Player player) {
        S pagedInventory = this.getPage(0);
        if (pagedInventory != null) {
            player.openInventory(pagedInventory);
        }
    }
}
