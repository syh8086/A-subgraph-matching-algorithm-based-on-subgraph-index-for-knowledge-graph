package FGQTIndex;

import java.util.ArrayList;

public class FNode {
	
	public int fid;
	public int vid;
	public ArrayList<FEdge> inTE;
	public ArrayList<FEdge> outTE;
	public ArrayList<FEdge> inNTE;
	public ArrayList<FEdge> outNTE;

	public FNode(int fid) {
		this.fid = fid;
		inTE = new ArrayList<FEdge>();
		outTE = new ArrayList<FEdge>();
		inNTE = new ArrayList<FEdge>();
		outNTE = new ArrayList<FEdge>();
	}
	
	public void setVID(int vid) {
		this.vid = vid;
	}
	
	public void insertInTE(FEdge fedge) {
		this.inTE.add(fedge);		
	}
	
	public void insertOutTE(FEdge fedge) {
		this.outTE.add(fedge);
	}
	
	public void insertInNTE(FEdge fedge) {
		this.inNTE.add(fedge);
	}
	
	public void insertOutNTE(FEdge fedge) {
		this.outNTE.add(fedge);
	}

}
