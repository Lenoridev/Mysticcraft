package net.kapitencraft.mysticcraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kapitencraft.mysticcraft.init.ModEnchantments;
import net.kapitencraft.mysticcraft.item.capability.dungeon.IPrestigeAbleItem;
import net.kapitencraft.mysticcraft.item.capability.dungeon.IStarAbleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;

public class MiscCommand {
    public static void exeMaxEnchant(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> main = dispatcher.register(Commands.literal("misc")
                .requires(ModCommands::isModerator)
                .then(Commands.literal("max_ench")
                        .executes(MiscCommand::exeMaxEnchant)
                ).then(Commands.literal("hyper_max_ench")
                        .executes(MiscCommand::exeEnchantmentUpgrades)
                ).then(Commands.literal("hyper_max")
                        .executes(MiscCommand::exeHyperMax)
                )
        );
    }

    private static int exeMaxEnchant(CommandContext<CommandSourceStack> context) {
        return ModCommands.checkNonConsoleCommand(context, (player, stack) -> {
            ItemStack stack1 = player.getMainHandItem();
            if (stack1.isEnchantable()) {
                int i = 0;
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                if (Enchantments.SHARPNESS.canEnchant(stack1)) enchantments.put(Enchantments.SHARPNESS, 5);
                if (Enchantments.BLOCK_FORTUNE.canEnchant(stack1)) enchantments.put(Enchantments.BLOCK_FORTUNE, 3);
                if (Enchantments.ALL_DAMAGE_PROTECTION.canEnchant(stack1)) {
                    enchantments.put(Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    enchantments.put(ModEnchantments.BONK.get(), 1);
                }
                for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
                    if (enchantment.canEnchant(stack1) && isCompatible(enchantments, enchantment) && !enchantment.isCurse()) {
                        enchantments.put(enchantment, enchantment.getMaxLevel());
                        i++;
                    }
                }
                enchantments.forEach(stack1::enchant);
                ModCommands.sendSuccess(stack, "command.misc.max_enchant.success", stack1.getHoverName(), i);
                return 1;
            }
            stack.sendFailure(Component.translatable("command.misc.max_enchant.failed"));
            return 0;
        });
    }

    private static boolean isCompatible(Map<Enchantment, Integer> map, Enchantment enchantment) {
        for (Enchantment enchantment1 : map.keySet()) {
            if (!enchantment.isCompatibleWith(enchantment1)) {
                return false;
            }
        }
        return true;
    }

    private static int exeEnchantmentUpgrades(CommandContext<CommandSourceStack> context) {
        CommandSourceStack stack = context.getSource();
        ServerPlayer serverPlayer = stack.getPlayer();
        if (serverPlayer != null) {
            ItemStack mainHandItem = serverPlayer.getMainHandItem();
            Map<Enchantment, Integer> enchantments = mainHandItem.getAllEnchantments();
            Map<Enchantment, Integer> newEnchantments = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment key = entry.getKey();
                if (entry.getValue() < Math.floor(key.getMaxLevel() * 1.5)) {
                    newEnchantments.put(key, (int) (key.getMaxLevel() * 1.5));
                } else {
                    newEnchantments.put(key, entry.getValue());
                }
            }
            mainHandItem.getOrCreateTag().remove("Enchantments");
            newEnchantments.forEach(mainHandItem::enchant);
            return 1;
        }
        stack.sendFailure(Component.translatable("command.failed.console"));
        return 0;
    }

    private static int exeHyperMax(CommandContext<CommandSourceStack> context) {
        return ModCommands.checkNonConsoleCommand(context, (serverPlayer, stack) -> {
            exeMaxEnchant(context);
            exeEnchantmentUpgrades(context);
            exeExtraUpgrades(context);
            stack.sendSuccess(Component.translatable("command.misc.hyper_max.success").withStyle(ChatFormatting.GREEN), true);
            return 1;
        });
    }

    private static int exeExtraUpgrades(CommandContext<CommandSourceStack> context) {
        return ModCommands.checkNonConsoleCommand(context, (serverPlayer, stack) -> {
            ItemStack mainHand = serverPlayer.getMainHandItem();

            int prestiges = 0;
            if (mainHand.getItem() instanceof IPrestigeAbleItem) {
                ItemStack prestigedItem = mainHand;
                while (prestigedItem.getItem() instanceof IPrestigeAbleItem prestige && prestige.mayUpgrade(mainHand)) {
                    prestigedItem = prestige.upgrade(mainHand);
                    prestiges++;
                }
                mainHand = prestigedItem;
            }
            int stars = 0;
            if (mainHand.getItem() instanceof IStarAbleItem starAbleItem) {
                stars = starAbleItem.getMaxStars(mainHand) - IStarAbleItem.getStars(mainHand);
                IStarAbleItem.setStars(mainHand, starAbleItem.getMaxStars(mainHand));
            }
            stack.sendSuccess(Component.translatable("command.misc.extra_upgrade.success", prestiges, stars), true);
            return 1;
        });
    }
}
