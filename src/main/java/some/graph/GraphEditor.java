package some.graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxStyleUtils;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Random;

public class GraphEditor extends JApplet {

    private static final Dimension DEFAULT_SIZE = new Dimension(900, 900);

    private ListenableGraph<String, DefaultEdge> graph;
    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;

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
        graph = new ListenableUndirectedGraph<>(DefaultEdge.class);
        jgxAdapter = new JGraphXAdapter<>(graph);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";
        
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);

        graph.addEdge(v1, v2);
        graph.addEdge(v2, v3);
        graph.addEdge(v3, v1);
        graph.addEdge(v4, v3);

        Box bh = Box.createHorizontalBox();
        bh.add(generateNavigationPanel());
        bh.add(new mxGraphComponent(jgxAdapter));

        getContentPane().add(bh);

        redraw();
    }

    private JPanel generateNavigationPanel() {
        JPanel p = new JPanel();

        JButton addButton = new JButton("Добавить");
        p.add(addButton);

        JTextField numberVertex = new JFormattedTextField(NumberFormat.getInstance());
        numberVertex.setColumns(10);
        p.add(numberVertex);

        addButton.addActionListener(e -> {
            graph.addVertex(new Random().nextInt() + "");

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