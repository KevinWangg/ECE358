package Queues;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import Utils.ExponentialRandomVariable;

public class MM1Queue {
    private final double arrivalRate;
    private final double observerRate;
    private final double packetLength;
    private final double totalSimulationTime;
    private final double transmissionRate;
    public final LinkedList<QueueEvent> queue = new LinkedList<>();

    public MM1Queue(double arrivalRate, double packLength, double transmissionRate, double totalSimulationTime){
        this.arrivalRate = arrivalRate;
        this.observerRate = this.arrivalRate * 5;
        this.totalSimulationTime = totalSimulationTime;
        this.packetLength = packLength;
        this.transmissionRate = transmissionRate;
    }

    public void addEvents() {
        createArrivalEvents();
        createObserverEvents();
        Collections.sort(queue, new Comparator<QueueEvent>() {
            @Override
            public int compare(QueueEvent o1, QueueEvent o2) {
                if (o1.eventTime - o2.eventTime > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
    private void createArrivalEvents() {
        double time = 0;
        double arrival;
        QueueEvent previousDeparture = null;
        QueueEvent currentDeparture;
        while (time < this.totalSimulationTime) {
            arrival = ExponentialRandomVariable.generateRandomVariable(this.arrivalRate);
            time += arrival;
            currentDeparture = createDepartureEvents(previousDeparture, time);
            QueueEvent arrivalEvent = new QueueEvent(QueueEvent.Type.Arrival, time);
            queue.add(arrivalEvent);
            queue.add(currentDeparture);
            previousDeparture = currentDeparture;
        }
    }

    private QueueEvent createDepartureEvents(QueueEvent previousEvent, double arrivalTime) {
        double lengthOfPacket = ExponentialRandomVariable.generateRandomVariable(this.packetLength);
        double serviceTime = lengthOfPacket/this.transmissionRate;
        double departureTime;
        if (previousEvent != null && previousEvent.eventTime > arrivalTime) {
            departureTime = previousEvent.eventTime + serviceTime;
        } else {
            departureTime = arrivalTime + serviceTime;
        }
        QueueEvent departureEvent = new QueueEvent(QueueEvent.Type.Departure, departureTime);
        return departureEvent;
    }

    private void createObserverEvents() {
        double time = 0;
        double arrival;
        while (time < this.totalSimulationTime) {
            arrival = ExponentialRandomVariable.generateRandomVariable(this.observerRate);
            time += arrival;
            QueueEvent observerEvent = new QueueEvent(QueueEvent.Type.Observer, time);
            queue.add(observerEvent);
        }
    }

    public void startSimulation() {
        double time = 0;
        
    }


}
