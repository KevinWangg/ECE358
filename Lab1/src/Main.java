import Queues.MM1KQueue;
import Utils.ExponentialRandomVariable;
import Queues.MM1Queue;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
        double averagePacketLength = 2000;
        double averageTransmissonRate = 1000000;

        double[] rhoValues = {0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0,
                2.2, 2.4, 2.6, 2.8, 3.0, 3.2, 3.4, 3.6, 3.8, 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.4, 5.8, 6.2, 6.6, 7.0, 7.4, 7.8, 8.2, 8.6, 9.0, 9.4, 9.8, 10};
        int [] queueSizes = {10, 25, 50};

        for (int queueSize: queueSizes) {
            for (double rho: rhoValues) {
                System.out.println("*******************************************************************************");
                System.out.println(String.format("Simulating M/M/1/K for queue size of: %s, rho value: %s", queueSize, rho));
                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                MM1K(arrivalRate, 1/averagePacketLength, averageTransmissonRate, 2000, queueSize);
            }
        }
//        System.out.print("For Question 1 enter 1, for MM1 enter 2, for MM1K enter 3.");
//        String option = scanner.next();
//
//        switch (option) {
//            case "1": {
//                question1();
//                break;
//            }
//            case "2": {
//                System.out.print("Enter the total simulation time:");
//                double simulationTime  = scanner.nextDouble();
//                System.out.print("Enter the value of rou for this simulation:");
//                double rho  = scanner.nextDouble();
//                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
//                MM1(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime);
//                break;
//            }
//            case "3": {
//                System.out.print("Enter the total simulation time:");
//                double simulationTime  = scanner.nextDouble();
//                double[] rhoValues = {0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0,
//                        2.2, 2.4, 2.6, 2.8, 3.0, 3.2, 3.4, 3.6, 3.8, 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.4, 5.8, 6.2, 6,6, 7.0, 7.4, 7.8, 8.2, 8.6, 9.0, 9.4, 9.8, 10.0};
//                int [] queueSizes = {10, 25, 50};
//
//                for (int queueSize: queueSizes) {
//                    for (double rho: rhoValues) {
//                        System.out.println("*******************************************************************************");
//                        System.out.println(String.format("Simulating M/M/1/K for queue size of: %s, rho value: %s", queueSize, rho));
//                        double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
//                        MM1K(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime, queueSize);
//                    }
//                }
//                break;
//            }
//        }
    }

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

    public static void MM1(double packetsPerSecond, double packetLength, double transmissonRate, double totalSimulationTime) {
        MM1Queue mm1Queue = new MM1Queue(packetsPerSecond, packetLength, transmissonRate, totalSimulationTime);
        mm1Queue.addEvents();
        mm1Queue.startSimulation();

    }

    public static void MM1K(double packetsPerSecond, double packetLength, double transmissonRate, double totalSimulationTime, int queueSize) {
        MM1KQueue mm1kQueue = new MM1KQueue(packetsPerSecond, packetLength, transmissonRate, totalSimulationTime, queueSize);
        mm1kQueue.addEvents();
        mm1kQueue.startSimulation();
    }


}
