package io.github.takenoko4096.mojangson.values;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MojangsonStructure {
    boolean isEmpty();

    boolean clear();

    MojangsonStructure copy();
}
