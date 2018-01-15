
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Solution {

//	@SuppressWarnings("resource")
	public static void main(String filename, String method, int cutoff, int rdseed) throws IOException {
		// TODO Auto-generated method stub
//		if(args.length < 4){
//			System.err.println("Unexpected number of command line arguments");
//			System.exit(1);
//		}
		String fileName = filename;
		String algorithmName = method;
		int cutOffTime = cutoff;
		int randomSeed = rdseed;
//		String outputName = fileName+"_"+algorithmName+"_"+cutOffTime;
		File dir = new File("").getCanonicalFile();
		String dataDir = dir.getParent() + "/Data/" + fileName + ".graph";
		File outDir = new File("").getCanonicalFile();
		String outD = outDir.getParent() + "/output/" + fileName;
		String sol_out = outD + "_" + algorithmName + "_" + cutOffTime + "_" + rdseed;
		String trace_out = outD + "_" + algorithmName + "_" + cutOffTime + "_" + rdseed;
		BufferedReader file1 = new BufferedReader(new FileReader(dataDir));
		PrintWriter outputSol = new PrintWriter(sol_out +".sol", "UTF-8");
		PrintWriter outputTrace = new PrintWriter(trace_out+ ".trace", "UTF-8");
		Random random = new Random(randomSeed);
		
		//====================================main function====================================
		String line = file1.readLine().trim();
		String[] firstLine = line.split(" ");
		// find number of vertex and edges
		int numOfVertex = Integer.parseInt(firstLine[0]);
		int numOfEdge = Integer.parseInt(firstLine[1]);
		ArrayList<int[]> map = new ArrayList<>();
		map.add(new int[numOfVertex]);
		map.add(new int[numOfVertex]);
		//======================construct the graph============================ 
		Graph graph = new Graph();
		ArrayList<int[]> edgeList = new ArrayList<>();
		int curLine = 1;
		while(curLine<=numOfVertex){
			line = file1.readLine().trim();
			if(line.equals("")){
				curLine++;
				continue;
			}
			String[] tmpLine = line.split(" ");
			ArrayList<Integer> tmpNeighbor = new ArrayList<>();
			for (int i = 0; i < tmpLine.length; i ++){
				int neighbor = Integer.parseInt(tmpLine[i]);		
				tmpNeighbor.add(neighbor);
				edgeList.add(new int[]{curLine, neighbor});
			}
			map.get(0)[curLine-1]+=tmpNeighbor.size();
			graph.map.put(curLine, tmpNeighbor);
			curLine++;
		}
		//========================iterate the algorithm for 5 times=========================================
		StringBuilder sb = new StringBuilder();
		int minSize = Integer.MAX_VALUE;
		double systemStartingTime = System.nanoTime();
		double systemEndingTime = System.nanoTime();
		while((systemEndingTime-systemStartingTime)/1000000000<cutOffTime){
			//============================copy the map=======================================
			for(int i = 0;i < numOfVertex; i ++){
				map.get(1)[i] = map.get(0)[i];
			}
			//============================run approximation algorithm=======================================
			double startTime = System.nanoTime();
			ArrayList<Integer> res = approximationAlgorithm(graph, numOfVertex, numOfEdge, random, edgeList, map.get(1));
			double endTime = System.nanoTime();
			systemEndingTime = System.nanoTime();
			//============================print output file=================================================
			double totalTime = (endTime - startTime) / 1000000000;
			int size = res.size();
			if(size < minSize){
				sb = new StringBuilder();
				minSize = size;
				for(int vertex : res){
					sb.append(vertex);
					sb.append(',');
				}
				sb.delete(sb.length()-1, sb.length());
				outputTrace.println(totalTime + ", " + size);
			}
		}
		outputSol.println(minSize);
		outputSol.println(sb.toString());
		outputTrace.close();
		outputSol.close();
		file1.close();
		//===============================================================================================
	}
	static public ArrayList<Integer> approximationAlgorithm(Graph graph, int numOfVertex, int n, Random random, ArrayList<int[]> edgeList, int[] map){
		HashSet<Integer> set = new HashSet<>();
		ArrayList<Integer> res = new ArrayList<>();
		int numOfEdge = n;
		//================iterate the edge deletion (ED) algorithm to find the cover set=================
		while(numOfEdge > 0){
			int randomEdge = random.nextInt(n*2);
			// if the cover already has both ends of random edge or the vertex won't covers any more edge,
			// re-do the randomization 
			if((set.contains(edgeList.get(randomEdge)[0])||map[edgeList.get(randomEdge)[0]-1]==0) && (set.contains(edgeList.get(randomEdge)[1])||map[edgeList.get(randomEdge)[1]-1] == 0)){
				continue;
			}
			//===========================================================================================
			//check how many more identical edge are going to be covered by V1 and V2 
			int randomStartingVertex1 = edgeList.get(randomEdge)[0];
			int randomStartingVertex2 = edgeList.get(randomEdge)[1];
			if(!set.contains(randomStartingVertex1)){
				for(int vertex : graph.map.get(randomStartingVertex1)){
					if(!set.contains(vertex)){
						numOfEdge--;
						map[vertex-1]--;
					}
				}
				set.add(randomStartingVertex1);
				res.add(randomStartingVertex1);
			}
			if(!set.contains(randomStartingVertex2)){
				for(int vertex : graph.map.get(randomStartingVertex2)){
					if(!set.contains(vertex)){
						numOfEdge--;
						map[vertex-1]--;
					}
				}
				set.add(randomStartingVertex2);
				res.add(randomStartingVertex2);
			}
		}
		// return the cover 
		return res;
		
	}

}
