import Utils.ExponentialRandomVariable;
import import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<double> exponentialRandomVariablesList = new ArrayList<double>();
        ExponentialRandomVariable exponentialRandomVariable = new ExponentialRandomVariable();
        for (int i = 0; i < 1000; i ++) {
            double randomVariable = Math.random();
            exponentialRandomVariablesList.add(exponentialRandomVariable.generateExponentialRandomVariable(randomVariable, 0.75));
        }


    }
}
