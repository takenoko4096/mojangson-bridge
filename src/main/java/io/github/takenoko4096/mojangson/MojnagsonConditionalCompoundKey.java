package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonCompound;

/**
 * mojangsonパスにおける条件付きのキーアクセスを表現します。
 * @param name キーの名前。
 * @param compound 条件となるコンパウンド。
 */
public record MojnagsonConditionalCompoundKey(String name, MojangsonCompound compound) {
}
