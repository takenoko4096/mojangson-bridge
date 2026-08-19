package io.github.takenoko4096.json;

/**
 * JsonParser または JsonPathParser によって投げられるパース例外。
 * @see JsonParser
 * @see JsonPathParser
 */
public class JsonParseException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     * @param json 元のjson文字列
     * @param location 例外の発生位置
     */
    protected JsonParseException(String message, String json, int location) {
        super(
            String.format(
                message + "; pos: %s >> %s << %s",
                json.substring(Math.max(0, location - 8), Math.max(0, location)),
                location >= json.length() ? "" : json.charAt(location),
                json.substring(Math.min(location + 1, json.length()), Math.min(location + 8, json.length()))
            )
        );
    }
}
