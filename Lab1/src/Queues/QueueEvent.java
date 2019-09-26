package Queues;

public class QueueEvent {
    enum Type {
        Arrival,
        Departure,
        Observer
    }
    public Type eventType;
    public double eventTime;
    public QueueEvent(Type type, Double time) {
        this.eventType = type;
        this.eventTime = time;
    }
}
