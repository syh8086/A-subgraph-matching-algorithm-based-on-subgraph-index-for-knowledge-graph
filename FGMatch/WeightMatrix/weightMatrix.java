package WeightMatrix;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import MGraph.Edge;
import MGraph.Graph;
import MGraph.Node;

public class weightMatrix {
	
	public countMatrix cm; // the count matrix
	public Graph query; // the query graph
	public HashMap<Integer, Integer> edgelabel; // the hash map of unique edge label 
	public double[][] weightmatrix; // the computed weight matrix of query graph
	public weightMatrix(countMatrix cm) {
		this.cm = cm;
		this.query = cm.query;
		this.edgelabel = cm.edgelabel;
		this.weightmatrix();
	}
	
	// compute the probability Matrix of query graph;
	
	private double[][] probMatrix(){
		int[] countSum = new int[cm.edgelabel.size()];
		for(int i = 0; i < cm.edgelabel.size(); i++) {
			countSum[i] = 0;
		}
		double[][] probMatrix = new double[query.nodes.size()][edgelabel.size()];
		int[][] countMatrix = cm.countWeightMatrix;
		for(int i = 0; i < cm.query.nodes.size(); i++) {
			for(int j = 0; j < cm.edgelabel.size(); j++) {				
				countSum[j] = countSum[j] + countMatrix[i][j];
			}
		}
		
		for(int i = 0; i < query.nodes.size(); i++) {
			for(int j = 0; j < edgelabel.size(); j++) {
				probMatrix[i][j] = new BigDecimal((float)countMatrix[i][j]/countSum[j]).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
			}
		}
		return probMatrix;
	}
	
	// compute the entropy Matrix of query graph;
	
	private double[][] entropyMatrix(){
		double[][] entropyMatrix = new double[query.nodes.size()][edgelabel.size()];
		double[][] probMatrix = this.probMatrix();
		double[] entroyLabel = new double[edgelabel.size()];
		for(int i = 0; i < edgelabel.size(); i++) {
			entroyLabel[i] = 10;
		}
		for(int i = 0; i < query.nodes.size(); i++) {
			for(int j = 0; j < edgelabel.size(); j++) {
				if(probMatrix[i][j] != 0) {
				entroyLabel[j] = entroyLabel[j] + probMatrix[i][j] * Math.log10(probMatrix[i][j]);
				}
			}
		}
		
		
		double sum= 0;
		for(int i = 0; i < edgelabel.size(); i++) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			sum = sum + entroyLabel[i];
		}
		
		for(int i = 0; i < query.nodes.size(); i++) {
			for(int j = 0; j < edgelabel.size(); j++) {
				entropyMatrix[i][j] = probMatrix[i][j] * entroyLabel[j]/sum;
			}
		}
		
		return entropyMatrix;
	}
	
	// compute the weight matrix of query graph;
	
	public void weightmatrix(){
		double[][] entropyMatrix = this.entropyMatrix();
		weightmatrix = new double[query.nodes.size()][query.nodes.size()];
		double[] vertexWeight = new double[query.nodes.size()];
		for(int i = 0; i < query.nodes.size(); i++) {
			vertexWeight[i] = 0;
		}
		for(int i = 0; i < query.nodes.size(); i++) {
			for(int j = 0; j < edgelabel.size(); j++) {
				vertexWeight[i] = vertexWeight[i] + entropyMatrix[i][j];
			}
		}
		
		
		for(int i = 0; i < query.nodes.size(); i++) {
			for(int j = 0; j < query.nodes.size(); j++) {
				if(i != j) {
					weightmatrix[i][j] = 0;
				}
				if(i == j) {
					weightmatrix[i][i] = vertexWeight[i];
				}
			}
		}
		
		for(int i = 0; i < query.nodes.size(); i++) {
			Node node= query.nodes.get(i);
			for(int j = 0; j < node.inEdges.size(); j++) {
				Edge inEdge = node.inEdges.get(j);
				if(i != j) {
					for (int r = 0; r < inEdge.labels.size(); r++) {
						weightmatrix[i][inEdge.source.id] = weightmatrix[i][inEdge.source.id]
								+ entropyMatrix[i][cm.edgelabel.get(inEdge.labels.get(r))];
					}
				}
			}
			
			for(int j = 0; j < node.outEdges.size(); j++) {
				Edge outEdge = node.outEdges.get(j);
				if(i != j) {
					for (int r = 0; r < outEdge.labels.size(); r++) {
						weightmatrix[i][outEdge.target.id] = weightmatrix[i][outEdge.target.id]
								+ entropyMatrix[i][cm.edgelabel.get(outEdge.labels.get(r))];
					}
				}
			}
		}
	}
	
}
