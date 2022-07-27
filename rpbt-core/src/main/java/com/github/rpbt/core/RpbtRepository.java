package com.github.rpbt.core;

import com.github.rpbt.core.http.HttpRepository;

import java.io.IOException;
import java.io.InputStream;

/**
 * An rpbt repository containing resources.
 */
public interface RpbtRepository {
    /**
     * Create an {@link RpbtRepository} that fetches resources from a remote HTTP server.
     *
     * @param url The url to root of the repository.
     * @return The created {@link HttpRepository}.
     * @throws IllegalArgumentException If the url is invalid or does not use http(s).
     */
    static HttpRepository http(String url) {
        return new HttpRepository(url);
    }

    /**
     * Get a resource from this repository.
     *
     * @param type The type of resource.
     * @param id The id of the resource.
     * @param version The version of the resource.
     * @return The resource.
     * @throws ResourceNotFoundException If the resource could not be found.
     * @throws IOException If an I/O error occurs.
     */
    Resource getResource(ResourceType type, String id, String version) throws IOException, ResourceNotFoundException;

    /**
     * Read the zip file of a resource and get the input stream.
     *
     * @param resource The resource to read.
     * @return The input stream containing the zip file.
     * @throws IOException If an I/O error occurs.
     */
    InputStream readResource(Resource resource) throws IOException;
}
