package pn_visualization;

import com.mxgraph.view.mxGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

public class PetriNetPlaceCell implements PetriNetComponentCell
{
	private mxCell mainCell;
	
	public PetriNetPlaceCell(mxGraph graph, String name)
	{
		Object parent = graph.getDefaultParent();

		mainCell = (mxCell) graph.insertVertex(parent, null, "", 20, 20, 
				40, 40, "shape=ellipse;foldable=0");//, "verticalLabelPosition=bottom;shape=rectangle;perimeter=rectanglePerimeter");
					
		mxGeometry nameV1 = new mxGeometry(0.5, 1.2, 0, 0);
		//nameV1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		nameV1.setRelative(true);

		mxCell nameLabel = new mxCell(name, nameV1, "shape=rectangle");
		nameLabel.setVertex(true);
			
		graph.addCell(nameLabel, mainCell);
			
	}
	
	public mxCell getMainCell() {
		return mainCell;
	}

}
