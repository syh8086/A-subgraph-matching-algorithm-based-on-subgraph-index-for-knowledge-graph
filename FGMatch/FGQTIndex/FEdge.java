package FGQTIndex;

public class FEdge {
	
	public FNode source;
	public FNode target;
	public int[] labelArray;
	public boolean NTEVerficatioon = false;
	
	public FEdge(FNode source, FNode target, int nodesize) {
		this.source = source;
		this.target = target;
		labelArray = new int[nodesize];
		for(int i = 0; i < nodesize; i++) {
			labelArray[i] = 0;
		}
	}
	
	public void setLabelArray(int sid, int sign) {
		labelArray[sid] = sign;
	}
	
	public void setBooleanNTE(Boolean flag){
		this.NTEVerficatioon = flag;
	}

}
