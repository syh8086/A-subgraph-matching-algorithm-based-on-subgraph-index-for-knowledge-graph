package FGRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import FGQTIndex.ConsFG;
import MGraph.Edge;
import MGraph.Graph;
import MGraph.Node;
import SpanningTree.SConstruction;
import VF2Runner.DataGraph;
import WeightMatrix.countMatrix;
import WeightMatrix.weightMatrix;

public class FGRun {

	private Path queryPath;
	private Path outPath;

	private Graph query;
	private Graph data;

	public FGRun() {
	}

	public void run() throws IOException {
//		queryPath = Paths.get("D:\\dataStore", "queryGraphs.ttl");
//		String dataPath = "D:/dataStore/dataGraphs.ttl";
		queryPath = Paths.get("D:\\workspace\\FGQTMatch\\dataStore", "queryCliqueGraphs.ttl");
		String dataPath = "D:/workspace/FGQTMatch/dataStore/dataCliqueGraphs.ttl";
		DataGraph datagraph = new DataGraph(dataPath);
		data = datagraph.getDataGraph();
		this.loadQueryGraphSetFromFile(queryPath, "query");
	}
	
	

	/**
	 * Load graph set from file
	 * 
	 * @param inpath     Input path
	 * @param namePrefix The prefix of the names of graphs
	 * @return Graph Set
	 * @throws IOException
	 */
	private void loadQueryGraphSetFromFile(Path inpath, String namePrefix) throws IOException {
		Scanner scanner = new Scanner(inpath.toFile());
		query = null;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.equals("")) {
				continue;
			} else if (line.startsWith("t")) {
				String graphId = line.split(" ")[2];
				if (query != null) {

					String outFileName = new String("resFG" + query.name);
					outPath = Paths.get("D:\\dataStore", outFileName);
					this.matching(data, query, outPath);

					query = null;
				}
				query = new Graph(namePrefix + graphId);
			} else if (line.startsWith("v")) {
				String[] lineSplit = line.split(" ");
				int nodeId = Integer.parseInt(lineSplit[1]);
				int nodeLabel = Integer.parseInt(lineSplit[2]);
				Node node = new Node(query.nodes.size());
				node.setRd(nodeId);
				node.insertLabel(nodeLabel);
				query.addNode(node);
			} else if (line.startsWith("e")) {
				String[] lineSplit = line.split(" ");
				int sourceId = Integer.parseInt(lineSplit[1]);
				int targetId = Integer.parseInt(lineSplit[2]);
				int edgeLabel = Integer.parseInt(lineSplit[3]);
				int edgeLabelCount = Integer.parseInt(lineSplit[4]);
				Node source = query.nodes.get(this.getQueryId(sourceId));
				Node target = query.nodes.get(this.getQueryId(targetId));
				Edge edge = new Edge(source, target);
				edge.edgeLabelCount = edgeLabelCount;
				edge.insertLabel(edgeLabel);
				source.insertOutEdges(edge);
				target.insertInEdges(edge);
				query.addEdge(edge);
			}
		}
		scanner.close();
	}

	private int getQueryId(int vertex) {
		int id = -1;
		for (int i = 0; i < query.nodes.size(); i++) {
			if (query.nodes.get(i).rd == vertex) {
				id = i;
				break;
			}
		}
		return id;
	}

	private void matching(Graph data, Graph query, Path outPath) throws IOException {
		long startMilli = System.currentTimeMillis();

		PrintWriter writer = new PrintWriter(outPath.toFile());
		FGMatch fgm = new FGMatch();
		System.out.println("Loading Done!");
		startMilli = System.currentTimeMillis();
		System.out.println();

		countMatrix cm = new countMatrix(query);
		weightMatrix wm = new weightMatrix(cm);
		SConstruction sc = new SConstruction(wm);
		sc.insertNonTreeEdge();
		ConsFG fc = new ConsFG(data, sc);
		fc.startFGGenration();

		printConstructionTime(startMilli);
		long currentMili = System.currentTimeMillis();
		writer.write(((currentMili - startMilli)) + " milliseconds elapsed" + "\n");
		writer.flush();
		if (fc.candidateJudge()) {
			int resultCount = 0;
			resultCount = fgm.matchGraphSetWithQuery(fc.FG, sc.st);
			if (resultCount == 0) {
				System.out.println();
				System.out.println("Cannot find a map for: " + query.name);
				printTotalMatchingTime(startMilli);
				System.out.println();

				writer.write("Cannot find a map for: " + query.name + "\n\n");
				writer.flush();
			} else {
				System.out.println(fc.FG.FNodes.size() + " " + fc.FG.TEs.size() + " " + fc.FG.NTEs.size());
				System.out.println("Found " + resultCount + " maps for: " + query.name);
				printTotalMatchingTime(startMilli);
				System.out.println();
				writer.write(fc.FG.FNodes.size() + " " + fc.FG.TEs.size() + " " + fc.FG.NTEs.size() + "\n");
				writer.write("Maps for: " + query.name + "\n");
				currentMili = System.currentTimeMillis();
				writer.write(((currentMili - startMilli)) + " milliseconds elapsed" + "\n");
//			for (int[] state : stateSet){
//				writer.write("In: " + "FlowGraph" + "\n");
//				// state.printMapping();
//				for (int i = 0 ; i < state.length ; i++) {
//					writer.write("(" + state[i] + "-" + i + ") ");
//				}
//			}		
				writer.flush();
			}
		} else {
			System.out.println("Cannot find a map for: " + query.name);
			printTotalMatchingTime(startMilli);
			System.out.println();

			writer.write("Cannot find a map for: " + query.name + "\n\n");
			writer.flush();
		}
	}

	private void printConstructionTime(long startMilli) {
		long currentMili = System.currentTimeMillis();
		System.out.println(((currentMili - startMilli)) + " milliseconds elapsed");
	}

	private void printTotalMatchingTime(long startMilli) {
		long currentMili = System.currentTimeMillis();
		System.out.println(((currentMili - startMilli)) + " milliseconds per graph in average.");
	}

}
