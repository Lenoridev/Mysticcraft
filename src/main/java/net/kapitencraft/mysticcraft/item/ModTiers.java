package net.kapitencraft.mysticcraft.item;

import net.kapitencraft.mysticcraft.init.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class ModTiers {
    public static final Tier MANA_STEEL = new Tier() {
        @Override
        public int getUses() {
            return 700;
        }

        @Override
        public float getSpeed() {
            return 1.8f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 13;
        }

        @Override
        public int getLevel() {
            return 7;
        }

        @Override
        public int getEnchantmentValue() {
            return 20;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.MANA_STEEL_INGOT.get());
        }
    };
    public static final Tier SPELL_TIER = new Tier() {
        @Override
        public int getUses() {
            return 1000;
        }

        @Override
        public float getSpeed() {
            return 2;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 7;
        }

        @Override
        public int getEnchantmentValue() {
            return 19;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }
    };
}
