import Queues.QueueEvent;
import Utils.ExponentialRandomVariable;
import Queues.MM1Queue;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        question1();
        MM1(125, 2000,1000000, 1000);
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
        MM1Queue queue = new MM1Queue(packetsPerSecond, packetLength, transmissonRate, totalSimulationTime);
        queue.addEvents();
//        for (QueueEvent x : queue.queue){
//            System.out.println(String.format("Time: %s, Type: %s",x.eventTime, x.queueType));
//        }
//        System.out.println(queue.queue.size());
        System.out.println(queue.queue.size());

    }
}
