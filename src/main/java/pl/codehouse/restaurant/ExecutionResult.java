package pl.codehouse.restaurant;

import java.util.Optional;

public record ExecutionResult<T>(Optional<T> value, RuntimeException exception) {

    public static <T> ExecutionResult<T> success(T value) {
        return new ExecutionResult<>(Optional.of(value), null);
    }

    // Static factory method for failure
    public static <T> ExecutionResult<T> failure(RuntimeException exception) {
        return new ExecutionResult<>(Optional.empty(), exception);
    }

    public boolean isSuccess() {
        return value.isPresent();
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public T handle() {
        return value.orElseThrow(() -> exception);
    }
}