package pl.codehouse.restaurant;

import reactor.core.publisher.Mono;

public interface Command<T, R> {
    Mono<ExecutionResult<R>> execute(Context<T> context);
}
