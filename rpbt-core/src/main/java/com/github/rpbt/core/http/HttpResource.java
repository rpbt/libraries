package com.github.rpbt.core.http;

import com.github.rpbt.core.Resource;
import com.github.rpbt.core.ResourceType;

/**
 * A {@link Resource} from an {@link HttpRepository}.
 */
public class HttpResource extends Resource {
    private final String baseUrl;

    HttpResource(ResourceType type, String id, String version, String sha1, String baseUrl) {
        super(type, id, version, sha1);
        this.baseUrl = baseUrl;
    }

    /**
     * Get the base url for this resource. Includes a trailing slash.
     * <p>
     * Will follow the format:
     * {@code <repo root>/<type>/<id>/<version>/}
     *
     * @return The base url.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}
