package org.apache.karaf.examples.camel.java;

import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Observes tracing and updates {@link MDC} accordingly with trace id, span id and the specified baggage items.
 *
 * @see <a href="https://github.com/opentracing-contrib/java-api-extensions">OpenTracing API Extensions</a>
 */
public class MdcTracerObserver implements BaggageTracerObserver {

    private List<String> baggageKeys = Collections.emptyList();

    public SpanObserver onStart(SpanData spanData) {

        String traceId = spanData.getTraceId();
        if (traceId != null) {
            MDC.put("trace_id", traceId);
        }
        String spanId = spanData.getSpanId();
        if (spanId != null) {
            MDC.put("span_id", spanId);
        }

        for (String baggageKey : baggageKeys) {
            Object baggageItem = spanData.getBaggageItem(baggageKey);
            if (baggageItem != null) {
                MDC.put(toSnakeCase(baggageKey), baggageItem.toString());
            }
        }

        return new SpanObserver() {
            @Override
            public void onSetOperationName(SpanData spanData, String operationName) {
                // noop
            }

            @Override
            public void onSetTag(SpanData spanData, String key, Object value) {
                // noop
            }

            @Override
            public void onSetBaggageItem(SpanData spanData, String key, String value) {
                MDC.put(toSnakeCase(key), value);
            }

            @Override
            public void onLog(SpanData spanData, long timestampMicroseconds, Map<String, ?> fields) {
                // noop
            }

            @Override
            public void onLog(SpanData spanData, long timestampMicroseconds, String event) {
                // noop
            }

            @Override
            public void onFinish(SpanData spanData, long finishMicros) {
                String finishedTraceId = spanData.getTraceId();
                if (finishedTraceId != null && finishedTraceId.equals(spanData.getSpanId())) {
                    for (String baggageKey : baggageKeys) {
                        MDC.remove(toSnakeCase(baggageKey));
                    }
                }
            }
        };
    }

    private static String toSnakeCase(final String kebabCaseString) {
        return kebabCaseString.replace('-', '_');
    }

    /**
     * Sets baggage keys to put to {@link MDC}. Baggage keys get lowercased on the wire.
     * If you use kebab-case keys as in <code>"process-instance-id"</code>,
     * they will be converted to snake_case when putting the corresponding baggage item
     * into the MDC.
     *
     * @param baggageKeys to put into the MDC
     */
    public void setBaggageKeys(List<String> baggageKeys) {
        this.baggageKeys = baggageKeys;
    }
}
