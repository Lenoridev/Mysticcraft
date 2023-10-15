package net.kapitencraft.mysticcraft.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> main = dispatcher.register(Commands.literal("my_test")
                .then(Commands.literal("main_test")
                        .executes(context -> exeEnchantmentUpgrades(context.getSource()))
                )
        );
    }

    private static int exeEnchantmentUpgrades(CommandSourceStack stack) {
        ServerPlayer serverPlayer = stack.getPlayer();
        if (serverPlayer != null) {
            ItemStack mainHandItem = serverPlayer.getMainHandItem();
            Map<Enchantment, Integer> enchantments = mainHandItem.getAllEnchantments();
            Map<Enchantment, Integer> newEnchantments = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment key = entry.getKey();
                if (Math.random() > 0.5) {
                    newEnchantments.put(key, entry.getValue() + 1);
                } else {
                    newEnchantments.put(key, entry.getValue());
                }
            }
            mainHandItem.getOrCreateTag().remove("Enchantments");
            newEnchantments.forEach(mainHandItem::enchant);
        }
        return 1;
    }
}
