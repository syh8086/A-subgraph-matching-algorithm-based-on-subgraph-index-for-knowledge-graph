package FGRunner;

import java.io.PrintWriter;
import java.util.ArrayList;

import FGQTIndex.FEdge;
import FGQTIndex.FGraph;
import FGQTIndex.FNode;
import SpanningTree.SEdge;
import SpanningTree.SNode;
import SpanningTree.STree;

public class FGState {
	
	public int[] core_1; 
	public int[] core_2; 	
	public int[] parent;
    public int depth = 0; // current depth of the search tree	
	public boolean matched = false;
	public ArrayList<Integer> initialCandidateSet;
	public ArrayList<Integer> localCandidateSet;
	public int[] nodeState;
	public int MCS = 0;
	
	public FGraph fg;
	public STree st;
	
	public FGState(FGraph fg, STree st) {
		this.fg = fg;
		this.st = st;
		this.nodeState = fg.nodeState;
		
		int fgSize = fg.FNodes.size();
		int stSize = st.nodes.size();
		core_1 = new int[fgSize];
		core_2 = new int[stSize];
		parent = new int[stSize];
		
		for(int i = 0; i < fgSize; i++) {
			core_1[i] = -1;
		}
		
		for(int i = 0; i < stSize; i++) {
			core_2[i] = -1;
			parent[i] = -1;
		}
		
		for(SNode snode: st.nodes) {
			for(SEdge sedge : snode.TE) {
				parent[sedge.child.sId] = sedge.parent.sId;
			}
		}
		if(depth == 0) {
			FNode root  = this.fg.FNodes.get(0);
			initialCandidateSet = new ArrayList<Integer>();
			for(FEdge fedge : root.outTE) {
				if(nodeState[fedge.target.vid] == 1 && fedge.labelArray[depth] == 1) {
					if(!initialCandidateSet.contains(fedge.target.fid)) {
						initialCandidateSet.add(fedge.target.fid);
					}
				}
			}
			MCS = MCS + initialCandidateSet.size();
		}
		
	}
	
	
	/**
	 * Add a new match (targetIndex, queryIndex) to the state
	 * @param targetIndex	Index of the node in target graph
	 * @param queryIndex	Index of the node in query graph
	 */
	public void extendMatch(int dataIndex, int queryIndex) {
		
		core_1[dataIndex] = queryIndex;
		core_2[queryIndex] = dataIndex;
				
		depth++;	// move down one level in the search tree

		if (depth < st.nodes.size()) {
			if (queryIndex == 0) {
				FNode dataNode = fg.FNodes.get(dataIndex);
				localCandidateSet = new ArrayList<Integer>();
				for (FEdge fedge : dataNode.outTE) {
					if (nodeState[fedge.target.vid] == 1 && fedge.labelArray[depth] == 1){
						if (!localCandidateSet.contains(fedge.target.fid)) {
							localCandidateSet.add(fedge.target.fid);
						}
					}
				}
				MCS = MCS + localCandidateSet.size();
			} else {
				int parentIndex = core_2[parent[depth]];
				FNode dataNode = fg.FNodes.get(parentIndex);
				localCandidateSet = new ArrayList<Integer>();
				for (FEdge fedge : dataNode.outTE) {
					if ( nodeState[fedge.target.vid] == 1 && fedge.labelArray[depth] == 1) {
						if (!localCandidateSet.contains(fedge.target.fid)) {
							localCandidateSet.add(fedge.target.fid);
						}
					}
				}
				MCS = MCS + localCandidateSet.size();
			}
		}
			
	}
	
	/**
	 * Remove the match of (targetNodeIndex, queryNodeIndex) for backtrack
	 * @param targetNodeIndex
	 * @param queryNodeIndex
	 */
	public void backtrack(int targetNodeIndex, int queryNodeIndex) {
		
		core_1[targetNodeIndex] = -1;
		core_2[queryNodeIndex] = -1;
		
		depth --;
	}
	
	/**
	 * Print the current mapping
	 */
	public void printMapping() {
		for (int i = 0 ; i < core_2.length ; i++) {
			System.out.print("(" + core_2[i] + "-" + i + ") ");
		}
		System.out.println();
	}
	
	/**
	 * Write state to file
	 */
	public void writeMapping(PrintWriter writer){
		for (int i = 0 ; i < core_2.length ; i++) {
			writer.write("(" + core_2[i] + "-" + i + ") ");
		}
		writer.write("\n");
	}

}
