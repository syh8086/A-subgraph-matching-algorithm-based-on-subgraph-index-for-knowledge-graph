package FGQTIndex;

import java.util.ArrayList;
import java.util.HashMap;

import MGraph.Edge;
import MGraph.Graph;
import MGraph.Node;
import SpanningTree.SConstruction;
import SpanningTree.SEdge;
import SpanningTree.SNode;
import SpanningTree.STree;

public class ConsFG {

	public ArrayList<ArrayList<Integer>> candidateSet;
	public Graph data;
	public Graph query;
	public int[] nodeState;
	public int[] queryArray;
	public STree st;

	public FGraph FG;
	public int[] parent;
	private int querynodesize;

	private HashMap<Integer, ArrayList<Integer>> neighbourNodes;

	private HashMap<Integer, Boolean> visited;

	public ConsFG(Graph data, SConstruction sc) {
		this.data = data;
		this.query = sc.wm.query;
		this.st = sc.st;
		this.querynodesize = query.nodes.size();
		nodeState = new int[data.nodes.size()];
		for (int i = 0; i < data.nodes.size(); i++) {
			nodeState[i] = 0;
		}

		parent = new int[querynodesize];
		for (int i = 0; i < querynodesize; i++) {
			parent[i] = -1;
		}
	}

	// get the candidate set

	public void candidateSet() {
		candidateSet = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < query.nodes.size(); i++) {
			candidateSet.add(new ArrayList<Integer>());
		}

