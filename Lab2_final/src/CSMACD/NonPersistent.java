package CSMACD;

import Node.Node;
import Node.Packet;

import java.util.ArrayList;

public class NonPersistent {
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
    public NonPersistent(int numOfNodes, double arrivalRate, double transmissonRate, double packetLength, double distanceBetweenNodes, double propagationSpeed, double totalSimulationTime) {
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
            // Creates a node with an packet length and index, index is used to calculate propagation time..
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
                    //Calculate backoff time for current node
                    exponentialBackoffTime = currentTransmittingNode.calculateExponentialDropoffForCollisions() / this.transmissonRate;
                    updatePacketTimesForCollision(currentTransmittingNode, exponentialBackoffTime, this.maxPropagationTime);
                }
            } else {
                //Node has successfully transmitted the packet, you need to reset number of waits for the node, reset collisions, increment the number of successful transmissions and remove the current packet
                //then update the times for all the other nodes
                currentTransmittingNode.resetNumberOfWaits();
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

    // Used to determine if collision will occur during transmission.
    public boolean checkCollisions(Node currentTransmittingNode) {
        boolean collisionDetected = false;
        //Create temp list to hold nodes that have been updated already
        ArrayList<Node> temp = new ArrayList<Node>();
        //Remove current transmitting node from list of nodes and add to temp list
        this.nodes.remove(currentTransmittingNode);
        temp.add(currentTransmittingNode);
        //Find the next minimum node that's not transmitting;
        Node currentMinNode = findMin();
        //If the queue for minimum node is empty, then just return;
        if (currentMinNode.queue.size() == 0) {
            this.nodes.add(currentTransmittingNode);
            return collisionDetected;
        }
        //Calculate the propagation time between the current transmitting node and the minimum node
        double propagationTime = calculatePropagationTime(currentTransmittingNode, currentMinNode);

        //Used to keep track of max propagation time which is used to update the packet times of current transmitting node if collision occurs.
        this.maxPropagationTime = 0;
        this.maxPropagationTime = Math.max(propagationTime, this.maxPropagationTime);

        //Loop until the current minimum node's packet arrival time is greater than the time it takes for first bit of transmitting packet to arrive to current minimum node
        while (currentMinNode.queue.getFirst().arrivalTime <= this.currentSimulationTime + propagationTime) {
            //Remove current minimum node from node's list and add to temp list
            this.nodes.remove(currentMinNode);
            temp.add(currentMinNode);

            //Increment number of collisions for that node, and number of transmitted packets
            currentMinNode.incrementNumberOfCollisions();
            currentMinNode.incrementTransmittedPackets();

            //If current minimum node's number of collisions is greater than 10, then you have to reset collisions, drop the current packet and remove it from the node's queue.
            if (currentMinNode.currentNumberOfCollisions > 10) {
                currentMinNode.resetCollisions();
                currentMinNode.incrementDroppedPackets();
                currentMinNode.queue.removeFirst();
            //Else update the packets in the current node's queue.
            } else {
                //Calculate backoff time for the current minimum node
                double backOffTime = currentMinNode.calculateExponentialDropoffForCollisions() / this.transmissonRate;
                updatePacketTimesForCollision(currentMinNode, backOffTime, propagationTime);
            }
            //Set boolean to true and find next node with minimum time for packet arrival and calculate propagation time between that node and current transmitting time
            collisionDetected = true;
            currentMinNode = findMin();
            propagationTime = calculatePropagationTime(currentTransmittingNode, currentMinNode);
            this.maxPropagationTime = Math.max(propagationTime, this.maxPropagationTime);
        }

        //Add all the nodes that have been removed back to the nodes list
        for (int i = 0; i < temp.size(); i ++) {
            this.nodes.add(temp.get(i));
        }
        return collisionDetected;
    }

    //Update the values of packets in current node's queue
    private void updatePacketTimesForCollision(Node currNode, double backOffTime, double propagationTime) {
        // Find the time required that packets need to be updated to.
        double minTime = this.currentSimulationTime + backOffTime + propagationTime + this.transmittingTime;
        currNode.queue.getFirst().arrivalTime = minTime;

        //Loop through all the packets after the first packet in the queue until you see a packet who's arrival time is greater than the minimum time, and update all the previous packet's time to the minimum time.
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


    //Update the wait time for all the nodes after an successful transmission
    private void updateWaitTimes(Node currentNode ) {
        for (Node n: this.nodes) {
            //Calculate the propagation time between transmitting node and current node
            double propagationTime = n==currentNode ? 0 : calculatePropagationTime(currentNode, n);
            double minimumTime = this.currentSimulationTime + propagationTime;
            double waitingTime = this.currentSimulationTime + propagationTime + this.transmittingTime;
            //Update the packets in current node
            updateWaitingTimesSuccess(waitingTime, minimumTime, n);
        }
    }

    //Updates packet time for current node
    private void updateWaitingTimesSuccess(double waitingTime, double minimumTime, Node n) {
        //Check if queue is empty
        if (n.queue.size() == 0) {
            return;
        }
        //Get the first packet in the queue
        Packet firstPacket = n.queue.getFirst();
        Boolean found = false;
        //Find first packet that isn't dropped after incrementing number of waits and that packet's arrival time is less than waiting time.
        while ((firstPacket.arrivalTime >= minimumTime) && (firstPacket.arrivalTime <= waitingTime)) {
            n.incrementNumberOfWaits();
            //If node has waited more than 10 times in a row, reset number of waits, increment number of dropped packets, remove the current packet and get the next packet, and increment the number of waits to 1.
            if (n.currentNumberOfWaits > 10) {
                n.resetNumberOfWaits();
                n.incrementDroppedPackets();
                n.queue.removeFirst();
                firstPacket = n.queue.getFirst();
                n.incrementNumberOfWaits();
            }
            //Calculate backoff time
            double exponentialBackoffTime = n.calculateExponentialDropoffForBusBusy()/this.transmissonRate;
            //Set current pack arrival time
            firstPacket.arrivalTime = exponentialBackoffTime + waitingTime;
            found = true;
        }
        //Return if no packets need to be updated
        if (!found) {
            return;
        }
        //Update all packets in current node whose time is less than the first packet's arrival time to the first packet's arrival time
        for (int i = 1; i < n.queue.size(); i ++) {
            Packet curPacket = n.queue.get(i);
            if (curPacket.arrivalTime <= firstPacket.arrivalTime) {
                curPacket.arrivalTime = firstPacket.arrivalTime;
            } else {
                return;
            }
        }
    }

    //Calculates efficiency and throughput
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
