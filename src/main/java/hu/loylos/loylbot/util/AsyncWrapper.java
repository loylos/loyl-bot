package hu.loylos.loylbot.util;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@UtilityClass
public class AsyncWrapper {

    public <T> Mono<T> execute(Callable<T> callable) {
        return Mono.fromCallable(callable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public <T> Mono<T> executeOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(AsyncWrapper::unwrapOptional)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public <T> Flux<T> executeList(Callable<List<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private <T> Mono<T> unwrapOptional(Optional<T> optional) {
        if (optional.isEmpty()) {
            return Mono.empty();
        } else {
            return Mono.just(optional.get());
        }
    }
}