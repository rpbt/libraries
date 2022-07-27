package com.github.rpbt.core;

import java.util.Locale;

/**
 * A type of resource from an {@link RpbtRepository}.
 */
public enum ResourceType {
    /**
     * An rpbt plugin.
     */
    PLUGIN,
    /**
     * A dependency for a pack. This is a compiled resource pack.
     */
    DEPENDENCY;

    /**
     * Get the name of the folder in the repository where the resources of this type
     * are stored.
     *
     * @return The folder name.
     */
    public String getFolder() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
