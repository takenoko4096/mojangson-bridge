package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * すべての型オブジェクトがこのクラスの静的フィールドで定義されています。
 * @see MojangsonValueType
 */
public final class MojangsonValueTypes {
    private MojangsonValueTypes() {}

    /**
     * byte に対応。
     * @see MojangsonByte
     */
    public static final MojangsonValueType<MojangsonByte> BYTE = new MojangsonValueType<>(MojangsonByte.class) {
        @Override
        public MojangsonByte toMojangson(@Nullable Object value) {
            return switch (value) {
                case MojangsonByte mojangsonByte -> mojangsonByte;
                case Byte byteValue -> MojangsonByte.valueOf(byteValue.byteValue());
                case Boolean booleanValue -> MojangsonByte.valueOf(booleanValue ? (byte) 1 : (byte) 0);
                case null, default -> throw new IllegalArgumentException("byte 型でない値は MojangsonByte に変換できません");
            };
        }
    };

    /**
     * short に対応。
     * @see MojangsonShort
     */
    public static final MojangsonValueType<MojangsonShort> SHORT = new MojangsonValueType<>(MojangsonShort.class) {
        @Override
        public MojangsonShort toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonShort mojangsonShort) return mojangsonShort;
            else if (value instanceof Short shortValue) return MojangsonShort.valueOf(shortValue.shortValue());
            else throw new IllegalArgumentException("short 型でない値は MojangsonShort に変換できません");
        }
    };

    /**
     * int に対応。
     * @see MojangsonInt
     */
    public static final MojangsonValueType<MojangsonInt> INT = new MojangsonValueType<>(MojangsonInt.class) {
        @Override
        public MojangsonInt toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonInt mojangsonInt) return mojangsonInt;
            else if (value instanceof Integer intValue) return MojangsonInt.valueOf(intValue.intValue());
            else throw new IllegalArgumentException("int 型でない値は MojangsonInt に変換できません");
        }
    };

    /**
     * long に対応。
     * @see MojangsonLong
     */
    public static final MojangsonValueType<MojangsonLong> LONG = new MojangsonValueType<>(MojangsonLong.class) {
        @Override
        public MojangsonLong toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonLong mojangsonLong) return mojangsonLong;
            else if (value instanceof Long longValue) return MojangsonLong.valueOf(longValue.longValue());
            else throw new IllegalArgumentException("long 型でない値は MojangsonLong に変換できません");
        }
    };

    /**
     * float に対応。
     * @see MojangsonFloat
     */
    public static final MojangsonValueType<MojangsonFloat> FLOAT = new MojangsonValueType<>(MojangsonFloat.class) {
        @Override
        public MojangsonFloat toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonFloat mojangsonFloat) return mojangsonFloat;
            else if (value instanceof Float floatValue) return MojangsonFloat.valueOf(floatValue.floatValue());
            else throw new IllegalArgumentException("float 型でない値は MojangsonFloat に変換できません");
        }
    };

    /**
     * double に対応。
     * @see MojangsonDouble
     */
    public static final MojangsonValueType<MojangsonDouble> DOUBLE = new MojangsonValueType<>(MojangsonDouble.class) {
        @Override
        public MojangsonDouble toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonDouble mojangsonDouble) return mojangsonDouble;
            else if (value instanceof Double doubleValue) return MojangsonDouble.valueOf(doubleValue.doubleValue());
            else throw new IllegalArgumentException("double 型でない値は MojangsonDouble に変換できません");
        }
    };

    /**
     * java.lang.String に対応。
     * @see MojangsonString
     */
    public static final MojangsonValueType<MojangsonString> STRING = new MojangsonValueType<>(MojangsonString.class) {
        @Override
        public MojangsonString toMojangson(@Nullable Object value) {
            return switch (value) {
                case MojangsonString mojangsonString -> mojangsonString;
                case String stringValue -> MojangsonString.valueOf(stringValue);
                case Character characterValue -> MojangsonString.valueOf(characterValue.charValue());
                case null, default ->
                    throw new IllegalArgumentException("String 型でない値は MojangsonString に変換できません");
            };
        }
    };

    /**
     * byte[] に対応。
     * @see MojangsonByteArray
     */
    public static final MojangsonValueType<MojangsonByteArray> BYTE_ARRAY = new MojangsonValueType<>(MojangsonByteArray.class) {
        @Override
        public MojangsonByteArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonByteArray mojangsonByteArray) return mojangsonByteArray;
            else if (value instanceof byte[] bytes) return new MojangsonByteArray(bytes);
            else throw new IllegalArgumentException("byte[] 型でない値は MojangsonByteArray に変換できません");
        }
    };

    /**
     * int[] に対応。
     * @see MojangsonIntArray
     */
    public static final MojangsonValueType<MojangsonIntArray> INT_ARRAY = new MojangsonValueType<>(MojangsonIntArray.class) {
        @Override
        public MojangsonIntArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonIntArray mojangsonIntArray) return mojangsonIntArray;
            else if (value instanceof int[] ints) return new MojangsonIntArray(ints);
            else throw new IllegalArgumentException("int[] 型でない値は MojangsonIntArray に変換できません");
        }
    };

    /**
     * long[] に対応。
     * @see MojangsonLongArray
     */
    public static final MojangsonValueType<MojangsonLongArray> LONG_ARRAY = new MojangsonValueType<>(MojangsonLongArray.class) {
        @Override
        public MojangsonLongArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonLongArray mojangsonLongArray) return mojangsonLongArray;
            else if (value instanceof long[] longs) return new MojangsonLongArray(longs);
            else throw new IllegalArgumentException("long[] 型でない値は MojangsonLongArray に変換できません");
        }
    };

    /**
     * java.util.Map&lt;String, ?&gt; に対応。
     * @see MojangsonCompound
     */
    public static final MojangsonValueType<MojangsonCompound> COMPOUND = new MojangsonValueType<>(MojangsonCompound.class) {
        @Override
        public MojangsonCompound toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonCompound mojangsonCompound) return mojangsonCompound;
            else if (value instanceof Map<?,?> map) {
                final Map<String, MojangsonValue<?>> compound = new HashMap<>();

                for (final Object key : map.keySet()) {
                    if (key instanceof String string) {
                        compound.put(string, MojangsonValue.valueOf(map.get(string)));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not name string");
                    }
                }

                return new MojangsonCompound(compound);
            }
            else throw new IllegalArgumentException("Map<String, ?> 型でない値は MojangsonCompound に変換できません");
        }
    };

    /**
     * java.util.List&lt;?&gt; に対応。
     * @see MojangsonList
     */
    public static final MojangsonValueType<MojangsonList> LIST = new MojangsonValueType<>(MojangsonList.class) {
        @Override
        public MojangsonList toMojangson(@Nullable Object value) {
            return switch (value) {
                case MojangsonList untyped -> untyped;
                case TypedMojangsonList<?> typed -> typed.untyped();
                case Collection<?> iterable -> {
                    final List<MojangsonValue<?>> listOfMojangson = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfMojangson.add(MojangsonValue.valueOf(element));
                    }

                    yield new MojangsonList(listOfMojangson);
                }
                case null, default -> throw new IllegalArgumentException("List<?> 型でない値は MojangsonList に変換できません");
            };
        }
    };

    /**
     * null に対応。
     * @see MojangsonNull
     */
    public static final MojangsonValueType<MojangsonNull> NULL = new MojangsonValueType<>(MojangsonNull.class) {
        @Override
        public MojangsonNull toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonNull mojangsonNull) return mojangsonNull;
            else if (value == null) return MojangsonNull.NULL;
            else throw new IllegalArgumentException("null でない値は MojangsonNull に変換できません");
        }
    };
}
