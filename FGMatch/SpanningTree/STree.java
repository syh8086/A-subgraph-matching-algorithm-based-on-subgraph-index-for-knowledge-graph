package SpanningTree;

import java.util.ArrayList;

public class STree {
	
	public ArrayList<SNode> nodes;
	public ArrayList<SEdge> TE;
	public ArrayList<SEdge> NTE;
	
	public String name;
	
	public STree(String name) {
		this.name = name;
		this.nodes = new ArrayList<SNode>();
		this.TE = new ArrayList<SEdge>();
		this.NTE = new ArrayList<SEdge>();
	}

	
	public void insertNode(SNode node) {
		this.nodes.add(node);
	}
	
}
