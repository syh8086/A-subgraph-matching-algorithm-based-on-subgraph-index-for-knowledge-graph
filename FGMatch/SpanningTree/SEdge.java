package SpanningTree;

public class SEdge {
	
	public SNode parent;
	public SNode child;
	public Boolean NTEVerfication = false;
	
	public SEdge(SNode parent, SNode child) {
		this.parent = parent;
		this.child = child;
	}
	
	public void setBooleanNTE(Boolean flag) {
		this.NTEVerfication  = flag;
	}

}
