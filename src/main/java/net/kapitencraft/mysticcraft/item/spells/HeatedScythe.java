package net.kapitencraft.mysticcraft.item.spells;

import net.kapitencraft.mysticcraft.spell.SpellSlot;
import net.kapitencraft.mysticcraft.spell.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class HeatedScythe extends SpellItem implements IDamageSpellItem {

    public HeatedScythe() {
        super(new Properties().rarity(Rarity.UNCOMMON), 1, 50, 0);
        this.addSlot(new SpellSlot(Spells.FIRE_BOLD_1));
    }
    @Override
    public List<Component> getItemDescription() {
        return null;
    }

    @Override
    public List<Component> getPostDescription() {
        return null;
    }
}