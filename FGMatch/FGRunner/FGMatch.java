package FGRunner;

import java.util.ArrayList;

import FGQTIndex.FEdge;
import FGQTIndex.FGraph;
import FGQTIndex.FNode;
import SpanningTree.SEdge;
import SpanningTree.SNode;
import SpanningTree.STree;
import VF2Core.Pair;

public class FGMatch {
	
//	public ArrayList<int[]> stateSet;
	public int resultCount = 0;
	public int MCS = 0;
	
	public FGMatch() {
		
	}
	
	
	/**
	 * Find matches given a query graph and a set of target graphs
	 * @param graphSet		Target graph set
	 * @param queryGraph	Query graph
	 * @return				The state set containing the mappings
	 */
	public int matchGraphSetWithQuery(FGraph fg, STree st){		
		FGState state = new FGState(fg, st);
//		stateSet = new ArrayList<int[]>();
		matchRecursive(state, fg, st);
		MCS = state.MCS;
				
		return resultCount;
	}
	
	/**
	 * Recursively figure out if the target graph contains query graph
	 * @param state			VF2 State
	 * @param targetGraph	Big Graph
	 * @param queryGraph	Small Graph
	 * @return	Match or not
	 */
	private boolean matchRecursive(FGState state, FGraph fg, STree st){
		
		if (state.depth == st.nodes.size()){	// Found a match
			state.matched = true;
//			state.printMapping();
			
//			int[] result = new int[state.core_2.length];
//			for(int i = 0; i < state.core_2.length; i++) {
//				result[i] = state.core_2[i];
//			}
//			stateSet.add(result);
			resultCount ++;
//			if(resultCount > 10000) {
//				return true;
//			}
			return false;
		} else {	// Extend the state
			ArrayList<Pair<Integer,Integer>> candidatePairs = genCandidatePairs(state, fg, st);
			for (Pair<Integer, Integer> entry : candidatePairs){
				if (checkFeasibility(state, entry.getKey(), entry.getValue())){
					state.extendMatch(entry.getKey(), entry.getValue()); // extend mapping
					if (matchRecursive(state, fg, st)){	// Found a match
						return true;
					}
					state.backtrack(entry.getKey(), entry.getValue()); // remove the match added before
				}
			}
		}
		return false;	
	}
	
	/**
	 * Generate all candidate pairs given current state
	 * @param state			FG State
	 * @param targetGraph	Big Graph
	 * @param queryGraph	Small Graph
	 * @return				Candidate Pairs
	 */
	private ArrayList<Pair<Integer, Integer>> genCandidatePairs(FGState state, FGraph fg, STree st) {
		ArrayList<Pair<Integer, Integer>> pairList = new ArrayList<Pair<Integer, Integer>>();
		if (state.depth == 0) {
			ArrayList<Integer> initialCandidateSet = state.initialCandidateSet;
			for (int i = 0; i < initialCandidateSet.size(); i++) {
				pairList.add(new Pair<Integer, Integer>(initialCandidateSet.get(i), state.depth));
			}
		} else {
			ArrayList<Integer> localCandidateSet = state.localCandidateSet;
			for (int i = 0; i < localCandidateSet.size(); i++) {
				pairList.add(new Pair<Integer, Integer>(localCandidateSet.get(i), state.depth));
			}

		}
		return pairList;
	}
	
	/**
	 * Check the feasibility of adding this match
	 * @param state				VF2 State
	 * @param targetNodeIndex	Target Graph Node Index
	 * @param queryNodeIndex	Query Graph Node Index
	 * @return					Feasible or not
	 */
	private Boolean checkFeasibility(FGState state, int dataIndex, int queryIndex) {
		// Node Label Rule
		// The two nodes must have the same label
		FNode datanode = state.fg.FNodes.get(dataIndex);
		SNode querynode = state.st.nodes.get(queryIndex);
		
		for(int i = 0; i < state.core_2.length; i++) {
			if(state.core_2[i] == dataIndex) {
				return false;
			}
		}
		
		if(querynode.NTEVerfication) {
			int countData = 0;
			int countQuery = 0;
			for (SEdge sedge : querynode.NTE) {
				int nodeIndex = sedge.child.sId;
				if (state.core_2[nodeIndex] > -1) {
					countQuery++;
					for (FEdge fedge : datanode.outNTE) {
						if (state.core_1[fedge.target.fid] == nodeIndex) {
							countData++;
							break;
						}
					}
					for (FEdge fedge : datanode.inNTE) {
						if (state.core_1[fedge.source.fid] == nodeIndex) {
							countData++;
							break;
						}
					}
				}
			}
			if(countData < countQuery) {
				return false;
			}
		} 
		
		return true;		
	}

}
