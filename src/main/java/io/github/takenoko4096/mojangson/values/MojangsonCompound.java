package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * mojangsonにおけるコンパウンドを表現します。
 */
@NullMarked
public class MojangsonCompound extends MojangsonValue<Map<String, MojangsonValue<?>>> implements MojangsonStructure {
    /**
     * StringとMojnagsonValueのMapからMojangsonCompoundを作成します。
     * @param map 元となるMap。
     */
    public MojangsonCompound(Map<String, MojangsonValue<?>> map) {
        super(map);
    }

    /**
     * 空のMojnagsonCompoundを作成します。
     */
    public MojangsonCompound() {
        this(new HashMap<>());
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.COMPOUND;
    }

    /**
     * 引数に渡されたキーが存在するかを返します。
     * @param key キー。
     * @return 存在する場合、真。
     */
    public boolean has(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * 引数に渡されたキーの型を返します。
     * @param key キー。
     * @return キーに紐づけられた値の型。
     * @throws IllegalArgumentException キーが存在しない場合。
     */
    public MojangsonValueType<?> getTypeOf(String key) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return MojangsonValueType.of(value.get(key));
    }

    /**
     * 引数に渡されたキーに紐づけられた値を返します。
     * @param key キー。
     * @param type 期待する型。
     * @return キーに紐づけられた値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException キーが存在しないか、型が予期しないものの場合。
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

    public <T extends MojangsonValue<?>> @Nullable T getOrNull(String key, MojangsonValueType<T> type) {
        if (has(key)) {
            if (getTypeOf(key).equals(type)) {
                return getOrThrow(key, type);
            }
            else return null;
        }
        else return null;
    }

    public <T extends MojangsonValue<?>> T getOrDefault(String key, MojangsonValueType<T> type, T defaultValue) {
        return Objects.requireNonNullElse(getOrNull(key, type), defaultValue);
    }

    /**
     * 引数に渡されたキーに任意の値を紐づけます。
     * @param key キー。
     * @param value 値。
     */
    public void set(String key, Object value) {
        this.value.put(key, MojangsonValueType.of(value).toMojangson(value));
    }

    /**
     * 引数に渡されたキーを削除します。
     * @param key キー。
     * @return 削除に成功した場合、真。
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
     * @return すべてのキーのSet。
     */
    public Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    public Map<String, MojangsonValue<?>> toMap() {
        return Map.copyOf(value);
    }

    /**
     * このコンパウンドを再帰的にMapに変換します。
     * @return Map形式のディープコピー。
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
    public MojangsonCompound deepCopy() {
        return MojangsonValueTypes.COMPOUND.toMojangson(toMapRecursively());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
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
     * 引数に渡されたパスが存在するかを返します。
     * @param path パス。
     * @return 存在する場合、真。
     */
    public boolean has(MojangsonPath path) {
        try {
            return Boolean.TRUE.equals(path.access(this, MojangsonPath.MojangsonPathReference::has, false));
        }
        catch (MojangsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * 引数に渡されたパスの型を返します。
     * @param path パス。
     * @return パスに紐づけられた値の型。
     * @throws IllegalArgumentException パスが存在しない場合。
     */
    public MojangsonValueType<?> getTypeOf(MojangsonPath path) {
        try {
            final MojangsonValueType<?> type = path.access(this, MojangsonPath.MojangsonPathReference::getType, false);

            if (type == null) {
                throw new IllegalStateException("型の取得に失敗しました: アクセスの戻り値が null です");
            }

            return type;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引数に渡されたパスに紐づけられた値を返します。
     * @param path パス。
     * @param type 期待する型。
     * @return パスに紐づけられた値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException パスが存在しないか、型が予期しないものの場合。
     */
    public <T extends MojangsonValue<?>> T getOrThrow(MojangsonPath path, MojangsonValueType<T> type) {
        try {
            final T value = path.access(this, reference -> reference.getOrThrow(type), false);
            if (value == null) {
                throw new IllegalArgumentException("値の取得に失敗しました: アクセスの戻り値が null です");
            }
            return value;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T extends MojangsonValue<?>> @Nullable T getOrNull(MojangsonPath path, MojangsonValueType<T> type) {
        try {
            return path.access(this, reference -> reference.getOrNull(type), false);
        }
        catch (MojangsonPathUnableToAccessException _) {
            return null;
        }
    }

    public <T extends MojangsonValue<?>> @Nullable T getOrDefault(MojangsonPath path, MojangsonValueType<T> type, T defaultValue) {
        try {
            final T value = path.access(this, reference -> reference.getOrDefault(type, defaultValue), false);
            if (value == null) {
                return defaultValue;
            }
            return value;
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 引数に渡されたパスを削除します。
     * @param path パス。
     * @return 削除に成功した場合、真。
     */
    public boolean delete(MojangsonPath path) {
        try {
            return Boolean.TRUE.equals(path.access(this, MojangsonPath.MojangsonPathReference::delete, false));
        }
        catch (MojangsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * 引数に渡されたパスに任意の値を紐づけます。
     * @param path パス。
     * @param value 値。
     */
    public void set(MojangsonPath path, Object value) {
        try {
            path.access(this, reference -> {
                reference.set(value);
                return null;
            }, true);
        }
        catch (MojangsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * MapをMojangsonCompoundに変換します。
     * @param value Map。
     * @return MojangsonCompound。
     */
    public static MojangsonCompound valueOf(Map<?, ?> value) {
        final var map = new HashMap<String, MojangsonValue<?>>();

        for (final var kv : value.entrySet()) {
            map.put(kv.getKey().toString(), valueOf(kv.getValue()));
        }

        return new MojangsonCompound(map);
    }
}
