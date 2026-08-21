package com.triumph.autoswap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class TotemSwapHandler {

    private static int lastSlot = -1; // запоминаем, какой тотем брали в прошлый раз

    public static void performSwap(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        // 1. Если в левой руке уже есть тотем — ищем следующий
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            int next = findNextTotem(client);
            if (next == -1) {
                sendMessage(client, "Нет других тотемов");
                return;
            }
            swapToOffhand(client, next);
            lastSlot = next;
            return;
        }

        // 2. Если тотема в левой руке нет — берём первый попавшийся
        int slot = findAnyTotem(client);
        if (slot == -1) {
            sendMessage(client, "Нет тотемов в инвентаре");
            return;
        }
        swapToOffhand(client, slot);
        lastSlot = slot;
    }

    private static int findAnyTotem(MinecraftClient client) {
        for (int i = 0; i < 36; i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    private static int findNextTotem(MinecraftClient client) {
        int start = (lastSlot + 1) % 36;
        for (int i = 0; i < 36; i++) {
            int index = (start + i) % 36;
            ItemStack stack = client.player.getInventory().getStack(index);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING && index != lastSlot) {
                return index;
            }
        }
        // если ничего не нашли — пробуем с начала (кроме текущего)
        for (int i = 0; i < 36; i++) {
            if (i != lastSlot && client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }

    private static void swapToOffhand(MinecraftClient client, int slot) {
        // Открываем инвентарь (если не открыт)
        if (!(client.player.currentScreenHandler instanceof net.minecraft.screen.PlayerScreenHandler)) {
            client.player.openHandledScreen(client.player.playerScreenHandler);
        }

        // Берём тотем из слота
        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            slot,
            0,
            SlotActionType.PICKUP,
            client.player
        );

        // Кладём в левую руку (слот 45 в контейнере игрока)
        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            45,
            0,
            SlotActionType.PICKUP,
            client.player
        );

        sendMessage(client, "Тотем заменён");
    }

    private static void sendMessage(MinecraftClient client, String text) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§a[AutoSwap] §f" + text), false);
        }
    }
}