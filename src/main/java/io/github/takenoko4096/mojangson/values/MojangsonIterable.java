package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
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
