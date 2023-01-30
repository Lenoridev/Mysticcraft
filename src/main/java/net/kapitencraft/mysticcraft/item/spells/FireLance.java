package net.kapitencraft.mysticcraft.item.spells;

import net.kapitencraft.mysticcraft.spell.SpellSlot;
import net.kapitencraft.mysticcraft.spell.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class FireLance extends NormalSpellItem {
    public FireLance() {
        super(new Properties().rarity(Rarity.RARE), 1, 40, 0);
        this.addSlot(new SpellSlot(Spells.FIRE_LANCE.getSpell()));
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
