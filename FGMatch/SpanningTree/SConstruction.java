package SpanningTree;

import java.util.ArrayList;
import java.util.HashMap;

import MGraph.Edge;
import MGraph.Graph;
import WeightMatrix.weightMatrix;

public class SConstruction {

	public weightMatrix wm;
	public STree st;
	private int[] weightState;
	private ArrayList<Integer> candidate;
	private int depth = 1;
	private int pathdepth = 1;
	private ArrayList<Integer> nodeID;
	public Graph query;
	public HashMap<Integer, ArrayList<Integer>> neighbour;
	public int[] parent;
	public int size = 0;
	public int[] nodeOrder;
	

	public SConstruction(weightMatrix wm) {
		this.wm = wm;
		this.STconstruction();
	}

	public void STconstruction() {
		st = new STree(wm.query.name);
		candidate = new ArrayList<Integer>();
		query = wm.query;
		weightState = new int[wm.query.nodes.size()];
		for (int i = 0; i < wm.query.nodes.size(); i++) {
			weightState[i] = 0;
		}

		for (int i = 0; i < wm.query.nodes.size(); i++) {
			this.consST();
		}
	
		
//		this.mergePath();
//		this.updateNodeOrder();
		this.insertNonTreeEdge();
	}

	public void consST() {
		if (st.nodes.isEmpty()) {
			double[][] weightmatrix = wm.weightmatrix;
			double maxVertex = 0;
			int temp = 0;
			for (int i = 0; i < wm.query.nodes.size(); i++) {
				if (maxVertex <= weightmatrix[i][i]) {
					maxVertex = weightmatrix[i][i];
					temp = i;
				}
			}
			SNode node = new SNode(st.nodes.size());
			node.setBooleanLeafNode(true);
			candidate.add(temp);
			weightState[temp] = 1;
			node.setVID(temp);
			st.insertNode(node);
		} else {
			double[] weight = new double[wm.query.nodes.size()];
			double[] weightMax = new double[wm.query.nodes.size()];
			for (int i = 0; i < wm.query.nodes.size(); i++) {
				weight[i] = 0;
				weightMax[i] = 0;
			}
			ArrayList<Integer> hop1Candidate = new ArrayList<Integer>();
			for (int i = 0; i < candidate.size(); i++) {
				// get the nodes of query graph
				// compute the 1-hop candidates;
				for (int j = 0; j < wm.query.nodes.size(); j++) {
					if (weightState[j] == 0) {
						weight[j] = weight[j]
								+ wm.weightmatrix[j][candidate.get(i)]
										/ wm.weightmatrix[candidate.get(i)][candidate.get(i)]
								+ wm.weightmatrix[candidate.get(i)][j]
										/ wm.weightmatrix[candidate.get(i)][candidate.get(i)];
						if (!hop1Candidate.contains(j) && weight[j] != 0) {
							hop1Candidate.add(j);
						}
					}
				}
			}

			// compute the 2-hop candidates;

			for (int i = 0; i < hop1Candidate.size(); i++) {
				for (int j = 0; j < wm.query.nodes.size(); j++) {
					if (hop1Candidate.contains(j) && weightState[j] == 0) {
						weight[hop1Candidate.get(i)] = weight[hop1Candidate.get(i)]
								+ wm.weightmatrix[j][hop1Candidate.get(i)]
										/ wm.weightmatrix[hop1Candidate.get(i)][hop1Candidate.get(i)]
								+ wm.weightmatrix[hop1Candidate.get(i)][j]
										/ wm.weightmatrix[hop1Candidate.get(i)][hop1Candidate.get(i)];
					}
				}
			}

			// insert a new snode;

			double maxVertex = -1;
			int temp = -1;
			for (int i = 0; i < wm.query.nodes.size(); i++) {
				if (maxVertex <= weight[i] && weightState[i] == 0) {
					maxVertex = weight[i];
					temp = i;
				}
			}

			if (temp == -1) {
				for (int i = 0; i < wm.query.nodes.size(); i++) {
					if (weightState[i] == 0) {
						temp = i;
						break;
					}

				}
			}

			SNode node = new SNode(st.nodes.size());
			node.setBooleanLeafNode(true);
			candidate.add(temp);
			weightState[temp] = 1;
			node.setVID(temp);
			st.insertNode(node);

			// compute the max weight adjacent nodes in query of the snode;

			double maxParent = 0;
			int tempParent = 0;
			for (int i = 0; i < candidate.size(); i++) {
				// get the max parent nodes of query graph
				if (candidate.get(i) != temp) {
					weightMax[candidate.get(i)] = weightMax[candidate.get(i)] + wm.weightmatrix[temp][candidate.get(i)]
							+ wm.weightmatrix[candidate.get(i)][temp];
				}
			}
			for (int i = 0; i < weightMax.length; i++) {
				if (maxParent <= weightMax[i]) {
					maxParent = weightMax[i];
					tempParent = i;
				}
			}

			// insert sedge;

			int parentId = this.getSNode(tempParent);
			int childId = this.getSNode(temp);
			SEdge sedge = new SEdge(st.nodes.get(parentId), st.nodes.get(childId));
			st.nodes.get(parentId).setBooleanLeafNode(false);
			st.nodes.get(parentId).insertTE(sedge);
			st.TE.add(sedge);
		}
	}
	
