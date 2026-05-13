package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * mojangson構造を文字列にシリアライズするクラス。
 * このクラスにMojangsonCompoundを渡すことによって生成された文字列は、net.minecraft.nbt.TagParser.parseCompoundFully(String)を使用してnet.minecraft.nbt.CompoundTagに変換できます。
 */
@NullMarked
public class MojangsonSerializer {
    private final int indentationSpaceCount;

    private final boolean asJson;

    private MojangsonSerializer(int indentationSpaceCount, boolean asJson) {
        this.indentationSpaceCount = indentationSpaceCount;
        this.asJson = asJson;
    }

    /**
     * mojangson構造を文字列としてシリアライズします。
     * @param structure mojangson構造体。
     * @return 改行・空白文字によるインデントを含む文字列。
     * @throws MojangsonSerializationException シリアライズに失敗した場合。
     */
    public String serialize(MojangsonStructure structure) throws MojangsonSerializationException {
        return serialize(structure, 1).toString();
    }

    private StringBuilder serialize(@Nullable Object value, int indentation) throws MojangsonSerializationException {
        return switch (value) {
            case Boolean v -> bool(v);
            case Number v -> number(v);
            case String v -> string(v);
            case MojangsonCompound v -> compound(v, indentation);
            case MojangsonIterable<?> v -> iterable(v, indentation);
            case MojangsonValue<?> v -> serialize(v.value, indentation);
            case null -> new StringBuilder("null");
            default -> throw new MojangsonSerializationException("このオブジェクトは無効な型の値を含みます");
        };
    }

    private StringBuilder compound(MojangsonCompound compound, int indentation) {
        final String[] keys = compound.keys().toArray(String[]::new);

        final StringBuilder stringBuilder = new StringBuilder().append(COMPOUND_BRACES[0]);

        for (int i = 0; i < keys.length; i++) {
            final String key = keys[i];

            try {
                final Object childValue = compound.get(key, compound.getTypeOf(key));
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(string(key))
                    .append(COLON)
                    .append(WHITESPACE)
                    .append(serialize(childValue, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new MojangsonSerializationException("キー'" + key + "における無効な型: " + compound.getTypeOf(key), e);
            }

            if (i != keys.length - 1) {
                stringBuilder.append(COMMA);
            }
        }

        if (keys.length > 0) {
            stringBuilder
                .append(LINE_BREAK)
                .append(indentation(indentation));
        }

        stringBuilder.append(COMPOUND_BRACES[1]);

        return stringBuilder;
    }

    private StringBuilder iterable(MojangsonIterable<?> iterable, int indentation) {
        StringBuilder stringBuilder = new StringBuilder().append(ARRAY_LIST_BRACES[0]);

        if (!asJson && ITERABLE_TYPE_SYMBOLS.containsKey(iterable.getClass())) {
            stringBuilder
                .append(ITERABLE_TYPE_SYMBOLS.get(iterable.getClass()))
                .append(SEMICOLON);
        }

        int i = 0;
        for (final MojangsonValue<?> element : iterable) {
            if (i >= 1) {
                stringBuilder.append(COMMA);
            }

            try {
                stringBuilder
                    .append(LINE_BREAK)
                    .append(indentation(indentation + 1))
                    .append(serialize(element, indentation + 1));
            }
            catch (IllegalArgumentException e) {
                throw new MojangsonSerializationException("インデックス'" + i + "における無効な型: " + element.getClass().getName(), e);
            }

            i++;
        }

        if (!iterable.isEmpty()) {
            stringBuilder
                .append(LINE_BREAK)
                .append(indentation(indentation));
        }

        return stringBuilder.append(ARRAY_LIST_BRACES[1]);
    }

    private StringBuilder string(String value) {
        boolean requireQuote = asJson || SYMBOLS_ON_STRING.stream().anyMatch(sym -> value.contains(sym.toString())) || KEYWORDS.contains(value);
        final StringBuilder stringBuilder = new StringBuilder();

        if (requireQuote) stringBuilder.append(QUOTE);
        stringBuilder.append(value.replaceAll(String.valueOf(QUOTE), String.valueOf(ESCAPE).repeat(2) + QUOTE));
        if (requireQuote) stringBuilder.append(QUOTE);

        return stringBuilder;
    }

    private StringBuilder bool(boolean value) {
        if (value) return new StringBuilder("true");
        else return new StringBuilder("false");
    }

    private StringBuilder number(Number value) {
        final StringBuilder stringBuilder = new StringBuilder(String.valueOf(value));

        if (!asJson && NUMBER_TYPE_SYMBOLS.containsKey(value.getClass())) {
            stringBuilder.append(NUMBER_TYPE_SYMBOLS.get(value.getClass()));
        }

        return stringBuilder;
    }

    private String indentation(int indentation) {
        return String
            .valueOf(WHITESPACE)
            .repeat(indentationSpaceCount)
            .repeat(indentation - 1);
    }

    private static final char LINE_BREAK = '\n';

    private static final char QUOTE = '"';

    private static final char COLON = ':';

    private static final char COMMA = ',';

    private static final char SEMICOLON = ';';

    private static final char[] COMPOUND_BRACES = {'{', '}'};

    private static final char[] ARRAY_LIST_BRACES = {'[', ']'};

    private static final char WHITESPACE = ' ';

    private static final char ESCAPE = '\\';

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> SYMBOLS_ON_STRING = new HashSet<>();

    private static final Set<String> KEYWORDS = new HashSet<>(Set.of(
        "true", "false", "null"
    ));

    static {
        SYMBOLS_ON_STRING.add(WHITESPACE);
        SYMBOLS_ON_STRING.add(LINE_BREAK);
        SYMBOLS_ON_STRING.add(COMMA);
        SYMBOLS_ON_STRING.add(COLON);
        SYMBOLS_ON_STRING.add(SEMICOLON);
        SYMBOLS_ON_STRING.add(ESCAPE);
        SYMBOLS_ON_STRING.add(QUOTE);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[0]);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[1]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[0]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[1]);
        SYMBOLS_ON_STRING.addAll(SIGNS);
        SYMBOLS_ON_STRING.add(DECIMAL_POINT);
    }

