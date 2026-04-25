package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@NullMarked
public class MojangsonLongArray extends MojangsonArray<long[], MojangsonLong> {
    public MojangsonLongArray(long[] value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.LONG_ARRAY;
    }

    @Override
    public MojangsonLongArray copy() {
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
    public Iterator<MojangsonLong> iterator() {
        final List<MojangsonLong> longs = new ArrayList<>();
        for (final long longValue : value) {
            longs.add(MojangsonLong.valueOf(longValue));
        }
        return longs.iterator();
    }

    @Override
    public String toString() {
        return "long" + Arrays.toString(value);
    }

    @Override
    public long[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public MojangsonList listView() {
        return getView((arr, ind, val) -> arr[ind] = (long) val);
    }

    public static MojangsonLongArray from(MojangsonList list) {
        final long[] longs = new long[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.LONG)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            longs[i] = list.get(i, MojangsonValueTypes.LONG).longValue();
        }

        return new MojangsonLongArray(longs);
    }
}
