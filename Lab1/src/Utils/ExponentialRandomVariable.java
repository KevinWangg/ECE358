package Utils;
import java.util.ArrayList;

public class ExponentialRandomVariable {
    public static double generateRandomVariable(double rateParameter) {
        double randomVariable = Math.random();
        return - (1/rateParameter) * Math.log(1 - randomVariable);
    }

    public static double calculateExpectedValue(ArrayList<Double> randomVariableList) {
        double totalValue = 0;
        for (double randomVariable : randomVariableList){
            totalValue += randomVariable;
        }
        return totalValue / randomVariableList.size();
    }

    public static double calculateVariance(ArrayList<Double> randomVariableList, double expectedValue) {
        double totalValue = 0;
        for (double randomVariable : randomVariableList){
            totalValue += Math.pow(expectedValue - randomVariable, 2);
        }
        return totalValue / randomVariableList.size();
    }
}
