package Utils;

// Function to calculate the arrival times of the nodes
public class ExponentialRandomVariable {
    public static double generateRandomVariable(double rateParameter) {
        double randomVariable = Math.random();
        return - (1/rateParameter) * Math.log(1 - randomVariable);
    }
}