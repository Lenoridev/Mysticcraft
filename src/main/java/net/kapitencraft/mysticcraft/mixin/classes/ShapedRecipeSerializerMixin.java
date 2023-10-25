package net.kapitencraft.mysticcraft.mixin.classes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kapitencraft.mysticcraft.mixin.MixinHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;
import java.util.Set;

@Mixin(ShapedRecipe.Serializer.class)
public abstract class ShapedRecipeSerializerMixin<T extends Recipe<?>> implements RecipeSerializer<T> {
    @Override
    public @NotNull T fromJson(@NotNull ResourceLocation location, @NotNull JsonObject jsonObject) {
        String s = GsonHelper.getAsString(jsonObject, "group", "");
        CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", null), CraftingBookCategory.MISC);
        Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
        String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
        int i = astring[0].length();
        int j = astring.length;
        NonNullList<Ingredient> nonnulllist = dissolvePattern(astring, map, i, j);
        ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
        return (T) new ShapedRecipe(location, s, craftingbookcategory, i, j, nonnulllist, itemstack);
    }

    private static NonNullList<Ingredient> dissolvePattern(String[] aString, Map<String, Ingredient> map, int x, int y) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(x * y, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(map.keySet());
        set.remove(" ");

        for(int i = 0; i < aString.length; ++i) {
            for(int j = 0; j < aString[i].length(); ++j) {
                String s = aString[i].substring(j, j + 1);
                Ingredient ingredient = map.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + x * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    private static String[] patternFromJson(JsonArray array) {
        String[] astring = new String[array.size()];
        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + 3 + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(array.get(i), "pattern[" + i + "]");
                if (s.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + 3 + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    private static Map<String, Ingredient> keyFromJson(JsonObject object) {

        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), MixinHelper.fromJson(entry.getValue(), false).get(0));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static String[] shrink(String[] array) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < array.length; ++i1) {
            String s = array[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (array.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[array.length - l - k];

            for(int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = array[k1 + k].substring(i, j + 1);
            }

            return astring;
        }

    }

    private static int firstNonSpace(String p_44185_) {
        int i;
        for(i = 0; i < p_44185_.length() && p_44185_.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String p_44201_) {
        int i;
        for(i = p_44201_.length() - 1; i >= 0 && p_44201_.charAt(i) == ' '; --i) {
        }

        return i;
    }

    @Override
    public @Nullable T fromNetwork(@NotNull ResourceLocation location, FriendlyByteBuf buf) {
        int i = buf.readVarInt();
        int j = buf.readVarInt();
        String s = buf.readUtf();
        CraftingBookCategory craftingbookcategory = buf.readEnum(CraftingBookCategory.class);
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

        nonnulllist.replaceAll(ignored -> MixinHelper.fromNetwork(buf));

        ItemStack itemstack = buf.readItem();
        return (T) new ShapedRecipe(location, s, craftingbookcategory, i, j, nonnulllist, itemstack);
    }




}
