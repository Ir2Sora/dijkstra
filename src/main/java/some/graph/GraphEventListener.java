package some.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

public class GraphEventListener implements mxEventSource.mxIEventListener {

    private Integer vertexOldValue;
    private String edgeOldValue;

    private RedrawHandler redrawHandler;

    public GraphEventListener(RedrawHandler redrawHandler) {
        this.redrawHandler = redrawHandler;
    }

    @Override
    public void invoke(Object o, mxEventObject event) {
        mxCell cell = (mxCell) event.getProperty("cell");

        if (event.getName().equals(mxEvent.START_EDITING)) {
            if (cell.isVertex()) {
                vertexOldValue = (Integer) cell.getValue();
            } else {
                edgeOldValue = (String) cell.getValue();
            }
        }

        if (event.getName().equals(mxEvent.LABEL_CHANGED)) {
            if (cell.isVertex()) {
                cell.setValue(getVertexValue((String) cell.getValue()));
            } else {
                cell.setValue(getEdgeValue((String) cell.getValue()));
            }

            redrawHandler.redraw();
        }
    }

    private Integer getVertexValue(String newStrValue) {
        try {
            return Integer.valueOf(newStrValue);
        } catch (NumberFormatException e) {
            return vertexOldValue;
        }
    }

    private String getEdgeValue(String newStrValue) {
        try {
            return Double.valueOf(newStrValue).toString();
        } catch (NumberFormatException e) {
            return edgeOldValue;
        }
    }

    public static interface RedrawHandler {
        void redraw();
    }
}
