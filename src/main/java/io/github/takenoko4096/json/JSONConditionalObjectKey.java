package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JSONObject;

/**
 * jsonパスにおける条件付きのキーアクセスを表現します。
 * @param name キーの名前。
 * @param object 条件となるオブジェクト。
 */
public record JSONConditionalObjectKey(String name, JSONObject object) {
}
