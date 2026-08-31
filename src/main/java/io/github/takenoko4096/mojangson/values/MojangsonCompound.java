package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.*;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * mojangsonにおけるコンパウンドを表現します。
 */
public class MojangsonCompound extends MojangsonValue<Map<String, MojangsonValue<?>>> implements MojangsonStructure {
    /**
     * String と MojnagsonValue&lt;?&gt; の Map から MojangsonCompound を作成します。
     * @param map 元となる Map
     */
    public MojangsonCompound(Map<String, MojangsonValue<?>> map) {
        super(map);
    }

    /**
     * 空の MojnagsonCompound を作成します。
     */
    public MojangsonCompound() {
        this(new HashMap<>());
    }

    @Override
    public MojangsonValueType<MojangsonCompound> getType() {
        return MojangsonValueTypes.COMPOUND;
    }

    /**
     * 引数に渡されたキーに対応する位置が存在するかを返します。
     * @param key キー
     * @return 存在する場合 true
     */
    public boolean has(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * キーに紐づけられた値の型を返します。
     * @param key キー
     * @return キーに紐づけられた値の型
     * @throws IllegalArgumentException キーに対応する位置が存在しない場合
     */
    public MojangsonValueType<?> getTypeOf(String key) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return MojangsonValueType.of(value.get(key));
    }

