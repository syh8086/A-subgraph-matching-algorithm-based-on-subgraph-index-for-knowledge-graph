package FGQTIndex;

import java.util.ArrayList;

public class FGraph {
	
	public ArrayList<FNode> FNodes;
	public ArrayList<FEdge> TEs;
	public ArrayList<FEdge> NTEs;
    public int[] nodeState;
	
	
	public FGraph() {
		this.FNodes = new ArrayList<FNode>();
		this.TEs = new ArrayList<FEdge>();
		this.NTEs = new ArrayList<FEdge>();
	}
	
	public void insertFNode(FNode fnode) {
		this.FNodes.add(fnode);
	}

	
	public void insertTE(FEdge fedge) {
		this.TEs.add(fedge);
	}
	
	public void insertNTE(FEdge fedge) {
		this.NTEs.add(fedge);
	}
	
	public void setNodeState(int[] nodeState) {
		this.nodeState = nodeState;
	}

}
