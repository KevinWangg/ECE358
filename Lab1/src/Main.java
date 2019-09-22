import Queues.MM1KQueue;
import Queues.QueueEvent;
import Utils.ExponentialRandomVariable;
import Queues.MM1Queue;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double averagePacketLength = 2000;
        double averageTransmissonRate = 1000000;
        System.out.print("For Question 1 enter 1, for MM1 enter 2, for MM1K enter 3.");
        String option = scanner.next();

        switch (option) {
            case "1": {
                question1();
                break;
            }
            case "2": {
                System.out.print("Enter the total simulation time:");
                double simulationTime  = scanner.nextDouble();
                System.out.print("Enter the value of rou for this simulation:");
                double rho  = scanner.nextDouble();
                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                MM1(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime);
                break;
            }
            case "3": {
                System.out.print("Enter the total simulation time:");
                double simulationTime  = scanner.nextDouble();
                System.out.print("Enter the value of rou for this simulation:");
                double rho  = scanner.nextDouble();
                System.out.print("Enter the size of queue:");
                int queueSize  = scanner.nextInt();
                double arrivalRate = (rho * averageTransmissonRate) / averagePacketLength;
                MM1K(arrivalRate, 1/averagePacketLength, averageTransmissonRate, simulationTime, queueSize);
                break;
            }
        }
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