		for (int i = 0; i < query.nodes.size(); i++) {
			Node queryNode = query.nodes.get(i);
			for (int j = 0; j < data.nodes.size(); j++) {
				Node dataNode = data.nodes.get(j);
				if (this.labelContain(queryNode.labels, dataNode.labels) && this.checkCandidate(dataNode, queryNode)) {
					if (nodeState[j] == 0) {
						dataNode.initialQueryNodeArray(query.nodes.size());
						dataNode.setQueryNodeArray(i, 1);
						nodeState[j] = 1;
						data.nodes.set(j, dataNode);
					} else {
						dataNode.setQueryNodeArray(i, 1);
						data.nodes.set(j, dataNode);
					}
					candidateSet.get(i).add(j);
				}
			}
		}

	}

	private Boolean checkCandidate(Node dataNode, Node queryNode) {

		// Predecessor Rule
		// For all mapped predecessors of the query node,
		// there must exist corresponding predecessors of target node.
		// Vice Versa
		int count1 = 0;
		int count2 = 0;
		for (Edge edge2 : queryNode.inEdges) {
			Node sourceQuery = edge2.source;
			count2++;
			for (Edge edge1 : dataNode.inEdges) {
				Node sourceData = edge1.source;
				if (this.labelContain(edge2.labels, edge1.labels)
						&& this.labelContain(sourceQuery.labels, sourceData.labels)) {
					count1++;
					break;
				}
			}
		}
		if (count1 != count2) {
			return false;
		}

		// Successsor Rule
		// For all mapped successors of the query node,
		// there must exist corresponding successors of the target node
		// Vice Versa

		count1 = 0;
		count2 = 0;
		for (Edge edge2 : queryNode.outEdges) {
			Node targetQuery = edge2.target;
			count2++;
			for (Edge edge1 : dataNode.outEdges) {
				Node targetData = edge1.target;
				if (this.labelContain(edge2.labels, edge1.labels)
						&& this.labelContain(targetQuery.labels, targetData.labels)) {
					count1++;
					break;
				}
			}

		}
		if (count1 != count2) {
			return false;
		}

		return true;
	}

	private boolean labelContain(ArrayList<Integer> queryedgelabel, ArrayList<Integer> dataedgelabel) {
		boolean flag = false;
		if (queryedgelabel.size() <= dataedgelabel.size()) {
			int count = 0;
			for (int i = 0; i < queryedgelabel.size(); i++) {
				for (int j = 0; j < dataedgelabel.size(); j++) {
					if (queryedgelabel.get(i) == dataedgelabel.get(j)) {
						count++;
						break;
					}
				}
			}
			if (count == queryedgelabel.size()) {
				flag = true;
			}
		}
		return flag;
	}

	public boolean candidateJudge() {
		for (int i = 0; i < query.nodes.size(); i++) {
			if (candidateSet.get(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void parentChildConstruct() {
		neighbourNodes = new HashMap<Integer, ArrayList<Integer>>();
		for (SNode snode : st.nodes) {
			if (!snode.leafNode) {
				for (SEdge sedge : snode.TE) {
					parent[sedge.child.sId] = sedge.parent.sId;
					this.neighbourConsturct(sedge.child.sId, sedge.parent.sId);
				}
			} else {
				for (SEdge sedge : snode.TE) {
					this.neighbourConsturct(sedge.child.sId, sedge.parent.sId);
				}
			}

			if (snode.NTEVerfication) {
				for (SEdge sedge : snode.NTE) {
					this.neighbourConsturct(sedge.child.sId, sedge.parent.sId);
				}
			}
		}

	}

	private void neighbourConsturct(int parentId, int childId) {
		
		if (neighbourNodes.containsKey(parentId)) {
			neighbourNodes.get(parentId).add(childId);
		} else {
			ArrayList<Integer> value = new ArrayList<Integer>();
			value.add(childId);
			neighbourNodes.put(parentId, value);
		}

		if (neighbourNodes.containsKey(childId)) {
			neighbourNodes.get(childId).add(parentId);
		} else {
			ArrayList<Integer> value = new ArrayList<Integer>();
			value.add(parentId);
			neighbourNodes.put(childId, value);
		}
	}

	private void topDownFG_Construct() {

		visited = new HashMap<Integer, Boolean>();

		for (int i = 0; i < st.nodes.size(); i++) {
			visited.put(i, false);
		}

		visited.put(0, true);

		for (int i = 0; i < st.nodes.size(); i++) {
			SNode snode = st.nodes.get(i);
			if (!visited.get(i)) {
				int CNT = 0;
				for (int uDash : neighbourNodes.get(i)) {
					if (visited.get(uDash)) {
						for (int tnode : candidateSet.get(st.nodes.get(uDash).vid)) {
							for (Edge edge : data.nodes.get(tnode).inEdges) {
								Node edgenode = edge.source;
								if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
									if (edgenode.vCnt == CNT) {
										edge.source.vCnt++;
									}
								}
							}
							for (Edge edge : data.nodes.get(tnode).outEdges) {
								Node edgenode = edge.target;
								if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
									if (edgenode.vCnt == CNT) {
										edge.target.vCnt++;
									}
								}
							}
						}
						CNT++;
					}
					
				}

				for (int v : candidateSet.get(snode.vid)) {
					if (data.nodes.get(v).vCnt != CNT) {
						data.nodes.get(v).setQueryNodeArray(snode.vid, 0);
						int count = 0;
						for (int j = 0; j < data.nodes.get(v).queryNodeArray.length; j++) {
							if (data.nodes.get(v).queryNodeArray[j] == 1) {
								count++;
								break;
							}
						}
						if (count == 0) {
							nodeState[v] = 0;
						} else {
							data.nodes.get(v).vCnt = 0;
						}
					} else {
						data.nodes.get(v).vCnt = 0;
					}

				}
				visited.put(snode.sId, true);
			}
		}

	}

	private void bottomUpFG_Refinement() {

		visited = new HashMap<Integer, Boolean>();

		for (int i = 0; i < st.nodes.size(); i++) {
			SNode snode = st.nodes.get(i);
			if (snode.leafNode) {
				visited.put(snode.sId, true);
			} else {
				visited.put(snode.sId, false);
			}

		}

		for (int i = st.nodes.size() - 1; i >= 0; i--) {
			SNode snode = st.nodes.get(i);
			if (!visited.get(i)) {
				int CNT = 0;
				for (int uDash : neighbourNodes.get(i)) {
					if (visited.get(uDash)) {
						for (int tnode : candidateSet.get(st.nodes.get(uDash).vid)) {
							for (Edge edge : data.nodes.get(tnode).inEdges) {
								Node edgenode = edge.source;
								if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
									if (edgenode.vCnt == CNT) {
										edge.source.vCnt++;
									}
								}
							}
							for (Edge edge : data.nodes.get(tnode).outEdges) {
								Node edgenode = edge.target;
								if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
									if (edgenode.vCnt == CNT) {
										edge.target.vCnt++;
									}
								}
							}
						}
						CNT++;
					}
				
				}

				for (int v : candidateSet.get(snode.vid)) {
					if (data.nodes.get(v).vCnt != CNT) {
						data.nodes.get(v).setQueryNodeArray(snode.vid, 0);
						int count = 0;
						for (int j = 0; j < data.nodes.get(v).queryNodeArray.length; j++) {
							if (data.nodes.get(v).queryNodeArray[j] == 1) {
								count++;
								break;
							}
						}
						if (count == 0) {
							nodeState[v] = 0;
						} else {
							data.nodes.get(v).vCnt = 0;
						}
					} else {
						data.nodes.get(v).vCnt = 0;
					}

				}
				visited.put(snode.sId, true);
			}
		}

	}

	public void ConstructionFG() {
		this.FG = new FGraph();
		visited = new HashMap<Integer, Boolean>();

		for (int i = 0; i < st.nodes.size(); i++) {
			int nodeIndex = st.nodes.get(i).sId;
			visited.put(nodeIndex, false);
		}

		visited.put(st.nodes.get(0).sId, true);

		FNode root = new FNode(FG.FNodes.size());
		root.setVID(-1);
		FG.insertFNode(root);

		for (int i = 0; i < nodeState.length; i++) {
			if (nodeState[i] == 1) {
				FNode fnode = new FNode(FG.FNodes.size());
				fnode.setVID(i);
				FG.insertFNode(fnode);
			}
		}

		for (int i = 0; i < st.nodes.size(); i++) {
			SNode snode = st.nodes.get(i);
			ArrayList<Integer> candidate = candidateSet.get(snode.vid);
			if (i == 0) {
				for (int j = 0; j < candidate.size(); j++) {
					if (nodeState[candidate.get(j)] == 1) {
						int id = this.getFGNodeIndex(candidate.get(j));
						FEdge fedge = new FEdge(FG.FNodes.get(0), FG.FNodes.get(id), querynodesize);
						fedge.setLabelArray(0, 1);
						FG.FNodes.get(0).insertOutTE(fedge);
//						FG.FNodes.get(id).insertInTE(fedge);
						FG.insertTE(fedge);
					}
				}
			}
			if (!visited.get(snode.sId)) {
				for (int uDash : neighbourNodes.get(snode.sId)) {
					if (visited.get(uDash)) {
						if (parent[snode.sId] == uDash) {
							for (int tnode : candidateSet.get(st.nodes.get(uDash).vid)) {
								if (nodeState[tnode] == 1) {
									int parentIndex = this.getFGNodeIndex(tnode);
									for (Edge edge : data.nodes.get(tnode).inEdges) {
										Node edgenode = edge.source;
										if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
											int childIndex = this.getFGNodeIndex(edgenode.id);
											int tempTE = this.TEJudge(tnode, edgenode.id);
											if (tempTE == -1) {
												FEdge fedge = new FEdge(FG.FNodes.get(parentIndex),
														FG.FNodes.get(childIndex), querynodesize);
												fedge.setLabelArray(snode.sId, 1);
												FG.FNodes.get(parentIndex).insertOutTE(fedge);
//												FG.FNodes.get(childIndex).insertInTE(fedge);
												FG.TEs.add(fedge);
											} else {
												FEdge fedge = FG.TEs.get(tempTE);
												fedge.setLabelArray(snode.sId, 1);
												FG.FNodes.get(parentIndex).insertOutTE(fedge);
//												FG.FNodes.get(childIndex).insertInTE(fedge);
											}
										}
									}

									for (Edge edge : data.nodes.get(tnode).outEdges) {
										Node edgenode = edge.target;
										if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
											int childIndex = this.getFGNodeIndex(edgenode.id);
											int tempTE = this.TEJudge(tnode, edgenode.id);
											if (tempTE == -1) {
												FEdge fedge = new FEdge(FG.FNodes.get(parentIndex),
														FG.FNodes.get(childIndex), querynodesize);
												fedge.setLabelArray(snode.sId, 1);
												FG.FNodes.get(parentIndex).insertOutTE(fedge);
												FG.FNodes.get(childIndex).insertInTE(fedge);
												FG.TEs.add(fedge);
											} else {
												FEdge fedge = FG.TEs.get(tempTE);
												fedge.setLabelArray(snode.sId, 1);
												FG.FNodes.get(parentIndex).insertOutTE(fedge);
												FG.FNodes.get(childIndex).insertInTE(fedge);
											}
										}
									}
								}
							}

						} else {

							for (int ntnode : candidateSet.get(st.nodes.get(uDash).vid)) {
								if (nodeState[ntnode] == 1) {
									int childIndex = this.getFGNodeIndex(ntnode);
									for (Edge edge : data.nodes.get(ntnode).inEdges) {
										Node edgenode = edge.source;
										if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
											int parentIndex = this.getFGNodeIndex(edgenode.id);
											int tempNTE = this.NTEJudge(ntnode, edgenode.id);
											if (tempNTE == -1) {
												FEdge fedge = new FEdge(FG.FNodes.get(parentIndex),
														FG.FNodes.get(childIndex), querynodesize);
												fedge.setLabelArray(uDash, 1);
												FG.FNodes.get(parentIndex).insertOutNTE(fedge);
												FG.FNodes.get(childIndex).insertInNTE(fedge);
												FG.NTEs.add(fedge);
											} else {
												FEdge fedge = FG.NTEs.get(tempNTE);
												fedge.setLabelArray(uDash, 1);
												FG.FNodes.get(parentIndex).insertOutNTE(fedge);
												FG.FNodes.get(childIndex).insertInNTE(fedge);
											}
										}
									}

									for (Edge edge : data.nodes.get(ntnode).outEdges) {
										Node edgenode = edge.target;
										if (nodeState[edgenode.id] == 1 && edgenode.queryNodeArray[snode.vid] == 1) {
											int parentIndex = this.getFGNodeIndex(edgenode.id);
											int tempNTE = this.NTEJudge(ntnode, edgenode.id);
											if (tempNTE == -1) {
												FEdge fedge = new FEdge(FG.FNodes.get(parentIndex),
														FG.FNodes.get(childIndex), querynodesize);
												fedge.setLabelArray(uDash, 1);
												FG.FNodes.get(parentIndex).insertOutNTE(fedge);
												FG.FNodes.get(childIndex).insertInNTE(fedge);
												FG.NTEs.add(fedge);
											} else {
												FEdge fedge = FG.NTEs.get(tempNTE);
												fedge.setLabelArray(uDash, 1);
												FG.FNodes.get(parentIndex).insertOutNTE(fedge);
												FG.FNodes.get(childIndex).insertInNTE(fedge);
											}
										}
									}
								}
							}
						}
					}
				}
				visited.put(snode.sId, true);
			}
		}
		FG.setNodeState(nodeState);
	}

	private int getFGNodeIndex(int nodeIndex) {
		int count = 0;
		for (int i = 0; i <= nodeIndex; i++) {
			if (nodeState[i] == 1) {
				count++;
			}
		}
		return count;
	}

	private int TEJudge(int parentIndex, int childIndex) {
		int temp = -1;
		for (int i = 0; i < FG.TEs.size(); i++) {
			FEdge fedge = FG.TEs.get(i);
			if (fedge.source.vid == parentIndex && fedge.target.vid == childIndex) {
				temp = i;
				break;
			}
		}
		return temp;
	}
	

	private int NTEJudge(int parentIndex, int childIndex) {
		int temp = -1;
		for (int i = 0; i < FG.NTEs.size(); i++) {
			FEdge fedge = FG.NTEs.get(i);
			if (fedge.source.vid == parentIndex && fedge.target.vid == childIndex) {
				temp = i;
				break;
			}
		}
		return temp;
	}

	public void startFGGenration() {
		this.candidateSet();
		this.parentChildConstruct();
		if (this.candidateJudge()) {
			this.topDownFG_Construct();
			this.bottomUpFG_Refinement();
			this.ConstructionFG();
		}
	}

}
