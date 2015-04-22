package some.graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.softsmithy.lib.swing.JDoubleField;
import org.softsmithy.lib.swing.JIntegerField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class GraphEditor extends JApplet {

    private static final Dimension DEFAULT_SIZE = new Dimension(900, 900);
    private static final int DEFAULT_VERTEX_NUM = 5;

    private ListenableGraph<Integer, WeightedEdge> graph;
    private JGraphXAdapter<Integer, WeightedEdge> jgxAdapter;
    private JPanel weightsPanel;

    private double[][] weights;

    public GraphEditor() {
        graph = new ListenableDirectedGraph<>(WeightedEdge.class);
        jgxAdapter = new JGraphXAdapter<>(graph);
        weightsPanel = new JPanel(new GridLayout(DEFAULT_VERTEX_NUM + 1, DEFAULT_VERTEX_NUM + 1));
        weights = new double[DEFAULT_VERTEX_NUM][DEFAULT_VERTEX_NUM];
    }

    public static void main(String [] args) {
        GraphEditor applet = new GraphEditor();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Some amazing app");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(DEFAULT_SIZE);
        frame.pack();
        frame.setVisible(true);
    }

    public void init() {
        Box bh = Box.createHorizontalBox();
        bh.add(generateNavigationPanel());

        jgxAdapter.setCellsEditable(false);
        jgxAdapter.setAllowDanglingEdges(false);

        mxGraphComponent mxGraph = new mxGraphComponent(jgxAdapter);
        mxGraph.setConnectable(false);

        bh.add(mxGraph);
        getContentPane().add(bh);

        changeVertexNum(DEFAULT_VERTEX_NUM);
    }

    private JPanel generateNavigationPanel() {
        JPanel p = new JPanel();

        p.setPreferredSize(new Dimension(200, 900));

        JButton bAdd = new JButton("Сгенерировать");
        p.add(bAdd);

        JIntegerField tVertexNum = new JIntegerField();
        tVertexNum.getIntegerFormatter().getNumberFormat().setGroupingUsed(false);
        tVertexNum.getIntegerFormatter().setMinimumIntValue(2);
        tVertexNum.setText(DEFAULT_VERTEX_NUM + "");
        tVertexNum.setColumns(10);

        p.add(tVertexNum);
        p.add(weightsPanel);

        JButton bCompute = new JButton("Рассчитать");
        p.add(bCompute);

        JIntegerField tVertexOrigin = new JIntegerField(1, Integer.parseInt(tVertexNum.getText()));
        tVertexOrigin.getIntegerFormatter().getNumberFormat().setGroupingUsed(false);
        tVertexOrigin.setText("1");
        tVertexOrigin.setColumns(10);
        p.add(tVertexOrigin);

        JTextArea aAnswer = new JTextArea();
        aAnswer.setEditable(false);
        p.add(aAnswer);

        bAdd.addActionListener(e -> {
            int vertexNum = Integer.parseInt(tVertexNum.getText());
            weights = new double[vertexNum][vertexNum];
            aAnswer.setText("");
            changeVertexNum(vertexNum);
        });

        bCompute.addActionListener(e -> {
            int vertexOrigin = Integer.parseInt(tVertexOrigin.getText());
            double[] distance = new PathFinder(weights).compute(vertexOrigin - 1);

            StringBuilder sb = new StringBuilder();
            sb.append("Расстояние от вершины " + vertexOrigin).append(" до \n");
            for (int i = 0; i < distance.length; i++) {
                sb.append(i + 1).append(" = ").append(distance[i]).append("\n");
            }

            aAnswer.setText(sb.toString());
        });

        return p;
    }

    private void changeVertexNum(int vertexNum) {
        initWeightsPanel(vertexNum);
        initGraph(vertexNum);
    }

    private void initWeightsPanel(int vertexNum) {
        weightsPanel.removeAll();

        weightsPanel.setLayout((new GridLayout(vertexNum + 1, vertexNum + 1)));
        weightsPanel.add(new JLabel(""));

        for (int i = 1; i <= vertexNum; i++) {
            weightsPanel.add(new JLabel("" + i));
        }

        for (int i = 1; i <= vertexNum; i++) {
            weightsPanel.add(new JLabel("" + i));

            for (int j = 1; j <= vertexNum; j++) {
                JDoubleField lWeight = new JDoubleField();

                lWeight.getDoubleFormatter().getNumberFormat().setGroupingUsed(false);
                lWeight.getDoubleFormatter().setMinimumDoubleValue(0);
                lWeight.setColumns(5);
                lWeight.setEnabled(i < j);

                final int row = i - 1;
                final int col = j - 1;
                lWeight.getDocument().addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        update(lWeight.getText());
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        update(lWeight.getText());
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        update(lWeight.getText());
                    }

                    private void update(String text) {
                        try {
                            double d = Double.parseDouble(text.replace(',','.'));
                            weights[row][col] = d;
                            redrawGraph();
                        } catch (NumberFormatException e) {}
                    }
                });

                weightsPanel.add(lWeight);
            }
        }

        revalidate();
        repaint();
    }

    private void initGraph(int vertexNum) {
        graph.removeAllEdges(new ArrayList<>(graph.edgeSet()));
        graph.removeAllVertices(new ArrayList<>(graph.vertexSet()));

        IntStream.rangeClosed(1, vertexNum)
                    .forEach(graph::addVertex);
        initGraph();
    }

    private void initGraph() {
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }

    private void redrawGraph() {
        graph.removeAllEdges(new ArrayList<>(graph.edgeSet()));

        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights.length; j++) {
                if (weights[i][j] > 0) {
                    graph.addEdge(i + 1, j + 1, new WeightedEdge(weights[i][j]));
                }
            }
        }

        jgxAdapter.refresh();
    }
}