package WeightMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MGraph.Edge;
import MGraph.Graph;
import MGraph.Node;

public class countMatrix {

	public Graph query;
	public HashMap<Integer, Integer> edgelabel;
	private int[][] countMatrix;
	public int[][] countWeightMatrix;

	public countMatrix(Graph query) throws IOException {
		this.query = query;
		this.initalCountMatrix();
	}

	public void initalCountMatrix() {

		// Count the unique edge labels of each query vertex

		edgelabel = new HashMap<Integer, Integer>();
		int count = 0;
		for (int i = 0; i < query.edges.size(); i++) {
			ArrayList<Integer> label = query.edges.get(i).labels;
			for (int j = 0; j < label.size(); j++) {
				if (!edgelabel.containsKey(label.get(j))) {
					edgelabel.put(label.get(j), count);
					count++;
				}
			}
		}

		// construct the initial count matrix

		countMatrix = new int[query.nodes.size()][edgelabel.size()];
		countWeightMatrix = new int[query.nodes.size()][edgelabel.size()];
		for (int i = 0; i < query.nodes.size(); i++) {
			for (int j = 0; j < edgelabel.size(); j++) {
				countMatrix[i][j] = 0;
				countWeightMatrix[i][j] = 0;
			}
		}

		// assign the label count to count Matrix

		for (int i = 0; i < query.nodes.size(); i++) {
			Node node = query.nodes.get(i);
			for (int j = 0; j < node.inEdges.size(); j++) {
				ArrayList<Integer> labels = node.inEdges.get(j).labels;
				for (int r = 0; r < labels.size(); r++) {
					countWeightMatrix[i][edgelabel
							.get(labels.get(r))] = countWeightMatrix[i][edgelabel.get(labels.get(r))]
									+ node.inEdges.get(j).edgeLabelCount;
				}
			}
			for (int j = 0; j < node.outEdges.size(); j++) {
				ArrayList<Integer> labels = node.outEdges.get(j).labels;
				for (int r = 0; r < labels.size(); r++) {
					countWeightMatrix[i][edgelabel
							.get(labels.get(r))] = countWeightMatrix[i][edgelabel.get(labels.get(r))]
									+ node.outEdges.get(j).edgeLabelCount;
				}
			}
		}
	}
}
