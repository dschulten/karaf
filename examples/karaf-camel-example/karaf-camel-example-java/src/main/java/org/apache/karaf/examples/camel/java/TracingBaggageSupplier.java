package org.apache.karaf.examples.camel.java;

import io.opentracing.Span;
import org.apache.camel.Exchange;
import org.apache.camel.opentracing.ActiveSpanManager;
import org.apache.camel.opentracing.OpenTracingTracer;

public class TracingBaggageSupplier {

    public Object supplyBaggage(Exchange exchange) {
        Span span = ActiveSpanManager.getSpan(exchange);
        span.setBaggageItem("process-instance-id", "123456789");
        return exchange.getMessage().getBody();
    }
}
