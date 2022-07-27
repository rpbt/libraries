package com.github.rpbt.core;

/**
 * Utilities used by {@code rpbt-core}.
 */
public class RpbtUtils {
    /**
     * Get the "short id" from an id.
     * <p>
     * The "short id" is the last part of the id, in case it contains slashes.
     *
     * @param id The id.
     * @return The "short id".
     */
    public static String getShortId(String id) {
        int index = id.lastIndexOf("/") + 1;
        return id.substring(index);
    }
}
