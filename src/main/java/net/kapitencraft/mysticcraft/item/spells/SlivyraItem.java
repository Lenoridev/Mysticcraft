package net.kapitencraft.mysticcraft.item.spells;

import net.kapitencraft.mysticcraft.init.ModCreativeModeTabs;
import net.kapitencraft.mysticcraft.spell.SpellSlot;
import net.kapitencraft.mysticcraft.spell.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;

public class SlivyraItem extends SpellItem {

    SpellSlot[] spellSlots = {new SpellSlot(Spells.CRYSTAL_WARP)};

    Component[] description = {Component.literal("Some say, it might be the most"), Component.literal("powerful Magic Weapon in the world")};

    public SlivyraItem() {
        super(new Properties().tab(ModCreativeModeTabs.SPELL_AND_GEMSTONE).rarity(Rarity.RARE));
    }

    @Override
    public List<Component> getItemDescription() {
        return List.of(this.description);
    }

    @Override
    public List<Component> getPostDescription() {
        return new ArrayList<Component>();
    }

    @Override
    public SpellSlot[] getSpellSlots() {
        return this.spellSlots;
    }

    @Override
    public int getActiveSpell() {
        return 0;
    }

    @Override
    public int getSpellSlotAmount() {
        return 1;
    }
}
