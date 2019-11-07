package CSMACD;

import Node.Node;
import Node.Packet;

import java.util.ArrayList;

public class Persistent {
    private int numOfNodes;
    private double arrivalRate;
    private double transmissonRate;
    private double packetLength;
    private double distanceBetweenNodes;
    private double propagationSpeed;
    private double totalSimulationTime;
    private double currentSimulationTime;
    private double transmittingTime;
    private double propgationTime;
    private double maxPropagationTime = 0;

    private ArrayList<Node> nodes = new ArrayList<Node>();

    //Initialize all the variables used.
    public Persistent(int numOfNodes, double arrivalRate, double transmissonRate, double packetLength, double distanceBetweenNodes, double propagationSpeed, double totalSimulationTime) {
        this.numOfNodes = numOfNodes;
        this.arrivalRate = arrivalRate;
        this.transmissonRate = transmissonRate;
        this.packetLength = packetLength;
        this.distanceBetweenNodes = distanceBetweenNodes;
        this.propagationSpeed = propagationSpeed;
        this.totalSimulationTime = totalSimulationTime;
        this.currentSimulationTime = 0;
        // Calculate time to transmit one packet, this is constant through all nodes;
        this.transmittingTime = this.packetLength/this.transmissonRate;
        // Calculate time to propagate from two adjacent nodes, this time is then multiplied by the distance of two nodes.
        this.propgationTime = this.distanceBetweenNodes/this.propagationSpeed;
    }

    // Creates nodes and create packets for each node and add it to the array.
    public void initalizeNodes() {
        for (int i = 0; i < numOfNodes; i ++) {
            // Creates a node with an packet length and index, index is used to calculate propagation time
            Node newNode = new Node(this.totalSimulationTime, this.packetLength, i);
            newNode.addEvents(this.arrivalRate);
            this.nodes.add(newNode);
        }
    }

    //Helper function to find the node with the current minimum arrival time for a packet.
    private Node findMin() {
        Node currentMin = this.nodes.get(0);
        Node comparisonNode;
        for (int i = 0; i < this.nodes.size(); i ++) {
            comparisonNode = this.nodes.get(i);
            if (comparisonNode.queue.size() > 0 && comparisonNode.queue.getFirst().arrivalTime < currentMin.queue.getFirst().arrivalTime) {
                currentMin = comparisonNode;
            }
        }
        return currentMin;
    }

    //Calculates the propagation time between any two nodes.
    private double calculatePropagationTime(Node n1, Node n2) {
        int distanceBetweenNodes = Math.abs(n1.index - n2.index);
        return distanceBetweenNodes * this.propgationTime;
    }

    public void startSimulation() {
        //Find current node with minimum packet arrival time, this is current transmitting node.
        Node currentTransmittingNode = findMin();

        //Get first packet in minimum node.
        Packet currentPacket = currentTransmittingNode.queue.getFirst();

        //Set current simulation time to that packet's arrival time
        this.currentSimulationTime = currentPacket.arrivalTime;

        //Variable to detect if collision has occurred;
        boolean collisions;

        double exponentialBackoffTime;

        //Run simulation until total simulation time.
        while (this.currentSimulationTime < this.totalSimulationTime) {
            //Increment the current transmitting node's transmitted packets
            currentTransmittingNode.incrementTransmittedPackets();

            //Check if collisions will occur with current transmission
             collisions = checkCollisions(currentTransmittingNode);

            //If collision occurs, update the current transmitting node's packets
            if (collisions) {
                //Update the number of collisions for the current node;
                currentTransmittingNode.incrementNumberOfCollisions();
                //If current transmitting node's number of collisions is greater than 10, then you have to reset collisions, drop the current packet and remove it from the node's queue.
                if (currentTransmittingNode.currentNumberOfCollisions > 10) {
                    currentTransmittingNode.resetCollisions();
                    currentTransmittingNode.incrementDroppedPackets();
                    currentTransmittingNode.queue.removeFirst();
                //Else you update the packets in the current queue which arrive during collision to new time.
                } else {
                    exponentialBackoffTime = currentTransmittingNode.calculateExponentialDropoffForCollisions() / this.transmissonRate;
                    updatePacketTimesForCollision(currentTransmittingNode, exponentialBackoffTime, this.maxPropagationTime);
                }
            } else {
                //Node has successfully transmitted the packet, reset collisions, increment the number of successful transmissions and remove the current packet
                //then update the times for all the other nodes
                currentTransmittingNode.resetCollisions();
                currentTransmittingNode.incrementSuccessfulTransmittedPackets();
                currentTransmittingNode.queue.remove(currentPacket);
                updateWaitTimes(currentTransmittingNode);
            }
            //Find next node that will be transmitting and see the time to that node's first packet's arrival time.
            currentTransmittingNode = findMin();
            currentPacket = currentTransmittingNode.queue.getFirst();
            this.currentSimulationTime = currentPacket.arrivalTime;
        }
    }

