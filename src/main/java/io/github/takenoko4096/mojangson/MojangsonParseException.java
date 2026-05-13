package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.NullMarked;

/**
 * MojangsonParserまたはMojangsonPathParserによって投げられるパース例外。
 * @see MojangsonParser
 * @see MojangsonPathParser
 */
@NullMarked
public class MojangsonParseException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     * @param mojangson 元のmojangson文字列。
     * @param location 例外の発生位置。
     */
    protected MojangsonParseException(String message, String mojangson, int location) {
        super(
            String.format(
                message + "; pos: %s >> %s << %s",
                mojangson.substring(Math.max(0, location - 8), Math.max(0, location)),
                location >= mojangson.length() ? "" : mojangson.charAt(location),
                mojangson.substring(Math.min(location + 1, mojangson.length()), Math.min(location + 8, mojangson.length()))
            )
        );
    }
}
