package Queues;

public class QueueEvent {
    enum Type {
        Arrival,
        Departure,
        Observer
    }
    public Type queueType;
    public double eventTime;
    public QueueEvent(Type type, Double time) {
        this.queueType = type;
        this.eventTime = time;
    }
}
