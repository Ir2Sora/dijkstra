package some.graph;

public class WeightedEdge {

    private double weight;

    public WeightedEdge() {
        weight = 1;
    }

    public WeightedEdge(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return Double.toString(weight);
    }
}
