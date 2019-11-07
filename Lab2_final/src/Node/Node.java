package Node;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import Utils.ExponentialRandomVariable;

public class Node {
    //Queue used to store packets
    public LinkedList<Packet> queue;
    private double simulationTime;
    private double packetLength;
    public int index;
    //Variables used to store values such as collisions, transmitted packets, successfully transmitted packets etc
    public double numberOfTransmittedPackets = 0;
    public double currentNumberOfCollisions = 0;
    public double numberOfSuccessfulTransmissions = 0;
    public double numberOfDroppedPackets = 0;
    public double currentNumberOfWaits = 0;

    public Node(double simulationTime, double packetLength, int index) {
        this.queue = new LinkedList<>();
        this.simulationTime = simulationTime;
        this.packetLength = packetLength;
        this.index = index;
    }


    //Add packet arrivals to the queue.
    public void addEvents(double arrivalRate) {
        double currentTime = 0;
        double arrivalTime;
        while (currentTime < this.simulationTime) {
            arrivalTime = ExponentialRandomVariable.generateRandomVariable(arrivalRate);
            currentTime += arrivalTime;
            Packet newPacket = new Packet(currentTime, this.packetLength);
            queue.add(newPacket);
        }
    }


    public void incrementTransmittedPackets() {
        this.numberOfTransmittedPackets ++;
    }

    public void incrementSuccessfulTransmittedPackets() {
        this.numberOfSuccessfulTransmissions ++;
    }

    public void incrementNumberOfCollisions() {
        this.currentNumberOfCollisions ++;
    }

    public void resetCollisions() {
        this.currentNumberOfCollisions = 0;
    }

    public void incrementNumberOfWaits() {
        this.currentNumberOfWaits += 1;
    }

    public void resetNumberOfWaits() {
        this.currentNumberOfWaits = 0;
    }

    public void incrementDroppedPackets() {
        this.numberOfDroppedPackets ++;
    }

    //Calculate exponential drop off for collisions for both persistent and non-persistent
    public double calculateExponentialDropoffForCollisions() {
        int max = (int)Math.pow(2, currentNumberOfCollisions) - 1;
        return (Math.random() * max) * 512;
    }

    //Calculate exponential drop off for bus busy for non-persistent case.
    public double calculateExponentialDropoffForBusBusy() {
        int max = (int)Math.pow(2, this.currentNumberOfWaits) - 1;
        return (Math.random() * max) * 512;
    }
}

