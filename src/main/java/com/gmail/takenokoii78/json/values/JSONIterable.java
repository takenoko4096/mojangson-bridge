package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.JSONValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface JSONIterable<T extends JSONValue<?>> extends JSONStructure, Iterable<T> {
    @Override
    boolean isEmpty();

    boolean has(int index);

    int length();

    boolean delete(int index);

    @Override
    boolean clear();

    @Override
    JSONIterable<T> copy();
}
