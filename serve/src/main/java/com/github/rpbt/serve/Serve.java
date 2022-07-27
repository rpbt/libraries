package com.github.rpbt.serve;

import com.github.rpbt.core.ResourceNotFoundException;
import com.github.rpbt.core.ResourceType;
import com.github.rpbt.core.http.HttpRepository;
import com.github.rpbt.core.http.HttpResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Resolve resource packs from rpbt repositories to a url and hash for sending to
 * players.
 */
public class Serve {
    private final Map<String, HttpResource> cache = new HashMap<>();

    /**
     * Check if a pack is cached in this {@link Serve} instance.
     * <p>
     * If a pack is cached, {@link #send(String, String, HttpRepository, BiConsumer)}
     * will not perform HTTP operations.
     *
     * @param id The id of the pack.
     * @param version The version of the pack.
     * @return Whether the pack is cached.
     */
    public boolean isCached(String id, String version) {
        String key = id + ':' + version;
        return this.cache.containsKey(key);
    }

    /**
     * Get the url and SHA-1 hash for sending a resource pack to a player.
     * <p>
     * It is recommended to call this method asynchronously as it may perform HTTP
     * operations if the pack was not cached.
     * <p>
     * Or check if a pack is cached using {@link #isCached(String, String)} and if it
     * is, this method will not perform HTTP operations.
     * <p>
     * In case this method is called asynchronously, ensure the operations you perform
     * in the consumer can be called asynchronously, otherwise make sure to synchronize
     * back inside the consumer.
     *
     * @param id The id of the pack.
     * @param version The version of the pack.
     * @param repository The repository to get the pack from.
     * @param consumer The consumer to call with the url and hash, in that order.
     * @throws IOException If an I/O error occurs.
     * @throws ResourceNotFoundException If the pack could not be found.
     * @see AsyncServe#sendAsync(String, String, HttpRepository, BiConsumer)
     */
    public void send(String id, String version, HttpRepository repository, BiConsumer<String, String> consumer) throws IOException, ResourceNotFoundException {
        String key = id + ':' + version;
        HttpResource resource;
        if (this.cache.containsKey(key)) {
            resource = this.cache.get(key);
        } else {
            resource = repository.getResource(ResourceType.DEPENDENCY, id, version);
            this.cache.put(key, resource);
        }
        consumer.accept(repository.getUrl(resource), resource.getSha1());
    }
}