	public void updateNodeOrder() {
		
		nodeOrder = new int[st.nodes.size()];
		for(int i = 0; i < st.nodes.size(); i++) {
			nodeOrder[i] = -1;
			st.nodes.get(i).leafNode = false;
			if(st.nodes.get(i).TE.size() == 0) {
				st.nodes.get(i).leafNode = true;
			}
		}
			
		nodeOrder[st.nodes.get(0).sId] = 0; 
		size ++;
		this.iterNodeOrder(st.nodes.get(0));
		
		for(int i = 0; i < st.nodes.size(); i++) {
			st.nodes.get(i).sId = nodeOrder[st.nodes.get(i).sId];
		}
		
		ArrayList<SNode> nodes = new ArrayList<SNode>();
		for(int i = 0; i < st.nodes.size(); i++) {
			for(int j = 0; j < st.nodes.size(); j++) {
				if(st.nodes.get(j).sId == i) {
					nodes.add(st.nodes.get(j));
					break;
				}
			}
			
		}
		st.nodes = nodes;	
		
		
	}
	public boolean iterNodeOrder(SNode node){
		if(size == st.nodes.size()) {
			return true;
		} else {
			ArrayList<SNode> temp = this.getOrderNode(node);
			for(SNode snode : temp) {
				nodeOrder[snode.sId] = size;
				size ++;
				if(this.iterNodeOrder(snode)) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	public ArrayList<SNode> getOrderNode(SNode node){
		ArrayList<SNode> temp = new ArrayList<SNode>();
		for(SEdge edge : node.TE) {
			temp.add(edge.child);
		}
		return this.ArrayOrderNode(temp);
	}
	
	public ArrayList<SNode> ArrayOrderNode(ArrayList<SNode> nodes){
		ArrayList<SNode> newSnodes = new ArrayList<SNode>();
		int temp = nodes.size();
		while(temp > 0) {
			int min = -1;
			double maxWeight = 0;
			for(int i= 0; i < nodes.size(); i++) {
				if(maxWeight <= nodes.get(i).weight) {
					maxWeight = nodes.get(i).weight;
					min = i;
				}
			}
			newSnodes.add(nodes.get(min));
			nodes.remove(min);
			temp = nodes.size();
		}
		return newSnodes;
	}
	

	public void insertNonTreeEdge() {
		for (int i = 0; i < wm.query.edges.size(); i++) {
			Edge edge = wm.query.edges.get(i);
			if (!this.edgeContain(edge) && !this.edgeNTEContain(edge)) {
				int sourceIndex = this.getSNode(edge.source.id);
				int targetIndex = this.getSNode(edge.target.id);
				if (sourceIndex < targetIndex) {
					SEdge sedge = new SEdge(st.nodes.get(targetIndex), st.nodes.get(sourceIndex));
					sedge.setBooleanNTE(true);
					if (!this.nodeNTEJude(st.nodes.get(targetIndex).NTE, sourceIndex)) {
						st.nodes.get(targetIndex).insertNTE(sedge);
						st.nodes.get(targetIndex).setBooleanNTE(true);
					}
					st.NTE.add(sedge);
				} else {
					SEdge sedge = new SEdge(st.nodes.get(sourceIndex), st.nodes.get(targetIndex));
					sedge.setBooleanNTE(true);
					if (!this.nodeNTEJude(st.nodes.get(sourceIndex).NTE, targetIndex)) {
						st.nodes.get(sourceIndex).insertNTE(sedge);
						st.nodes.get(sourceIndex).setBooleanNTE(true);
					}
					st.NTE.add(sedge);
				}
			}
		}

	}

	public boolean nodeNTEJude(ArrayList<SEdge> NTE, int nodesid) {
		for (SEdge edge : NTE) {
			if (edge.child.sId == nodesid) {
				return true;
			}
		}
		return false;
	}

	private boolean edgeContain(Edge edge) {
		boolean flag = false;
		int sourceID = this.getSNode(edge.source.id);
		int targetID = this.getSNode(edge.target.id);
		SNode source = st.nodes.get(sourceID);
		SNode target = st.nodes.get(targetID);

		for (int i = 0; i < st.TE.size(); i++) {
			if (source.sId == st.TE.get(i).parent.sId && target.sId == st.TE.get(i).child.sId) {
				flag = true;
				break;
			}
			if (source.sId == st.TE.get(i).child.sId && target.sId == st.TE.get(i).parent.sId) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private boolean edgeNTEContain(Edge edge) {
		boolean flag = false;
		int sourceID = this.getSNode(edge.source.id);
		int targetID = this.getSNode(edge.target.id);
		SNode source = st.nodes.get(sourceID);
		SNode target = st.nodes.get(targetID);

		for (int i = 0; i < st.NTE.size(); i++) {
			if (source.sId == st.NTE.get(i).parent.sId && target.sId == st.NTE.get(i).child.sId) {
				flag = true;
				break;
			}
			if (source.sId == st.NTE.get(i).child.sId && target.sId == st.NTE.get(i).parent.sId) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private int getSNode(int id) {
		int temp = -1;
		for (int i = 0; i < st.nodes.size(); i++) {
			if (st.nodes.get(i).vid == id) {
				temp = i;
				break;
			}
		}
		return temp;
	}

	// forward attachment

	public void ForwardAttachment() {
		st.nodes.get(0).setDepth(depth);
		depth++;
		nodeID = new ArrayList<Integer>();
		for (int i = 0; i < st.nodes.get(0).TE.size(); i++) {
			st.nodes.get(0).TE.get(i).child.setDepth(depth);
			double weight = st.nodes.get(0).weight
					+ wm.weightmatrix[st.nodes.get(0).TE.get(i).parent.vid][st.nodes.get(0).TE.get(i).child.vid];
			st.nodes.get(0).TE.get(i).child.setWeight(weight);
			if (st.nodes.get(0).TE.get(i).child.leafNode == false) {
				nodeID.add(st.nodes.get(0).TE.get(i).child.sId);
			}
		}

		if (!nodeID.isEmpty()) {
			depth++;
			this.iterativeForwardAttachment();
		}
	}

	private void iterativeForwardAttachment() {
		ArrayList<Integer> nodeid = nodeID;
		nodeID = new ArrayList<Integer>();
		for (int i = 0; i < nodeid.size(); i++) {
			for (int j = 0; j < st.nodes.get(nodeid.get(i)).TE.size(); j++) {
				st.nodes.get(nodeid.get(i)).TE.get(j).child.setDepth(depth);
				double weight = st.nodes.get(nodeid.get(i)).weight + wm.weightmatrix[st.nodes.get(nodeid.get(i)).TE
						.get(j).parent.vid][st.nodes.get(nodeid.get(i)).TE.get(j).child.vid];
				st.nodes.get(nodeid.get(i)).TE.get(j).child.setWeight(weight);

				if (!st.nodes.get(nodeid.get(i)).TE.get(j).child.leafNode) {
					nodeID.add(st.nodes.get(nodeid.get(i)).TE.get(j).child.sId);
				}
			}
		}
		if (!nodeID.isEmpty()) {
			depth++;
			this.iterativeForwardAttachment();
		}
	}
	
	public void BackwardAttachment() {
		parent = new int[st.nodes.size()];
		for(int i = 0; i < st.nodes.size(); i++) {
			parent[i] = -1;
		}		
		for(SEdge sedge :st.TE) {
			parent[sedge.child.sId] = sedge.parent.sId;
		}
		nodeID = new ArrayList<Integer>();
		for(int i = 0; i < st.nodes.size(); i++) {
			if(st.nodes.get(i).leafNode) {
				nodeID.add(i);
			}
		}

		if (!nodeID.isEmpty()) {
			this.iterativeBackwardAttachment();
		}
	}
	
	private void iterativeBackwardAttachment() {
		ArrayList<Integer> nodeid = nodeID;
		nodeID = new ArrayList<Integer>();
		for (int i = 0; i < nodeid.size(); i++) {
			if(st.nodes.get(nodeid.get(i)).pathDepth < pathdepth) {
				st.nodes.get(nodeid.get(i)).pathDepth = pathdepth;
			}
			if(parent[nodeid.get(i)] != -1) {
				if(!nodeID.contains(parent[nodeid.get(i)])) {
					nodeID.add(parent[nodeid.get(i)]);
				}
			}
		}
		if (!nodeID.isEmpty()) {
			pathdepth++;
			this.iterativeForwardAttachment();
		}
	}

	public void updateParentChild() {
		
		for(int i = 0; i < st.nodes.size(); i++) {
			st.nodes.get(i).leafNode = false;
			if(st.nodes.get(i).TE.size() == 0) {
				st.nodes.get(i).leafNode = true;
			}
		}
		this.ForwardAttachment();
		this.BackwardAttachment();
		
	}

	// mergePath
	public void mergePath() {
		neighbour = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 0; i < query.nodes.size(); i++) {
			ArrayList<Integer> array = new ArrayList<Integer>();
			for (Edge sedge : query.nodes.get(i).inEdges) {
				array.add(this.getSNode(sedge.source.id));
			}
			for (Edge sedge : query.nodes.get(i).outEdges) {
				array.add(this.getSNode(sedge.target.id));
			}
			neighbour.put(i, array);
		}
		
		HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
		for(int i = 0; i < st.nodes.size(); i++) {
			visited.put(i, false);
		}
		visited.put(0, true);
		this.updateParentChild();
		int count  = 1;
		while (count < st.nodes.size()) { 
			if(!visited.get(count)) {
				SNode u = st.nodes.get(count);
				for(int uDash : neighbour.get(count)) {
					if(visited.get(uDash)) {
						SNode nu = st.nodes.get(uDash);
						if(parent[count] != uDash && u.weight < nu.weight && u.depth + nu.pathDepth <= depth && u.depth + nu.depth <=depth) {
							SEdge edge = new SEdge(u, nu);
							st.nodes.get(count).insertTE(edge);
							st.TE.add(edge);
							int temp = uDash;
							while (parent[temp] != -1) {
								SEdge edgeNew = new SEdge(st.nodes.get(temp), st.nodes.get(parent[temp]));
								st.nodes.get(temp).insertTE(edgeNew);
								st.TE.add(edgeNew);
								int id = this.getdeletedTEId(parent[temp], temp, st.nodes.get(parent[temp]).TE);
								st.nodes.get(parent[temp]).TE.remove(id);
								id = this.getdeletedTEId(parent[temp], temp, st.TE);
								st.TE.remove(id);
								temp = parent[temp];
							}
							int id = this.getdeletedTEId(0, temp, st.nodes.get(0).TE);
							st.nodes.get(0).TE.remove(id);
							id = this.getdeletedTEId(0, temp, st.TE);
							st.TE.remove(id);
							count = 1;
							this.updateParentChild();
							for(int i = 1; i < st.nodes.size(); i++) {
								visited.put(i, false);
							}
							break;
						}
						
					}									
				}
				visited.put(0, true);
				count ++;
			} else {
			count ++;
			}
		}

	}
	public int getdeletedTEId(int parentId, int childId, ArrayList<SEdge> sedge) {
		int temp = -1;
		for(int i = 0; i < sedge.size(); i++) {
			if(sedge.get(i).parent.sId == parentId && sedge.get(i).child.sId == childId) {
				temp = i;
				break;
			}
		}
		return temp;
	}

}
