package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MojangsonIterable<T extends MojangsonValue<?>> extends MojangsonStructure, Iterable<T> {
    @Override
    boolean isEmpty();

    boolean has(int index);

    int length();

    @Override
    boolean clear();

    @Override
    MojangsonIterable<T> copy();
}
