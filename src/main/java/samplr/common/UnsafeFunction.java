package samplr.common;

import java.util.function.Function;

@FunctionalInterface
public interface UnsafeFunction<T, R, E extends Exception> {
    R apply(T t) throws E;

    static <T, R, E extends Exception> Function<T, R> unsafe(UnsafeFunction<T, R, E> unsafeFunction) {
        return arg -> {
            try {
                return unsafeFunction.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
