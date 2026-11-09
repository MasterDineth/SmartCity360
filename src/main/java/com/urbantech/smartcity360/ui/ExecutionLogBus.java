package com.urbantech.smartcity360.ui;

import javax.swing.SwingUtilities;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe centralized event bus for broadcasting execution traces from
 * domain GoF pattern interactions to GUI log consoles and examiners.
 */
public final class ExecutionLogBus {

    private static final ExecutionLogBus INSTANCE = new ExecutionLogBus();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final List<Consumer<String>> subscribers = new CopyOnWriteArrayList<>();

    private ExecutionLogBus() {}

    public static ExecutionLogBus getInstance() {
        return INSTANCE;
    }

    /**
     * Subscribes a listener (e.g. text area append handler) to receive log messages.
     */
    public void subscribe(Consumer<String> subscriber) {
        if (subscriber != null && !subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    /**
     * Unsubscribes a listener.
     */
    public void unsubscribe(Consumer<String> subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * Broadcasts a formatted execution trace message to all registered GUI consoles.
     * Guarantees invocation on the Swing Event Dispatch Thread (EDT).
     *
     * @param category Module or pattern category (e.g. "Part A: Decorator")
     * @param message Detailed trace string
     */
    public void log(String category, String message) {
        String timestamp = LocalTime.now().format(TIME_FORMATTER);
        String formatted = String.format("[%s] [%s] %s", timestamp, category, message);

        if (SwingUtilities.isEventDispatchThread()) {
            notifySubscribers(formatted);
        } else {
            SwingUtilities.invokeLater(() -> notifySubscribers(formatted));
        }
    }

    /**
     * Logs multiple trace steps in order.
     */
    public void logAll(String category, List<String> messages) {
        if (messages == null) return;
        for (String msg : messages) {
            log(category, msg);
        }
    }

    private void notifySubscribers(String formattedMessage) {
        for (Consumer<String> subscriber : subscribers) {
            try {
                subscriber.accept(formattedMessage);
            } catch (Exception ignored) {
                // Prevent rogue UI exception from breaking bus
            }
        }
    }
}
