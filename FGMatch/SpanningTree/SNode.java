package SpanningTree;

import java.util.ArrayList;

public class SNode {
	public int sId;
	public int vid;
	public ArrayList<SEdge> TE;
	public ArrayList<SEdge> NTE;
	public boolean leafNode = false;
	public boolean NTEVerfication = false;
	public int depth = 0;
	public double weight = 0;
	public int pathDepth = 0;
	
	public SNode(int sId) {
		this.sId = sId;
		this.TE = new ArrayList<SEdge>();
		this.NTE = new ArrayList<SEdge>();
	}
	
	public void setVID(int vid) {
		this.vid = vid;
	}

	public void insertTE(SEdge edge){
		this.TE.add(edge);
	}
	
	public void insertNTE(SEdge edge){
		this.NTE.add(edge);
	}
	
	public void setBooleanNTE(Boolean flag){
		this.NTEVerfication = flag;
	}
	
	public void setBooleanLeafNode(Boolean flag) {
		this.leafNode = flag;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
