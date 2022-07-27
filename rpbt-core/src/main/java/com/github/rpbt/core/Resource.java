package com.github.rpbt.core;

import com.github.rpbt.core.http.HttpRepository;

/**
 * Information about a resource from an {@link RpbtRepository}.
 * <p>
 * This only contains information from the {@code resource.json} file. Use an
 * {@link RpbtRepository} to perform actions on the zip file of this resource, for
 * example {@link HttpRepository#getUrl(Resource)} or {@link RpbtRepository#readResource(Resource)}.
 */
public class Resource {
    private final ResourceType type;
    private final String id;
    private final String version;
    private final String sha1;

    /**
     * Create a new instance.
     *
     * @param type The type of resource.
     * @param id The id of the resource.
     * @param version The version of the resource.
     * @param sha1 The SHA-1 hash of the zip file in this resource.
     */
    protected Resource(ResourceType type, String id, String version, String sha1) {
        this.type = type;
        this.id = id;
        this.version = version;
        this.sha1 = sha1;
    }

    /**
     * Get the type of resource.
     *
     * @return The type of resource.
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * Get the id of this resource.
     *
     * @return The id.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the version of this resource.
     *
     * @return The version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the SHA-1 hash of the zip file in this resource.
     *
     * @return the SHA-1 hash.
     */
    public String getSha1() {
        return sha1;
    }
}
