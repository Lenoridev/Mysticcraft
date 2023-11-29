package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.item.data.spell.ISpellItem;
import net.kapitencraft.mysticcraft.item.item_bonus.Bonus;
import net.kapitencraft.mysticcraft.spell.Element;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public interface Spell extends Bonus {
    Rarity getRarity();
    String getCastingType();
    String getName();
    void execute(LivingEntity living, ItemStack stack);
    List<Component> getDescription();
    boolean canApply(Item item);
    void addDescription(List<Component> list, ISpellItem item, ItemStack ignoredStack, Player player);
    String getPattern();

    List<Element> elements();

    Type getType();

    String getRegistryName();

    double getDefaultManaCost();
    int getCooldown();

    enum Type {
        RELEASE("release"),
        CYCLE("cycle");
        private final String string;

        Type(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }
}