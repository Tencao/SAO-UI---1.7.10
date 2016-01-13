package com.tencao.saoui.util;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);

        return (l, r, i) -> {
            accept(l, r, i);
            after.accept(l, r, i);
        };
    }
}
