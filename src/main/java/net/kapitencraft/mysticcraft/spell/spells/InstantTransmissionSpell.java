package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.misc.MISCTools;
import net.kapitencraft.mysticcraft.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class InstantTransmissionSpell extends Spell {
    private final Component[] description = new Component[]{Component.literal("teleports you 8 blocks ahead")};


    public InstantTransmissionSpell() {
        super(50, "Instant Transmission", "1110111", Spell.RELEASE, Rarity.COMMON);
    }

    @Override
    public void execute(LivingEntity user, ItemStack stack) {
        MISCTools.saveTeleport(user, 8);

    }

    @Override
    public boolean canApply(Item item) {
        return true;
    }

    @Override
    public List<Component> getDescription() {
        return List.of(description);
    }
}
