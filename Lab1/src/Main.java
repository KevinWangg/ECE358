import Queues.MM1KQueue;
import Utils.ExponentialRandomVariable;
import Queues.MM1Queue;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        // Simulation Variables
        double averagePacketLength = 2000;
        double averageTransmissonRate = 1000000;

        double[] rhoValuesMM1K = {0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5};
        int [] queueSizes = {10, 25, 50};

        double [] rhoValuesMM1 = {0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};

        switch (args[0]) {
            case "1": {
                System.out.println("********************************************************************************* \n");
                System.out.println("Question 1 \n");

                question1();

                System.out.println("\n");
                break;
            }
            case "2": {
                System.out.println("********************************************************************************* \n");
                System.out.println("M/M/1 simulation results  \n");
                double simulationTime  = Double.valueOf(args[1]);
                double rho  = Double.valueOf(args[2]);
                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                MM1(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime);
                break;
            }
            case "3": {
                for (double rho: rhoValuesMM1) {
                    System.out.println("*******************************************************************************");
                    System.out.println(String.format("Simulating M/M/1 for rho value: %s", rho));
                    //Determine arrival rate of packet from rho value
                    double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                    MM1(arrivalRate, 1/averagePacketLength, averageTransmissonRate, 1000);
                }
                System.out.println("\n");
                break;
            }
            case "4": {
                double simulationTime = Double.valueOf(args[1]);
                double rho  = Double.valueOf(args[2]);
                int MM1KQueueSize = Integer.valueOf(args[3]);
                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                MM1K(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime, MM1KQueueSize);
                break;
            }
            case "5": {
                System.out.println("********************************************************************************* \n");
                System.out.println("M/M/1/K simulation results  \n");
                // Gets data for MM1K queues
                for (int queueSize: queueSizes) {
                    for (double rho: rhoValuesMM1K) {
                        System.out.println("*******************************************************************************");
                        System.out.println(String.format("Simulating M/M/1/K for queue size of: %s, rho value: %s", queueSize, rho));
                        //Determine arrival rate of packet from rho value
                        double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                        MM1K(arrivalRate, 1/averagePacketLength, averageTransmissonRate, 1000, queueSize);
                    }
                }
                break;
            }
        }
    }
    // Generates 1000 exponential random variable with lambda=75 and calculates their expected value as well as the varience
    public static void question1() {
        ArrayList<Double> exponentialRandomVariablesList = new ArrayList<>();
        for (int i = 0; i < 1000; i ++) {
            exponentialRandomVariablesList.add(ExponentialRandomVariable.generateRandomVariable(75));
        }

        double expectedValue = ExponentialRandomVariable.calculateExpectedValue(exponentialRandomVariablesList);
        double variance = ExponentialRandomVariable.calculateVariance(exponentialRandomVariablesList, expectedValue);
        System.out.println(String.format("Expected Value: %s", expectedValue));
        System.out.println(String.format("Variance: %s", variance));
    }

    //Creates a MM1 Queue
    public static void MM1(double packetsPerSecond, double packetLength, double transmissonRate, double totalSimulationTime) {
        MM1Queue mm1Queue = new MM1Queue(packetsPerSecond, packetLength, transmissonRate, totalSimulationTime);
        mm1Queue.addEvents();
        mm1Queue.startSimulation();

    }

    //Creates a MM1K Queue
    public static void MM1K(double packetsPerSecond, double packetLength, double transmissonRate, double totalSimulationTime, int queueSize) {
        MM1KQueue mm1kQueue = new MM1KQueue(packetsPerSecond, packetLength, transmissonRate, totalSimulationTime, queueSize);
        mm1kQueue.addEvents();
        mm1kQueue.startSimulation();
    }


}
