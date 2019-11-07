import CSMACD.Persistent;
import CSMACD.NonPersistent;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        switch (args[0]) {
            case "1": {
                System.out.println("********************************************************************************* \n");
                System.out.println("Persistent CSMACD Simulations \n");

                calculatePersistentCSMACD();

                System.out.println("\n");
                break;
            }
            case "2": {
                System.out.println("********************************************************************************* \n");
                System.out.println("Non-Persistent CSMACD Simulations  \n");

                nonPersistentCSMACD();

                System.out.println("\n");
                break;
            }
            case "3": {
                double simulationTime  = Double.valueOf(args[1]);
                double arrivalRate  = Double.valueOf(args[2]);
                int numNodes = Integer.valueOf(args[3]);
                System.out.printf("The values for a persistent CSMACD simulation with packet arrival rate of %f and %d nodes. (Number of nodes, efficiency, throughput(bps), throughput(mbps) %n", arrivalRate, numNodes);
                Persistent persistentCSMACD = new Persistent(numNodes, arrivalRate, 1000000, 1500, 10, 200000000, simulationTime);
                persistentCSMACD.initalizeNodes();
                persistentCSMACD.startSimulation();
                persistentCSMACD.calculateEfficiency();
                System.out.println("\n");
                break;
            }
            case "4": {
                double simulationTime  = Double.valueOf(args[1]);
                double arrivalRate  = Double.valueOf(args[2]);
                int numNodes = Integer.valueOf(args[3]);
                System.out.printf("The values for a Non-persistent CSMACD simulation with packet arrival rate of %f and %d nodes. (Number of nodes, efficiency, throughput(bps), throughput(mbps) %n", arrivalRate, numNodes);
                NonPersistent NonPersistentCSMACD = new NonPersistent(numNodes, arrivalRate, 1000000, 1500, 10, 200000000, simulationTime);
                NonPersistentCSMACD.initalizeNodes();
                NonPersistentCSMACD.startSimulation();
                NonPersistentCSMACD.calculateEfficiency();
                System.out.println("\n");
                break;
            }
        }

    }

    // Runs the simulations for persistent CSMACD for the values provided in the labs
    public static void calculatePersistentCSMACD() {
        ArrayList <Integer> arrivalRate = new ArrayList<Integer>();
        arrivalRate.add(7);
        arrivalRate.add(10);
        arrivalRate.add(20);
        double transmissionTime;
        for (int rate: arrivalRate) {
            System.out.printf("The values for a persistent CSMACD simulation with packet arrival rate of %d. (Number of nodes, efficiency, throughput(bps), throughput(mbps) %n", rate);
            //Set transmission time for TA simulation so it doesnt take too long, we checked to see if the results are within +/- 5% in our report
            for (int nodes = 20; nodes <= 100; nodes += 20) {
                if(nodes > 60 && rate >= 10 ){
                    transmissionTime = 100;
                } else if (rate == 20) {
                    transmissionTime = 100;
                } else{
                    transmissionTime = 1000;
                }
                Persistent persistentCSMACD = new Persistent(nodes, rate, 1000000, 1500, 10, 200000000, transmissionTime);
                persistentCSMACD.initalizeNodes();
                persistentCSMACD.startSimulation();
                persistentCSMACD.calculateEfficiency();
            }
        }

    }

    // Runs the simulations for non-persistent CSMACD for the values provided in the labs
    public static void nonPersistentCSMACD() {
        ArrayList <Integer> arrivalRate = new ArrayList<Integer>();
        arrivalRate.add(7);
        arrivalRate.add(10);
        arrivalRate.add(20);
        double transmissionTime;
        for (int rate: arrivalRate) {
            System.out.printf("The values for a non-persistent CSMACD simulation with packet arrival rate of %d. (Number of nodes, efficiency, throughput(bps), throughput(mbps) %n", rate);
            for (int nodes = 20; nodes <= 100; nodes += 20) {
                //Set transmission time for TA simulation so it doesnt take too long, we checked to see if the results are within +/- 5% in our report
                if(nodes > 60 && rate >= 10 ){
                    transmissionTime = 100;
                } else if (rate == 20) {
                    transmissionTime = 100;
                } else{
                    transmissionTime = 1000;
                }
                NonPersistent NonPersistentCSMACD = new NonPersistent(nodes, rate, 1000000, 1500, 10, 200000000, transmissionTime);
                NonPersistentCSMACD.initalizeNodes();
                NonPersistentCSMACD.startSimulation();
                NonPersistentCSMACD.calculateEfficiency();
            }
        }

    }

}
