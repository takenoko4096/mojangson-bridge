package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public class MojangsonParser {
    private static final Set<Character> WHITESPACE = Set.of(' ', '\n', '\t');

    private static final char COMMA = ',';

    private static final char COLON = ':';

    private static final char SEMICOLON = ';';

    private static final char ESCAPE = '\\';

    private static final Set<Character> QUOTES = Set.of('"', '\'');

    private static final char[] FUNCTION_BRACES = {'(', ')'};

    private static final char[] COMPOUND_BRACES = {'{', '}'};

    private static final char[] ARRAY_LIST_BRACES = {'[', ']'};

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final String[] BOOLEANS = {"false", "true"};

    private static final char SIGNED = 's';

    private static final char UNSIGNED = 'u';

    private static final Function<String, ? extends Number> DEFAULT_SIGNED_INT_PARSER = Integer::parseInt;

    private static final Function<String, ? extends Number> DEFAULT_UNSIGNED_INT_PARSER = Integer::parseUnsignedInt;

    private static final Function<String, ? extends Number> DEFAULT_DECIMAL_PARSER = Double::parseDouble;

    private static final Map<Character, Function<String, ? extends Number>> SIGNED_NUMBER_PARSERS = new HashMap<>(Map.of(
        'b', Byte::parseByte,
        's', Short::parseShort,
        'i', Integer::parseInt,
        'l', Long::parseLong,
        'f', Float::parseFloat,
        'd', Double::parseDouble
    ));

    private final Map<Character, Function<String, ? extends Number>> UNSIGNED_NUMBER_PARSERS = new HashMap<>(Map.of(
        'b', (string) -> {
            final int value = Integer.parseUnsignedInt(string);
            if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
                return (byte) value;
            }
            else {
                throw exception(value + ' ' + "は byte の値として不正です");
            }
        },
        's', (string) -> {
            final int value = Integer.parseUnsignedInt(string);
            if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
                return (short) value;
            }
            else {
                throw exception(value + ' ' + "は short の値として不正です");
            }
        },
        'i', Integer::parseUnsignedInt,
        'l', Long::parseUnsignedLong
    ));

    private static final Map<Character, Function<MojangsonList, MojangsonIterable<? extends MojangsonNumber<?>>>> PRIMITIVE_ARRAY_CONVERTERS = new HashMap<>(Map.of(
        'B', MojangsonByteArray::from,
        'I', MojangsonIntArray::from,
        'L', MojangsonLongArray::from
    ));

    private final Map<String, MojangsonFunctionalOperator> functions = new HashMap<>(Map.of(
        "bool", (args) -> {
            if (args.size() != 1) {
                throw exception("関数 bool() の引数長は 1 が適切です");
            }

            final MojangsonValue<?> value = args.getFirst();

            if (value instanceof MojangsonNumber<?> number) {
                return number.intValue() == 0 ? MojangsonByte.valueOf((byte) 0) : MojangsonByte.valueOf((byte) 1);
            }
            else {
                throw exception("関数 'bool()' の第一引数の型 '" + value.getType() + "' が正しくありません");
            }
        },
        "uuid", (args) -> {
            if (args.size() != 1) {
                throw exception("関数 uuid() の引数長は 1 が適切です");
            }

            final MojangsonValue<?> value = args.getFirst();

            if (value instanceof MojangsonString string) {
                final UUID uuid;
                try {
                    uuid = UUID.fromString(string.value);
                }
                catch (IllegalArgumentException e) {
                    throw exception("'" + string + "' はUUIDとして不正です");
                }

                final long mostSignificantBits = uuid.getMostSignificantBits();
                final long leastSignificantBits = uuid.getLeastSignificantBits();
                final int[] array = new int[]{
                    (int) (mostSignificantBits >> 32),
                    (int) mostSignificantBits,
                    (int) (leastSignificantBits >> 32),
                    (int) leastSignificantBits
                };

                return new MojangsonIntArray(array);
            }
            else {
                throw exception("関数 'uuid()' の第一引数の型 '" + value.getType() + "' が正しくありません");
            }
        }
    ));

    private static final Set<Character> SYMBOLS_ON_STRING = new HashSet<>();

    private static final Set<Character> STOPPER_ON_STRING = new HashSet<>();

    static {
        SYMBOLS_ON_STRING.addAll(WHITESPACE);
        SYMBOLS_ON_STRING.add(COMMA);
        SYMBOLS_ON_STRING.add(COLON);
        SYMBOLS_ON_STRING.add(SEMICOLON);
        SYMBOLS_ON_STRING.add(ESCAPE);
        SYMBOLS_ON_STRING.addAll(QUOTES);
        SYMBOLS_ON_STRING.add(FUNCTION_BRACES[0]);
        SYMBOLS_ON_STRING.add(FUNCTION_BRACES[1]);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[0]);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[1]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[0]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[1]);
        SYMBOLS_ON_STRING.addAll(SIGNS);
        SYMBOLS_ON_STRING.add(DECIMAL_POINT);

        STOPPER_ON_STRING.addAll(WHITESPACE);
        STOPPER_ON_STRING.add(FUNCTION_BRACES[0]);
        STOPPER_ON_STRING.add(FUNCTION_BRACES[1]);
        STOPPER_ON_STRING.add(COMPOUND_BRACES[1]);
        STOPPER_ON_STRING.add(ARRAY_LIST_BRACES[1]);
        STOPPER_ON_STRING.add(COMMA);
        STOPPER_ON_STRING.add(COLON);
    }

    private String text;

    private int location = 0;

    private MojangsonParser(String text) {
        this.text = text;
    }

    public MojangsonParser() {
        this.text = "";
    }

    private boolean isOver() {
        return location >= text.length();
    }

    private MojangsonParseException exception(String message) {
        return new MojangsonParseException(message, text, location);
    }

    private char next(boolean ignorable) {
        if (isOver()) {
            throw exception("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location++);

        if (ignorable) {
            return WHITESPACE.contains(next) ? next(true) : next;
        }
        return next;
    }

    private void back() {
        if (location > 0) location--;
    }

    private void whitespace() {
        if (isOver()) return;

        final char current = text.charAt(location++);

        if (WHITESPACE.contains(current)) {
            whitespace();
        }
        else {
            location--;
        }
    }

    private boolean test(String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean next(String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            whitespace();
            return true;
        }

        return false;
    }

    private boolean next(char next) {
        return next(String.valueOf(next));
    }

    private void expect(String next) {
        if (!next(next)) {
            throw exception("期待された文字列は" + next + "でしたが、テストが偽を返しました");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
    }

    private String string() {
        final StringBuilder sb = new StringBuilder();
        char current = next(true);

        if (QUOTES.contains(current)) {
            final char quote = current;
            char previous = current;
            current = next(false);

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    sb.delete(sb.length() - 1, sb.length());
                }

                sb.append(current);

                previous = current;
                current = next(false);
            }
        }
        else {
            while (!STOPPER_ON_STRING.contains(current)) {
                if (SYMBOLS_ON_STRING.contains(current)) {
                    throw exception("クオーテーションで囲まれていない文字列において利用できない文字("+ current +")を検出しました");
                }

                sb.append(current);
                if (isOver()) return sb.toString();
                current = next(false);
            }

            back();
        }

        return sb.toString();
    }

    private @Nullable MojangsonNumber<?> number() {
        final int loc = location;

        final StringBuilder sb = new StringBuilder();
        char current = next(false);

        if (SIGNS.contains(current) || NUMBERS.contains(current)) {
            sb.append(current);
        }
        else {
            location = loc;
            return null;
        }

        boolean decimalPointAppeared = false;
        boolean isUnsigned = false;

        Function<String, ? extends Number> parser = null;

        while (!isOver()) {
            current = next(false);

            if (NUMBERS.contains(current)) {
                sb.append(current);
            }
            else if (current == DECIMAL_POINT && !decimalPointAppeared) {
                sb.append(current);
                decimalPointAppeared = true;
            }
            else if (SYMBOLS_ON_STRING.contains(current)) {
                back();
                break;
            }
            else if (current == SIGNED) {
                isUnsigned = false;
            }
            else if (current == UNSIGNED) {
                isUnsigned = true;
            }
            else if (Character.isAlphabetic(current) && SIGNED_NUMBER_PARSERS.containsKey(Character.toLowerCase(current)) && !isUnsigned) {
                parser = SIGNED_NUMBER_PARSERS.get(Character.toLowerCase(current));
                break;
            }
            else if (Character.isAlphabetic(current) && UNSIGNED_NUMBER_PARSERS.containsKey(Character.toLowerCase(current)) && isUnsigned) {
                parser = UNSIGNED_NUMBER_PARSERS.get(Character.toLowerCase(current));
                break;
            }
            else {
                location = loc;
                return null;
            }
        }

        if (!NUMBERS.contains(sb.charAt(sb.length() - 1))) {
            throw exception("数値は数字で終わる必要があります");
        }

        if (parser == null) {
            parser = decimalPointAppeared
                ? DEFAULT_DECIMAL_PARSER
                : isUnsigned
                    ? DEFAULT_UNSIGNED_INT_PARSER
                    : DEFAULT_SIGNED_INT_PARSER;
        }

        return MojangsonNumber.upcastedValueOf(parser.apply(sb.toString()));
    }

    private MojangsonCompound compound() {
        expect(COMPOUND_BRACES[0]);

        final MojangsonCompound compound = new MojangsonCompound();

        if (next(COMPOUND_BRACES[1])) {
            return compound;
        }

        keyValues(compound);

        expect(COMPOUND_BRACES[1]);

        return compound;
    }

    private MojangsonIterable<?> iterable() {
        expect(ARRAY_LIST_BRACES[0]);

        final MojangsonList list = new MojangsonList();
        Function<MojangsonList, MojangsonIterable<? extends MojangsonNumber<?>>> arrayConverter = null;

        for (char c : PRIMITIVE_ARRAY_CONVERTERS.keySet()) {
            final String prefix = String.valueOf(c) + SEMICOLON;

            if (next(prefix)) {
                arrayConverter = PRIMITIVE_ARRAY_CONVERTERS.get(c);
                break;
            }
        }

        if (next(ARRAY_LIST_BRACES[1])) {
            return arrayConverter == null ? list : arrayConverter.apply(list);
        }

        elements(list);

        expect(ARRAY_LIST_BRACES[1]);

        return arrayConverter == null ? list : arrayConverter.apply(list);
    }

    private void keyValues(MojangsonCompound compound) {
        final String key = string();
        if (!next(COLON)) throw exception("コロンが必要です");
        compound.set(key, value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) keyValues(compound);
        else if (commaOrBrace == COMPOUND_BRACES[1]) back();
        else throw exception("閉じ括弧が見つかりません");
    }

    private void elements(MojangsonList list) {
        list.add(value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) elements(list);
        else if (commaOrBrace == ARRAY_LIST_BRACES[1]) back();
        else throw exception("閉じ括弧が見つかりません");
    }

    private MojangsonValue<?> function(String name, MojangsonFunctionalOperator function) {
        expect(name);
        expect(FUNCTION_BRACES[0]);

        final List<MojangsonValue<?>> arguments = new ArrayList<>();

        do {
            arguments.add(value());
        }
        while (next(COMMA));

        expect(FUNCTION_BRACES[1]);

        return function.apply(arguments);
    }

    private MojangsonValue<?> value() {
        if (test(COMPOUND_BRACES[0])) {
            return compound();
        }
        else if (test(ARRAY_LIST_BRACES[0])) {
            return (MojangsonValue<?>) iterable();
        }
        else {
            final MojangsonNumber<?> number = number();
            if (number != null) {
                return number;
            }

            for (final var entry : functions.entrySet()) {
                final String name = entry.getKey();
                final var function = entry.getValue();

                if (test(name + FUNCTION_BRACES[0])) {
                    return function(name, function);
                }
            }

            if (next(BOOLEANS[0])) {
                return MojangsonByte.valueOf((byte) 0);
            }
            else if (next(BOOLEANS[1])) {
                return MojangsonByte.valueOf((byte) 1);
            }
            else if (next(MojangsonNull.NULL.toString())) {
                return MojangsonNull.NULL;
            }

            final String string = string();

            return MojangsonString.valueOf(string);
        }
    }

    private void finish() {
        if (!isOver()) throw exception("解析終了後、末尾に無効な文字列(" + text.substring(location) + ")を検出しました");
        location = 0;
    }

    private MojangsonValue<?> parse() {
        final MojangsonValue<?> value = value();
        finish();
        return value;
    }

    public MojangsonValue<?> parse(String text) {
        this.text = text;
        return parse();
    }

    public void register(String name, MojangsonFunctionalOperator function) {
        functions.put(name, function);
    }

    private static <T> T parseAs(String text, Class<T> clazz) {
        final MojangsonParser parser = new MojangsonParser(text);
        final MojangsonValue<?> value = parser.parse();

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        else throw new IllegalStateException("期待された型(" + clazz.getName() + ")と取得した値(" + value.getClass().getName() + ")が一致しません");
    }

    public static MojangsonValue<?> object(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonValue.class);
    }

    public static MojangsonCompound compound(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonCompound.class);
    }

    public static MojangsonList list(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonList.class);
    }

    public static MojangsonByteArray byteArray(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonByteArray.class);
    }

    public static MojangsonIntArray intArray(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonIntArray.class);
    }

    public static MojangsonLongArray longArray(String text) throws MojangsonParseException {
        return parseAs(text, MojangsonLongArray.class);
    }
}