    //Same as non-persistent
    public boolean checkCollisions(Node currentTransmittingNode) {
        boolean collisionDetected = false;
        ArrayList<Node> temp = new ArrayList<Node>();
        this.nodes.remove(currentTransmittingNode);
        temp.add(currentTransmittingNode);

        Node currentMinNode = findMin();

        if (currentMinNode.queue.size() == 0) {
            this.nodes.add(currentTransmittingNode);
            return collisionDetected;
        }

        double propagationTime = calculatePropagationTime(currentTransmittingNode, currentMinNode);
        this.maxPropagationTime = 0;
        this.maxPropagationTime = Math.max(propagationTime, this.maxPropagationTime);
        while (currentMinNode.queue.getFirst().arrivalTime <= this.currentSimulationTime + propagationTime) {
            this.nodes.remove(currentMinNode);
            temp.add(currentMinNode);

            currentMinNode.incrementNumberOfCollisions();
            currentMinNode.incrementTransmittedPackets();

            if (currentMinNode.currentNumberOfCollisions > 10) {
                currentMinNode.resetCollisions();
                currentMinNode.incrementDroppedPackets();
                currentMinNode.queue.removeFirst();
            } else {
                double backOffTime = currentMinNode.calculateExponentialDropoffForCollisions() / this.transmissonRate;
                updatePacketTimesForCollision(currentMinNode, backOffTime, propagationTime);
            }
            collisionDetected = true;
            currentMinNode = findMin();
            propagationTime = calculatePropagationTime(currentTransmittingNode, currentMinNode);
            this.maxPropagationTime = Math.max(propagationTime, this.maxPropagationTime);
        }
        for (int i = 0; i < temp.size(); i ++) {
            this.nodes.add(temp.get(i));
        }
        return collisionDetected;
    }

    //Same as non-persistent
    private void updatePacketTimesForCollision(Node currNode, double backOffTime, double propagationTime) {
        double minTime = this.currentSimulationTime + backOffTime + propagationTime + this.transmittingTime;
        currNode.queue.getFirst().arrivalTime = minTime;

        Packet p;
        for (int i = 1; i < currNode.queue.size(); i ++) {
            p = currNode.queue.get(i);
            if (p.arrivalTime < minTime) {
                p.arrivalTime = minTime;
            } else {
                break;
            }
        }
    }

    //Same as non-persistent
    private void updateWaitTimes(Node currentNode ) {
        for (Node n: this.nodes) {
            double propagationTime = n==currentNode ? 0 : calculatePropagationTime(currentNode, n);
            double minimumTime = this.currentSimulationTime + propagationTime;
            double waitingTime = this.currentSimulationTime + propagationTime + this.transmittingTime;
            updateWaitingTimesSuccess(waitingTime, minimumTime, n);
        }
    }


    //Update packet's time of arrival for current node when successful transmission
    private void updateWaitingTimesSuccess(double waitingTime, double minimumTime, Node n) {
        //Check if queue is empty
        if (n.queue.size() == 0) {
            return;
        }

        //Get first packet in current node's queue
        Packet firstPacket = n.queue.get(0);

        //If first packet's arrival time is less than required waiting time update it to waiting time else return
        if (firstPacket.arrivalTime >= minimumTime && (firstPacket.arrivalTime < waitingTime)) {
            firstPacket.arrivalTime = waitingTime;
        } else {
            return;
        }

        //loop through rest of queue and update packet's arrival time it's less than first packet's arrival time.
        for (int i = 1; i < n.queue.size(); i ++) {
            Packet currentPacket = n.queue.get(i);
            if (currentPacket.arrivalTime <= firstPacket.arrivalTime) {
                currentPacket.arrivalTime = waitingTime;
            } else {
                return;
            }
        }
    }

    //Same as non-persistent
    public void calculateEfficiency() {
        double totalTransmissions = 0;
        double totalSuccessfulTransmissions = 0;
        for (Node n: this.nodes) {
            totalTransmissions += n.numberOfTransmittedPackets;
            totalSuccessfulTransmissions += n.numberOfSuccessfulTransmissions;
        }
        double efficiency = totalSuccessfulTransmissions/totalTransmissions;
        double throughPut = (totalSuccessfulTransmissions * this.packetLength) / this.currentSimulationTime;
        System.out.printf("%d, %f, %f, %f %n", this.numOfNodes, efficiency, throughPut, throughPut/this.transmissonRate);
    }
}
