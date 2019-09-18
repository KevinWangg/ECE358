package Utils;

public class ExponentialRandomVariable {
    public double generateExponentialRandomVariable(double uniformRandomVariable, double rateParameter) {
        double exponentialRandomVariable = - (1/rateParameter) * Math.log(1 - uniformRandomVariable);
        return exponentialRandomVariable;
    }

    public double
}
