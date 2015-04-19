package some.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

public class GraphEventListener implements mxEventSource.mxIEventListener {

    private Integer vertexOldValue;
    private String edgeOldValue;

    @Override
    public void invoke(Object o, mxEventObject event) {
        mxCell cell = (mxCell) event.getProperty("cell");

        if (event.getName().equals(mxEvent.START_EDITING)) {
            setOldValue(cell);
        }

        if (event.getName().equals(mxEvent.LABEL_CHANGED)) {
            setNewValue(cell);
        }
    }

    private void setOldValue(mxCell cell) {
        if (cell.isVertex()) {
            vertexOldValue = (Integer) cell.getValue();
        } else {
            edgeOldValue = (String) cell.getValue();
        }
    }

    private void setNewValue(mxCell cell) {
        if (cell.isVertex()) {
            cell.setValue(getVertexValue((String) cell.getValue()));
        } else {
            cell.setValue(getEdgeValue((String) cell.getValue()));
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
}