    /**
     * キーに紐づけられた値を返します。
     * @param key キー
     * @param type 期待する型
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     * @throws IllegalArgumentException キーに対応する位置が存在しないか、型が予期しないものの場合
     */
    public <T extends MojangsonValue<?>> T getOrThrow(String key, MojangsonValueType<T> type) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません: " + getTypeOf(key));
        }

        return type.toMojangson(value.get(key));
    }

    /**
     * キーに紐づけられた値を返します。キーに対応する位置が存在しないか、型が予期しないものの場合nullを返します。
     * @param key キー
     * @param type 期待する型
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> @Nullable T getOrNull(String key, MojangsonValueType<T> type) {
        if (has(key)) {
            if (getTypeOf(key).equals(type)) {
                return getOrThrow(key, type);
            }
            else return null;
        }
        else return null;
    }

    /**
     * キーに紐づけられた値を返します。キーに対応する位置が存在しないか、型が予期しないものの場合デフォルト値を返します。
     * @param key キー
     * @param type 期待する型
     * @param defaultValue デフォルト値
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> T getOrDefault(String key, MojangsonValueType<T> type, Object defaultValue) {
        return Objects.requireNonNullElse(getOrNull(key, type), type.toMojangson(defaultValue));
    }

    /**
     * キーに任意の値を紐づけます。
     * @param key キー
     * @param value 値
     */
    public void set(String key, Object value) {
        this.value.put(key, MojangsonValue.valueOf(value));
    }

    /**
     * キーに紐づけられた値を削除します。
     * @param key キー
     * @return 削除に成功した場合 true
     */
    public boolean delete(String key) {
        if (has(key)) {
            value.remove(key);
            return true;
        }
        else return false;
    }

    @Override
    public boolean clear() {
        if (isEmpty()) {
            return false;
        }
        else {
            value.clear();
            return true;
        }
    }

    /**
     * このコンパウンドが持つキーの集合を返します。
     * @return すべてのキーを含む Set&lt;String&gt;
     */
    public Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    /**
     * このオブジェクトをMap&lt;String, MojangsonValue&lt;?&gt;&gt;に変換して返します。
     * @return Map&lt;String, MojangsonValue&lt;?&gt;&gt;
     */
    public Map<String, MojangsonValue<?>> toMap() {
        return Map.copyOf(value);
    }

    /**
     * このオブジェクトを再帰的にMap&lt;String, Object&gt;に変換します。
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> toMapRecursively() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final MojangsonValueType<?> type = getTypeOf(key);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = getOrThrow(key, MojangsonValueTypes.COMPOUND);
                map.put(key, compound.toMapRecursively());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = getOrThrow(key, MojangsonValueTypes.LIST);
                map.put(key, list.toListRecursively());
            }
            else if (value.get(key) instanceof MojangsonArray<?, ?> array) {
                map.put(key, array.toArray());
            }
            else if (value.get(key) instanceof MojangsonPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public MojangsonCompound copy() {
        return MojangsonValueTypes.COMPOUND.toMojangson(toMapRecursively());
    }

    /**
     * ある構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体
     * @return 部分構造であれば true
     */
    public boolean isSuperOf(MojangsonCompound other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final MojangsonValue<?> conditionValue = other.getOrThrow(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case MojangsonCompound jsonObject -> {
                        if (!getOrThrow(key, MojangsonValueTypes.COMPOUND).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case MojangsonList jsonArray -> {
                        if (!getOrThrow(key, MojangsonValueTypes.LIST).isSuperOf(jsonArray)) {
                            return false;
                        }
                    }
                    default -> {
                        if (!getOrThrow(key, getTypeOf(key)).equals(conditionValue)) {
                            return false;
                        }
                    }
                }
            }
            else return false;
        }

        return true;
    }

    /**
     * 引数に渡されたパスに対応する位置が存在するかを返します。
     * @param path パス
     * @return 存在する場合 true
     */
    public boolean has(MojangsonPath path) {
        try {
            final Boolean t = path.access(
                this,
                false,
                false,
                MojangsonCompound::has,
                MojangsonList::has
            );

            if (t == null) {
                return false;
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * 引数に渡されたパスに紐づけられた値の型を返します。
     * @param path パス
     * @return パスに紐づけられた値の型
     * @throws IllegalArgumentException パスに対応する位置が存在しない場合
     */
    public MojangsonValueType<?> getTypeOf(MojangsonPath path) {
        try {
            final MojangsonValueType<?> t = path.access(
                this,
                false,
                true,
                MojangsonCompound::getTypeOf,
                MojangsonList::getTypeAt
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * パスに紐づけられた値を返します。
     * @param path パス
     * @param type 期待する型
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     * @throws IllegalArgumentException パスが存在しないか、型が予期しないものの場合
     */
    public <T extends MojangsonValue<?>> T getOrThrow(MojangsonPath path, MojangsonValueType<T> type) {
        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrThrow(p, type),
                (s, p) -> s.getOrThrow(p, type)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * パスに紐づけられた値を返します。パスが存在しない、または型の不一致の場合にnullを返します。
     * @param path パス
     * @param type 期待する型
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> @Nullable T getOrNull(MojangsonPath path, MojangsonValueType<T> type) {
        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrNull(p, type),
                (s, p) -> s.getOrNull(p, type)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException _) {
            return null;
        }
    }

    /**
     * パスに紐づけられた値を返します。パスが存在しない、または型の不一致の場合にデフォルト値を返します。
     * @param path パス
     * @param type 期待する型
     * @param defaultValue デフォルト値
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> @Nullable T getOrDefault(MojangsonPath path, MojangsonValueType<T> type, Object defaultValue) {
        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrDefault(p, type, defaultValue),
                (s, p) -> s.getOrDefault(p, type, defaultValue)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * パスに対応する位置を削除します。
     * @param path パス
     * @return 削除に成功した場合 true
     */
    public boolean delete(MojangsonPath path) {
        try {
            final Boolean t = path.access(
                this,
                false,
                true,
                MojangsonCompound::delete,
                MojangsonList::delete
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (MojangsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * パスに任意の値を紐づけます。
     * @param path パス
     * @param value 値
     */
    public void set(MojangsonPath path, Object value) {
        try {
            path.access(
                this,
                false,
                true,
                (s, p) -> {
                    s.set(p, value);
                    return null;
                },
                (s, p) -> {
                    s.set(p, value);
                    return null;
                }
            );
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Map&lt;String, ?&gt; を MojangsonCompound に変換します。
     * @param value Map&lt;String, ?&gt;
     * @return MojangsonCompound
     */
    public static MojangsonCompound valueOf(Map<?, ?> value) {
        final var map = new HashMap<String, MojangsonValue<?>>();

        for (final var kv : value.entrySet()) {
            map.put(kv.getKey().toString(), valueOf(kv.getValue()));
        }

        return new MojangsonCompound(map);
    }
}
