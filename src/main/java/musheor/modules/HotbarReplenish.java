/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 *
 * edited by musheck
 */

package musheor.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import musheor.musheck_addon;


public class HotbarReplenish extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
            .name("threshold")
            .description("The threshold of items left to trigger replenishment.")
            .defaultValue(16)
            .min(1)
            .sliderRange(1, 63)
            .build()
    );

    private final Setting<Item> slot1Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-1-item")
            .description("Item to maintain in the first hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot2Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-2-item")
            .description("Item to maintain in the second hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot3Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-3-item")
            .description("Item to maintain in the third hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot4Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-4-item")
            .description("Item to maintain in the fourth hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot5Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-5-item")
            .description("Item to maintain in the fifth hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot6Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-6-item")
            .description("Item to maintain in the sixth hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot7Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-7-item")
            .description("Item to maintain in the seventh hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot8Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-8-item")
            .description("Item to maintain in the eigth hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    private final Setting<Item> slot9Item = sgGeneral.add(new ItemSetting.Builder()
            .name("slot-9-item")
            .description("Item to maintain in the nineth hotbar slot.")
            .defaultValue(Items.AIR)
            .build()
    );

    public HotbarReplenish() {
        super(musheck_addon.CATEGORY, "hotbar-replenish",
                "Automatically refills specific items in each hotbar slot. Each slot independantly configurable.");
    }

    @Override
    public void onDeactivate() {
        InvUtils.dropHand();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        Item[] itemsToCheck = new Item[]{
                slot1Item.get(), slot2Item.get(),
                slot3Item.get(), slot4Item.get(),
                slot5Item.get(), slot6Item.get(),
                slot7Item.get(), slot8Item.get(),
                slot9Item.get()
        };

        for (int i = 0; i <= 8; i++) {
            if (!itemsToCheck[i].equals(Items.AIR));

            checkSlotWithDesignatedItem(i, itemsToCheck[i]);
        }
    }

    private void checkSlotWithDesignatedItem(int slot, Item desiredItem) {
        assert mc.player != null;
        ItemStack currentStack = mc.player.getInventory().getStack(slot);

        if (desiredItem == Items.AIR) return;

        if (currentStack.isEmpty() || currentStack.getItem() != desiredItem) {
            int foundSlot = findSpecificItem(desiredItem, slot, threshold.get());
            if (foundSlot != -1) {
                addSlots(slot, foundSlot);
            }
        }
        else if (currentStack.isStackable() && currentStack.getCount() <= threshold.get()) {
            int foundSlot = findSpecificItem(desiredItem, slot, threshold.get() - currentStack.getCount() + 1);
            if (foundSlot != -1) {
                addSlots(slot, foundSlot);
            }
        }
    }

    private int findSpecificItem(Item item, int excludedSlot, int goodEnoughCount) {
        int slot = -1;
        int count = 0;

        assert mc.player != null;
        for (int i = mc.player.getInventory().size() - 2; i >= 0; i--) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (i != excludedSlot && stack.getItem() == item) {
                if (stack.getCount() > count) {
                    slot = i;
                    count = stack.getCount();

                    if (count >= goodEnoughCount) break;
                }
            }
        }

        return slot;
    }

    private void addSlots(int to, int from) {
        InvUtils.move().from(from).to(to);
    }
}
