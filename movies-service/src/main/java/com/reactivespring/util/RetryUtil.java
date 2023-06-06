package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetryUtil {
    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                // retry only for 5xx, leaves the 4xx alone
                .filter(ex -> ex instanceof MoviesInfoServerException ||
                        ex instanceof ReviewsServerException)
                // anytime exception happens, we need to propagate the root cause of the issue so that client knows the actual error
                // Without this, you will get vague errors
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }
}
