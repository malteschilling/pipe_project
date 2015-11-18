package pn_visualization;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.layout.mxCompactTreeLayout;

import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

public class VisualizationPetriNetFrame extends JFrame {
	
	private static final long serialVersionUID = -2707712944901661771L;
	
	private PetriNet petriNet;
	
	public VisualizationPetriNetFrame(PetriNet petriNet)  {
		super("PetriNet Visualization");
		this.petriNet = petriNet;
		
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		
		Map<String, Object> stil = new HashMap<String, Object>();
		stil.put(mxConstants.STYLE_ROUNDED, true);
		stil.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION); // <-- This is what you want
		stil.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		stil.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		stil.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		stil.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		stil.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
		stil.put(mxConstants.STYLE_FONTCOLOR, "#446299");

		mxStylesheet styleEdges = new mxStylesheet();
		styleEdges.setDefaultEdgeStyle(stil);
		graph.setStylesheet(styleEdges);
		graph.setKeepEdgesInBackground(true);

		graph.getModel().beginUpdate();
		
		Map<String, PetriNetComponentCell> objectMap = new HashMap<>();
		try
		{
			for (Place place : petriNet.getPlaces()) {
				PetriNetPlaceCell visPlace = new PetriNetPlaceCell(graph, place.getId() );
    			objectMap.put(place.getId(), visPlace);
            }

            for (Transition transition : petriNet.getTransitions()) {
            	PetriNetTransitionCell visTrans = new PetriNetTransitionCell(graph, transition.getId() );
    			objectMap.put(transition.getId(), visTrans);
            }

            for (Arc<? extends Connectable, ? extends Connectable> arc : petriNet.getArcs()) {
                PetriNetComponentCell source = objectMap.get(arc.getSource().getId());
                PetriNetComponentCell target = objectMap.get(arc.getTarget().getId());
                graph.insertEdge(parent, arc.getId(), "", source.getMainCell(), target.getMainCell() );
            }		
		}
		finally
		{
			graph.getModel().endUpdate();
		}		
		
    	mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
    	layout.setEdgeRouting( true );
        //layout.setLevelDistance( 100 );
        layout.setNodeDistance( 50 );
    	layout.execute(graph.getDefaultParent());

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		
	}

}
