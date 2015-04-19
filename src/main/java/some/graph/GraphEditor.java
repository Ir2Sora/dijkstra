package some.graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableUndirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class GraphEditor extends JApplet {

    private static final Dimension DEFAULT_SIZE = new Dimension(900, 900);

    private ListenableGraph<Integer, WeightedEdge> graph;
    private JGraphXAdapter<Integer, WeightedEdge> jgxAdapter;

    public GraphEditor() {
        graph = new ListenableUndirectedGraph<>(WeightedEdge.class);
        jgxAdapter = new JGraphXAdapter<>(graph);
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

        jgxAdapter.setAllowDanglingEdges(false);
        jgxAdapter.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        jgxAdapter.addListener(mxEvent.CELL_CONNECTED, (o, mxEventObject) -> {
            //TODO
        });

        mxGraphComponent mxGraph = new mxGraphComponent(jgxAdapter);
        mxEventSource.mxIEventListener listener = new GraphEventListener();
        mxGraph.addListener(mxEvent.START_EDITING, listener);
        mxGraph.addListener(mxEvent.LABEL_CHANGED, listener);



        bh.add(mxGraph);
        getContentPane().add(bh);

        redraw();
    }

    private JPanel generateNavigationPanel() {
        JPanel p = new JPanel();

        p.setPreferredSize(new Dimension(200, 900));

        JButton bAdd = new JButton("Сгенерировать");
        p.add(bAdd);

        JTextField tNumberVertex = new JFormattedTextField(NumberFormat.getInstance());
        tNumberVertex.setColumns(10);
        p.add(tNumberVertex);

        bAdd.addActionListener(e -> {
            graph.removeAllEdges(new ArrayList<>(graph.edgeSet()));
            graph.removeAllVertices(new ArrayList<>(graph.vertexSet()));

            if (!tNumberVertex.getText().isEmpty()) {
                int numberVertex = Integer.parseInt(tNumberVertex.getText());
                IntStream.rangeClosed(1, numberVertex)
                        .forEach(graph::addVertex);
            }

            redraw();
        });

        return p;
    }

    private void redraw() {
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }
}