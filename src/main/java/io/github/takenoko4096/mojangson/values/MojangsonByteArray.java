package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@NullMarked
public class MojangsonByteArray extends MojangsonArray<byte[], MojangsonByte> {
    public MojangsonByteArray(byte[] value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.BYTE_ARRAY;
    }

    @Override
    public MojangsonByteArray copy() {
        return from(listView());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public boolean clear() {
        boolean successful = false;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                value[i] = 0;
                successful = true;
            }
        }
        return successful;
    }

    @Override
    public Iterator<MojangsonByte> iterator() {
        final List<MojangsonByte> bytes = new ArrayList<>();
        for (final byte byteValue : value) {
            bytes.add(MojangsonByte.valueOf(byteValue));
        }
        return bytes.iterator();
    }

    @Override
    public String toString() {
        return "byte" + Arrays.toString(value);
    }

    @Override
    public byte[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public MojangsonList listView() {
        return getView((arr, ind, val) -> arr[ind] = (byte) val);
    }

    /**
     * MojangsonListからMojangsonByteArrayへの変換を試みます。
     * @param list MojangsonByteのみを要素に持つリスト。
     * @return MojangsonByteArray。
     */
    public static MojangsonByteArray from(MojangsonList list) {
        final byte[] bytes = new byte[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.BYTE)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            bytes[i] = list.get(i, MojangsonValueTypes.BYTE).byteValue();
        }

        return new MojangsonByteArray(bytes);
    }
}
