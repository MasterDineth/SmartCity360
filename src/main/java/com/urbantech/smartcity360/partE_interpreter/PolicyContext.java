package com.urbantech.smartcity360.partE_interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context Object in the GoF Interpreter Pattern.
 * Stores live telemetry and environmental sensor attributes for smart city zones
 * (e.g. ZONE, DECIBELS, SPEED_KMH, TIME_HOUR, VEHICLE, PARKING_MINS, PERMIT),
 * along with a chronological step-by-step evaluation trace for AST execution.
 */
public class PolicyContext {

    private final Map<String, Object> attributes;
    private final List<String> evaluationTrace;

    public PolicyContext() {
        this.attributes = new ConcurrentHashMap<>();
        this.evaluationTrace = new ArrayList<>();
    }

    public PolicyContext(Map<String, Object> initialAttributes) {
        this();
        if (initialAttributes != null) {
            this.attributes.putAll(initialAttributes);
        }
    }

    public void setAttribute(String key, Object value) {
        if (key != null) {
            if (value != null) {
                attributes.put(key, value);
            } else {
                attributes.remove(key);
            }
        }
    }

    public Object getAttribute(String key) {
        return key != null ? attributes.get(key) : null;
    }

    public boolean hasAttribute(String key) {
        return key != null && attributes.containsKey(key);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addTrace(String message) {
        if (message != null) {
            evaluationTrace.add(message);
        }
    }

    public List<String> getEvaluationTrace() {
        return evaluationTrace;
    }

    public void clearTrace() {
        evaluationTrace.clear();
    }
}
