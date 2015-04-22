package some.graph;

import java.util.stream.IntStream;

public class PathFinder {

    private double[][] weights;

    public PathFinder(double[][] weights) {
        this.weights = weights;
    }

    public double[] compute(int origin) {
        double[] d = new double[weights.length];

        for (int i = 0; i < d.length; i++) {
            d[i] = Double.POSITIVE_INFINITY;
        }
        d[origin] = 0;

        for (int j = origin + 1; j < d.length; j++) {
            int[] prev = getPrevious(j);

            for (int i = 0; i < prev.length; i++) {
                d[j] = Math.min(d[j], d[prev[i]] + weights[prev[i]][j]);
            }
        }

        return d;
    }

    private int[] getPrevious(int j) {
        return IntStream.rangeClosed(0, j)
                .filter(i -> weights[i][j] > 0)
                .toArray();
    }
}
