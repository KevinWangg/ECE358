package Queues;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import Utils.ExponentialRandomVariable;

public class MM1Queue {

    // Simulation parameters
    private final double arrivalRate;
    private final double observerRate;
    private final double packetLength;
    private final double totalSimulationTime;
    private final double transmissionRate;

    //Queue used to store events
    public final LinkedList<QueueEvent> queue = new LinkedList<>();

    // Used for collecting data
    private double queueSize = 0;
    private double numberOfObserverEvents = 0;
    private double totalNumberofPacketsObserved = 0;
    private double idleCounter = 0;
    private double generatedPackets = 0;
    private double numberOfArrivals = 0;
    private double numberOfDepartures = 0;

    public MM1Queue(double arrivalRate, double packetLength, double transmissionRate, double totalSimulationTime){
        this.arrivalRate = arrivalRate;
        this.observerRate = this.arrivalRate * 5;
        this.totalSimulationTime = totalSimulationTime;
        this.packetLength = packetLength;
        this.transmissionRate = transmissionRate;
    }

    public void addEvents() {
        createArrivalEvents();
        createObserverEvents();
        Collections.sort(this.queue, new Comparator<QueueEvent>() {
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

    private void handleObserverEvent() {
        this.numberOfObserverEvents += 1;
        this.totalNumberofPacketsObserved += this.queueSize;
        if (this.queueSize == 0) {
            this.idleCounter += 1;
        }
    }

    private void handleArrivalEvent() {
        this.queueSize += 1;
        this.generatedPackets += 1;
        this.numberOfArrivals += 1;
    }

    private void handleDepartureEvent() {
        this.queueSize -= 1;
        this.numberOfDepartures += 1;
    }

    public void startSimulation() {
        QueueEvent currentEvent;
        while (this.queue.size() > 0) {
            currentEvent = this.queue.removeFirst();
            if (currentEvent.eventType == QueueEvent.Type.Observer) {
                handleObserverEvent();
            } else if (currentEvent.eventType == QueueEvent.Type.Arrival) {
                handleArrivalEvent();
            } else {
                handleDepartureEvent();
            }
        }
        double expectedValue = this.totalNumberofPacketsObserved/this.numberOfObserverEvents;
        double idleTime = this.idleCounter/this.numberOfObserverEvents;
        System.out.println(String.format(
                "Number of Arrivals: %s, Number of Departures: %s, Number of Observartions: %s, Idle Counter: %s, Number of Generated Packets: %s, Total packets in Queue: %s, Expected Value: %s, P Idle: %s",
                this.numberOfArrivals, this.numberOfDepartures, this.numberOfObserverEvents, this.idleCounter, this.generatedPackets, this.totalNumberofPacketsObserved, expectedValue, idleTime)
        );
    }


}
