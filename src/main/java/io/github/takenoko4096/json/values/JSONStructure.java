package io.github.takenoko4096.json.values;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface JSONStructure {
    boolean isEmpty();

    boolean clear();

    JSONStructure copy();
}
