package com.urbantech.smartcity360.partA_decorator;

import java.util.List;
import java.util.Objects;

/**
 * Base Decorator in the GoF Decorator Pattern.
 * Maintains a reference to a wrapped DataStream instance and delegates
 * default requests to it. Concrete decorators extend this class to
 * attach dynamic security behaviors before or after delegation.
 */
public abstract class StreamDecorator implements DataStream {

    protected final DataStream wrappedStream;

    public StreamDecorator(DataStream wrappedStream) {
        this.wrappedStream = Objects.requireNonNull(wrappedStream, "wrappedStream must not be null");
    }

    @Override
    public String transmit() {
        return wrappedStream.transmit();
    }

    @Override
    public List<String> getExecutionTrace() {
        return wrappedStream.getExecutionTrace();
    }

    @Override
    public String getLayerDescription() {
        return wrappedStream.getLayerDescription();
    }

    /**
     * Access the underlying wrapped component.
     *
     * @return The enclosed DataStream instance
     */
    public DataStream getWrappedStream() {
        return wrappedStream;
    }
}
