package org.apache.karaf.examples.camel.java;

import io.opentracing.Span;
import io.opentracing.contrib.api.TracerObserver;

import java.util.List;

/**
 * Observer used to receive notifications related to {@link Span}s, including baggage items.
 *
 */
public interface BaggageTracerObserver extends TracerObserver {
    /**
     * Sets baggage keys to observe.
     *
     * @param baggageKeys to observe
     */
    void setBaggageKeys(List<String> baggageKeys);
}
