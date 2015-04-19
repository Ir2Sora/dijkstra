package some.graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.ListenableUndirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
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

        mxEventSource.mxIEventListener listener = new GraphEventListener(this::redraw);
        jgxAdapter.addListener(mxEvent.START_EDITING, listener);
        jgxAdapter.addListener(mxEvent.LABEL_CHANGED, listener);


        jgxAdapter.addListener(mxEvent.CELL_CONNECTED, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object o, mxEventObject mxEventObject) {
                redraw();
            }
        });

        mxGraphComponent mxGraph = new mxGraphComponent(jgxAdapter);

        bh.add(mxGraph);
        getContentPane().add(bh);

        redraw();
    }

    private JPanel generateNavigationPanel() {
        JPanel p = new JPanel();

        p.setPreferredSize(new Dimension(200, 900));

        JButton bAdd = new JButton("Добавить");
        p.add(bAdd);

        JTextField tNumberVertex = new JFormattedTextField(NumberFormat.getInstance());
        tNumberVertex.setColumns(10);
        p.add(tNumberVertex);

        bAdd.addActionListener(e -> {
            graph.removeAllEdges(new ArrayList<>(graph.edgeSet()));
            graph.removeAllVertices(new ArrayList<>(graph.vertexSet()));

            if (!StringUtils.isEmpty(tNumberVertex.getText())) {
                int numberVertex = Integer.parseInt(tNumberVertex.getText());
                IntStream.rangeClosed(1, numberVertex)
                        .forEach(i -> graph.addVertex(i));
            }

            redraw();
        });

        return p;
    }

    private void redraw() {
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel  = (mxGraphModel) graphComponent.getGraph().getModel();

        Collection<Object> cells =  graphModel.getCells().values();
        mxStyleUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }
}