    private static final Map<Class<? extends MojangsonIterable<?>>, Character> ITERABLE_TYPE_SYMBOLS = new HashMap<>(Map.of(
        MojangsonByteArray.class, 'B',
        MojangsonIntArray.class, 'I',
        MojangsonLongArray.class, 'L'
    ));

    private static final Map<Class<? extends Number>, Character> NUMBER_TYPE_SYMBOLS = new HashMap<>(Map.of(
        byte.class, 'b',
        short.class, 's',
        long.class, 'L',
        float.class, 'f',
        double.class, 'd',
        Byte.class, 'b',
        Short.class, 's',
        Long.class, 'L',
        Float.class, 'f',
        Double.class, 'd'
    ));

    /**
     * mojangson構造を文字列としてシリアライズします。
     * インデントの空白の文字数には4を使用します。
     * @param structure mojangson構造体。
     * @param asJson trueの場合、json形式でシリアライズされます。
     * @return 改行・空白文字によるインデントを含む文字列。
     */
    public static String structure(MojangsonStructure structure, boolean asJson) throws MojangsonSerializationException {
        return new MojangsonSerializer(4, asJson).serialize(structure);
    }

    /**
     * mojangson構造を文字列としてシリアライズします。
     * インデントの空白の文字数には4を使用し、mojangson形式でシリアライズされます。
     * @param structure mojangson構造体。
     * @return 改行・空白文字によるインデントを含む文字列。
     */
    public static String structure(MojangsonStructure structure) throws MojangsonSerializationException {
        return structure(structure, false);
    }
}
