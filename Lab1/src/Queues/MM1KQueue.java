package Queues;

import Utils.ExponentialRandomVariable;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class MM1KQueue {
    private final double arrivalRate;
    private final double observerRate;
    private final double packetLength;
    private final double totalSimulationTime;
    private final double transmissionRate;


    public final LinkedList<QueueEvent> eventsQueue = new LinkedList<>();
    private final LinkedList<QueueEvent> packetQueue = new LinkedList<>();

    private final int totalQueueSize;
    private int currentQueueSize = 0;
    private int packetsDropped = 0;
    private int numberOfObserverEvents = 0;
    private int totalNumberofPacketsObserved = 0;
    private int queueSize = 0;
    private int simulationQueueSize = 0;
    private int idleCounter = 0;
    private int generatedPackets = 0;
    private int numberOfArrivals = 0;
    private int numberOfDepartures = 0;

    public MM1KQueue(double arrivalRate, double packetLength, double transmissionRate, double totalSimulationTime, int queueSize){
        this.arrivalRate = arrivalRate;
        this.observerRate = this.arrivalRate * 5;
        this.totalSimulationTime = totalSimulationTime;
        this.packetLength = packetLength;
        this.transmissionRate = transmissionRate;
        this.totalQueueSize = queueSize;
    }

    public void addEvents() {
        createArrivalEvents();
        createObserverEvents();
        Collections.sort(this.eventsQueue, new Comparator<QueueEvent>() {
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
            removePacketFromQueue(time);
            this.generatedPackets += 1;
            if (this.packetQueue.size() == this.totalQueueSize) {
                this.packetsDropped += 1;
            } else {
                currentDeparture = createDepartureEvents(previousDeparture, time);
                QueueEvent arrivalEvent = new QueueEvent(QueueEvent.Type.Arrival, time);
                this.eventsQueue.add(arrivalEvent);
                this.eventsQueue.add(currentDeparture);
                this.packetQueue.add(currentDeparture);
                this.currentQueueSize += 1;
                previousDeparture = currentDeparture;
            }
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

    private void removePacketFromQueue(double currentTime) {
        if (this.packetQueue.size() == 0 || this.packetQueue.getFirst().eventTime > currentTime) {
            return;
        }

        while(this.packetQueue.size() > 0  && this.packetQueue.getFirst().eventTime <= currentTime) {
            this.packetQueue.removeFirst();
        }
    }

    private void createObserverEvents() {
        double time = 0;
        double arrival;
        while (time < this.totalSimulationTime) {
            arrival = ExponentialRandomVariable.generateRandomVariable(this.observerRate);
            time += arrival;
            QueueEvent observerEvent = new QueueEvent(QueueEvent.Type.Observer, time);
            this.eventsQueue.add(observerEvent);
        }
    }

    private void handleObserverEvent() {
        this.numberOfObserverEvents += 1;
        this.totalNumberofPacketsObserved += this.simulationQueueSize;
        if (this.simulationQueueSize == 0) {
            this.idleCounter += 1;
        }
    }

    private void handleArrivalEvent() {
        this.simulationQueueSize += 1;
        this.numberOfArrivals += 1;
    }

    private void handleDepartureEvent() {
        this.simulationQueueSize -= 1;
        this.numberOfDepartures += 1;
    }

    public void startSimulation() {
        QueueEvent currentEvent;
        while (this.eventsQueue.size() > 0) {
            currentEvent = this.eventsQueue.removeFirst();
            if (currentEvent.queueType == QueueEvent.Type.Observer) {
                handleObserverEvent();
            } else if (currentEvent.queueType == QueueEvent.Type.Arrival) {
                handleArrivalEvent();
            } else {
                handleDepartureEvent();
            }
        }
        double expectedValue = Double.valueOf(this.totalNumberofPacketsObserved)/Double.valueOf(this.numberOfObserverEvents);
        double packetLoss = Double.valueOf(this.packetsDropped)/Double.valueOf(this.generatedPackets);
        System.out.println(String.format(
                "Number of Arrivals: %s, Number of Departures: %s, Number of Observartions: %s, Idle Counter: %s, Number of Generated Packets: %s, Total packets in Queue: %s, Total packets dropped: %s, Expected Value: %s, Percentage of packetloss: %s",
                this.numberOfArrivals, this.numberOfDepartures, this.numberOfObserverEvents, this.idleCounter, this.generatedPackets, this.totalNumberofPacketsObserved, this.packetsDropped, expectedValue, packetLoss)
        );
    }

}
