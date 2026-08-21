package com.triumph.autoswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AutoSwapMod implements ClientModInitializer {
    public static KeyBinding swapKey;

    @Override
    public void onInitializeClient() {
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoswap.swap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.autoswap"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (swapKey.wasPressed()) {
                TotemSwapHandler.performSwap(client);
            }
        });

        System.out.println("[AutoSwap] Мод активен. Нажми G для смены тотема.");
    }
}