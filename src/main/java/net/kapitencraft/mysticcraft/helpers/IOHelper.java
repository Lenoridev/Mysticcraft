package net.kapitencraft.mysticcraft.helpers;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.api.TriConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class IOHelper {
    private static final String LENGTH_ID = "Length";


    public static boolean checkForIntAbove0(CompoundTag tag, String name) {
        return tag.contains(name) && tag.getInt(name) > 0;
    }


    public static boolean isTagEmpty(@Nullable CompoundTag tag) {
        return tag == null || tag.isEmpty();
    }

    public static float increaseFloatTagValue(CompoundTag tag, String name, float f) {
        float value = tag.getFloat(name)+f;
        tag.putFloat(name, value);
        return value;
    }

    public static <T> T get(DataResult<T> result, @NotNull Supplier<T> defaulted) {
        Optional<T> optional = result.result();
        return optional.orElseGet(defaulted);
    }

    @SuppressWarnings("all")
    public static <T> T loadFile(File file, Codec<T> codec, Supplier<T> defaulted) {
        try {
            file.createNewFile();
            return get(codec.parse(JsonOps.INSTANCE, Streams.parse(createReader(file))), defaulted);
        } catch (IOException e) {
            MysticcraftMod.LOGGER.warn("unable to load file: {}", e.getMessage());
        }
        return defaulted.get();
    }

    private static JsonReader createReader(File file) throws FileNotFoundException {
        return new JsonReader(new FileReader(file));
    }

    private static JsonWriter createWriter(File file) throws IOException {
        return new JsonWriter(new FileWriter(file));
    }

    @SuppressWarnings("all")
    public static <T> void saveFile(File file, Codec<T> codec, T in) {
        try {
            file.createNewFile();
            Streams.write(get(codec.encodeStart(JsonOps.INSTANCE, in), JsonObject::new), createWriter(file));
        } catch (IOException e) {
            MysticcraftMod.LOGGER.warn("unable to save file: {}", e.getMessage());
        }
    }

    public static int increaseIntegerTagValue(CompoundTag tag, String name, int i) {
        int value = tag.getInt(name)+i;
        tag.putInt(name, value);
        return value;
    }

    public static int increaseIntOnlyAbove0(CompoundTag tag, String name, int i) {
        if (checkForIntAbove0(tag, name)) {
            return increaseIntegerTagValue(tag, name, i);
        }
        return tag.getInt(name);
    }

    public static int reduceBy1(CompoundTag tag, String name) {
        return increaseIntOnlyAbove0(tag, name, -1);
    }

    public static @NotNull CompoundTag putHashMapTag(@NotNull HashMap<UUID, Integer> hashMap) {
        CompoundTag mapTag = new CompoundTag();
        List<Integer> IntArray = CollectionHelper.fromAny(hashMap.values());
        mapTag.put("Uuids", putUuidList(CollectionHelper.fromAny(hashMap.keySet())));
        mapTag.putIntArray("Ints", IntArray);
        mapTag.putInt(LENGTH_ID, hashMap.size());
        return mapTag;
    }

    @SuppressWarnings("ALL")
    public static void injectCompoundTag(Entity toInject, CompoundTag tag) {
        CompoundTag data = toInject.getPersistentData();
        Set<String> allKeys = tag.getAllKeys();
        allKeys.forEach(s -> {
            if (tag.get(s) != null) {
                if (tag.get(s) instanceof CompoundTag cTag)
                    injectCompoundTag(toInject, cTag);
                else data.put(s, tag.get(s));
            }
        });
    }

    public static String fromCompoundTag(CompoundTag tag) {
        return new StringTagVisitor().visit(tag);
    }

    public static CompoundTag fromString(String s) {
        try {
            return new TagParser(new StringReader(s)).readStruct();
        } catch (CommandSyntaxException e) {
            MysticcraftMod.LOGGER.warn("unable to read Tag '{}': {}", s, e.getMessage());
            return new CompoundTag();
        }
    }


    public static @NotNull HashMap<UUID, Integer> getHashMapTag(@Nullable CompoundTag tag) {
        HashMap<UUID, Integer> hashMap = new HashMap<>();
        if (tag == null) {
            return hashMap;
        }
        int[] intArray = tag.getIntArray("Ints");
        UUID[] UuidArray = getUuidArray(tag.getCompound("Uuids"));
        if (UuidArray != null) {
            for (int i = 0; i < (intArray.length == UuidArray.length ? intArray.length : 0); i++) {
                hashMap.put(UuidArray[i], intArray[i]);
            }
        }
        return hashMap;
    }

    public static UUID[] getUuidArray(CompoundTag arrayTag) {
        if (!arrayTag.contains(LENGTH_ID)) {
            MysticcraftMod.LOGGER.warn("tried to load UUID Array from Tag but Tag isn`t Array Tag");
        } else {
            int length = arrayTag.getInt(LENGTH_ID);
            UUID[] array = new UUID[length];
            for (int i = 0; i < length; i++) {
                array[i] = arrayTag.getUUID(String.valueOf(i));
            }
            return array;
        }
        return null;
    }

    public static final PrimitiveCodec<UUID> UUID_CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<UUID> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).map(UUID::fromString);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, UUID value) {
            return ops.createString(value.toString());
        }
    };

    public static CompoundTag putUuidList(List<UUID> list) {
        CompoundTag arrayTag = new CompoundTag();
        for (int i = 0; i < list.size(); i++) {
            arrayTag.putUUID(String.valueOf(i), list.get(i));
        }
        arrayTag.putInt(LENGTH_ID, list.size());
        return arrayTag;
    }

    public static class TagBuilder {
        private final CompoundTag tag = new CompoundTag();

        public static TagBuilder create() {
            return new TagBuilder();
        }

        public <T> TagBuilder withArg(String name, T value, TriConsumer<CompoundTag, String, T> consumer) {
            consumer.accept(tag, name, value);
            return this;
        }

        public CompoundTag build() {
            return tag;
        }
    }
}
