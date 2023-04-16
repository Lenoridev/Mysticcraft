package net.kapitencraft.mysticcraft.misc.utils;

import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class TagUtils {


    public static boolean checkForIntAbove0(CompoundTag tag, String name) {
        return tag.contains(name) && tag.getInt(name) > 0;
    }

    public static float increaseFloatTagValue(CompoundTag tag, String name, float f) {
        float value = tag.getFloat(name)+f;
        tag.putFloat(name, value);
        return value;
    }

    public static int increaseIntegerTagValue(CompoundTag tag, String name, int i) {
        int value = tag.getInt(name)+i;
        tag.putInt(name, value);
        return value;
    }

    public static @NotNull CompoundTag putHashMapTag(@NotNull HashMap<UUID, Integer> hashMap) {
        CompoundTag mapTag = new CompoundTag();
        List<Integer> IntArray = colToList(hashMap.values());
        mapTag.put("Uuids", putUuidList(colToList(hashMap.keySet())));
        mapTag.putIntArray("Ints", IntArray);
        return mapTag;
    }

    public static @NotNull HashMap<UUID, Integer> getHashMapTag(@Nullable CompoundTag tag) {
        HashMap<UUID, Integer> hashMap = new HashMap<>();
        if (tag == null) {
            return hashMap;
        }
        int[] intArray = tag.getIntArray("Ints");
        UUID[] UuidArray = getUuidArray((CompoundTag) Objects.requireNonNull(tag.get("Uuids")));
        for (int i = 0; i < (intArray.length == Objects.requireNonNull(UuidArray).length ? intArray.length : 0); i++) {
            hashMap.put(UuidArray[i], intArray[i]);
        }
        return hashMap;
    }

    public static <T> ArrayList<T> colToList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> ArrayList<T> toList(T ts) {
        ArrayList<T> target = new ArrayList<>();
        Collections.addAll(target, ts);
        return target;
    }

    public static int[] getIntArray(CompoundTag tag) {
        if (!tag.contains("Length")) {
            MysticcraftMod.sendWarn("tried to load UUID Array from Tag but Tag isn`t Array Tag");
        } else {
            int length = tag.getInt("Length");
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = tag.getInt(String.valueOf(i));
            }
            return array;
        }
        return null;

    }

    public static UUID[] getUuidArray(CompoundTag arrayTag) {
        if (!arrayTag.contains("Length")) {
            MysticcraftMod.sendWarn("tried to load UUID Array from Tag but Tag isn`t Array Tag");
        } else {
            int length = arrayTag.getInt("Length");
            UUID[] array = new UUID[length];
            for (int i = 0; i < length; i++) {
                array[i] = arrayTag.getUUID(String.valueOf(i));
            }
            return array;
        }
        return null;
    }

    public static CompoundTag putIntList(List<Integer> list) {
        CompoundTag arrayTag = new CompoundTag();
        for (int i = 0; i < list.size(); i++) {
            arrayTag.putInt(String.valueOf(i), list.get(i));
        }
        arrayTag.putInt("Length", list.size());
        return arrayTag;
    }

    public static CompoundTag putUUIDIntMultiMap(Multimap<UUID, Integer> multimap) {
        CompoundTag tag = new CompoundTag();
        for (UUID uuid : multimap.keySet()) {
            CompoundTag uuidTag = new CompoundTag();
            Iterator<Integer> iterator = multimap.get(uuid).iterator();
            for (int i = 0; i < multimap.get(uuid).size(); i++) {
                if (iterator.hasNext()) {
                    uuidTag.putInt("Value " + i, iterator.next());
                } else {
                    throw new IllegalStateException("that shouldn't happen...");
                }
            }
            tag.put(uuid.toString(), uuidTag);
        }
        return tag;
    }


    public static CompoundTag putUuidList(List<UUID> list) {
        CompoundTag arrayTag = new CompoundTag();
        for (int i = 0; i < list.size(); i++) {
            arrayTag.putUUID(String.valueOf(i), list.get(i));
        }
        arrayTag.putInt("Length", list.size());
        return arrayTag;
    }
}
