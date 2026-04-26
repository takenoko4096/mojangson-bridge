package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonPath;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NullMarked
public class MojangsonCompound extends MojangsonValue<Map<String, MojangsonValue<?>>> implements MojangsonStructure {
    public MojangsonCompound(Map<String, MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonCompound() {
        this(new HashMap<>());
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.COMPOUND;
    }

    public boolean has(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    public MojangsonValueType<?> getTypeOf(String key) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return MojangsonValueType.get(value.get(key));
    }

    public <T extends MojangsonValue<?>> T get(String key, MojangsonValueType<T> type) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません: " + getTypeOf(key));
        }

        return type.toMojangson(value.get(key));
    }

    public void set(String key, Object value) {
        this.value.put(key, MojangsonValueType.get(value).toMojangson(value));
    }

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

    public Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final MojangsonValueType<?> type = getTypeOf(key);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = get(key, MojangsonValueTypes.COMPOUND);
                map.put(key, compound.toMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = get(key, MojangsonValueTypes.LIST);
                map.put(key, list.toList());
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
    public MojangsonStructure copy() {
        return MojangsonValueTypes.COMPOUND.toMojangson(toMap());
    }

    public boolean isSuperOf(MojangsonCompound other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final MojangsonValue<?> conditionValue = other.get(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case MojangsonCompound jsonObject -> {
                        if (!get(key, MojangsonValueTypes.COMPOUND).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case MojangsonList jsonArray -> {
                        if (!get(key, MojangsonValueTypes.LIST).isSuperOf(jsonArray)) {
                            return false;
                        }
                    }
                    default -> {
                        if (!get(key, getTypeOf(key)).equals(conditionValue)) {
                            return false;
                        }
                    }
                }
            }
            else return false;
        }

        return true;
    }

    public boolean has(MojangsonPath path) {
        try {
            return Boolean.TRUE.equals(path.access(this, MojangsonPath.MojangsonPathReference::has, false));
        }
        catch (MojangsonPath.MojangsonInaccessiblePathException e) {
            return false;
        }
    }

    public MojangsonValueType<?> getTypeOf(MojangsonPath path) {
        try {
            final MojangsonValueType<?> type = path.access(this, MojangsonPath.MojangsonPathReference::getType, false);

            if (type == null) {
                throw new IllegalStateException("型の取得に失敗しました: アクセスの戻り値が null です");
            }

            return type;
        }
        catch (MojangsonPath.MojangsonInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T extends MojangsonValue<?>> T get(MojangsonPath path, MojangsonValueType<T> type) {
        try {
            final T value = path.access(this, reference -> reference.get(type), false);

            if (value == null) {
                throw new IllegalStateException("値の取得に失敗しました: アクセスの戻り値が null です");
            }

            return value;
        }
        catch (MojangsonPath.MojangsonInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean delete(MojangsonPath path) {
        try {
            return Boolean.TRUE.equals(path.access(this, MojangsonPath.MojangsonPathReference::delete, false));
        }
        catch (MojangsonPath.MojangsonInaccessiblePathException e) {
            return false;
        }
    }

    public void set(MojangsonPath path, Object value) {
        try {
            path.access(this, reference -> {
                reference.set(value);
                return null;
            }, true);
        }
        catch (MojangsonPath.MojangsonInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }
}
