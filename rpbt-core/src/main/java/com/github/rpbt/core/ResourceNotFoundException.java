package com.github.rpbt.core;

/**
 * Thrown when a {@link Resource} was not found in an {@link RpbtRepository}.
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
