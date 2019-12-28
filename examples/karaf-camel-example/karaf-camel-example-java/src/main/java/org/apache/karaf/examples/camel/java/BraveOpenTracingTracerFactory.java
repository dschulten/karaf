package org.apache.karaf.examples.camel.java;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import io.opentracing.Tracer;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

import java.util.Collections;
import java.util.List;

import static brave.opentracing.BraveTracer.newBuilder;
import static brave.propagation.ExtraFieldPropagation.newFactoryBuilder;

/**
 * Factory for OpenTracing tracer wrapped into an {@link APIExtensionsTracer}.
 */
public class BraveOpenTracingTracerFactory {

    private Tracer tracer;
    private List<String> baggageKeys = Collections.emptyList();
    private String localServiceName = "unknown-service";
    private List<TracerObserver> tracerObservers = Collections.emptyList();

    /**
     * Creates OpenTracing tracer instance wrapped into an {@link APIExtensionsTracer}.
     * @param spanReporter to report to Zipkin
     * @return tracer
     */
    public Tracer openTracingTracer(Reporter<Span> spanReporter) {
        if (tracer != null) {
            return tracer;
        }

        ExtraFieldPropagation.Factory propagationFactory = newFactoryBuilder(B3Propagation.FACTORY)
            .addPrefixedFields("baggage-", baggageKeys)
            .build();

        Tracing braveTracing = Tracing.newBuilder()
            .localServiceName(localServiceName) // shown in Zipkin
            .propagationFactory(propagationFactory)
            .spanReporter(spanReporter)
            .build();

        APIExtensionsTracer tracer = new APIExtensionsTracer(newBuilder(braveTracing).build());
        for (TracerObserver tracerObserver : tracerObservers) {
            if(tracerObserver instanceof BaggageTracerObserver) {
                ((BaggageTracerObserver) tracerObserver).setBaggageKeys(baggageKeys);
            }
            tracer.addTracerObserver(tracerObserver);
        }
        return this.tracer;
    }

    public void setBaggageKeys(List<String> baggageKeys) {
        this.baggageKeys = baggageKeys;
    }

    public void setTracerObservers(List<TracerObserver> tracerObservers) {
        this.tracerObservers = tracerObservers;
    }


    public void setLocalServiceName(String localServiceName) {
        this.localServiceName = localServiceName;
    }

}
