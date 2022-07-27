package com.github.rpbt.serve;

import com.github.rpbt.core.ResourceNotFoundException;
import com.github.rpbt.core.http.HttpRepository;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An extension of {@link Serve} that wraps calls to {@link Serve#send(String,
 * String, HttpRepository, BiConsumer)} asynchronously.
 */
public class AsyncServe extends Serve {
    private final Consumer<Runnable> async;
    private final Consumer<Runnable> sync;

    /**
     * Create a new AsyncServe instance.
     *
     * @param async A consumer that runs the runnable asynchronously.
     * @param sync A consumer that runs the runnable synchronously.
     */
    public AsyncServe(Consumer<Runnable> async, Consumer<Runnable> sync) {
        this.async = async;
        this.sync = sync;
    }

    /**
     * Get the url and SHA-1 hash for sending a resource pack to a player.
     * <p>
     * The future will complete when the consumer has been called.
     * <p>
     * The future will complete exceptionally with {@link ResourceNotFoundException} if
     * the pack could not be found. Or {@link IOException} if an I/O error occurs.
     * <p>
     * This method will call {@link Serve#send(String, String, HttpRepository,
     * BiConsumer)} asynchronously using the first constructor parameter. When the HTTP
     * request is done, the consumer will be called on the main thread using the second
     * constructor parameter.
     *
     * @param id The id of the pack.
     * @param version The version of the pack.
     * @param repository The repository to get the pack from.
     * @param consumer The consumer to call with the url and hash, in that order.
     * @return The future.
     */
    public CompletableFuture<Void> sendAsync(String id, String version, HttpRepository repository, BiConsumer<String, String> consumer) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.async.accept(() -> {
            try {
                this.send(id, version, repository, (url, hash) ->
                    this.sync.accept(() -> {
                        consumer.accept(url, hash);
                        future.complete(null);
                    })
                );
            } catch (IOException | ResourceNotFoundException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
