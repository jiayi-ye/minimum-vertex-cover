/**
 * Created by BRODY on 17/11/12.
 */
import java.util.*;
import java.io.*;


public class branchBound {

    public static class Edge {
        //adjacent number of vertex
        protected int verAdj;
        //point to next edge
        protected Edge link;
        //if exit
        protected boolean exist;
        //initial new Edge class
        Edge(int x) {
            verAdj = x;
            exist = true;
        }
    }

    public static class Vertex {
        //order number of vertex
        protected int verName;
        //point to first adjacent edge
        protected Edge adjacent;
        //if exit
        protected boolean exist;
        //initial new Vertex class
        Vertex(int x){
            verName = x;
            exist = true;
        }
    }

    public static class Graph_List {
        //head vertex of adjacent list
        private Vertex[] head;
        //total number of vertexes in current graph
        @SuppressWarnings("unused")
		private int vertexNum;
        //new graph list
        Graph_List(int x){
            head = new Vertex[x];
            vertexNum = x-1;
        }
    }

    public static class realEdge {
        //one end of edge
        private int source;
        //one end of edge
        private int target;
        //update the small one as source
        realEdge(int x, int y){
            if(x<y){
                source = x;
                target = y;
            }
            else{
                source = y;
                target = x;
            }
        }
    }

    public static ArrayList<Integer> ans;
    public static ArrayList<Integer> res;
    public static int totalVNum;
    public static int totalENum;
    public static int min;
    public static PrintWriter outputTrace;
    public static double startTime;
    public static int cutOffTime;

    public static void main(String filename, String method, int cutoff) throws IOException{
//        if (args.length < 2) {
//            System.err.println("Unexpected number of command line arguments");
//            System.exit(1);
//        }
    	
		
        String graph_file = filename+".graph";
        String algorithmName = method;
        cutOffTime = cutoff;
//        String outputName = filename + "_" + algorithmName + "_"+cutOffTime;
        PrintWriter outputSol;
        File dir = new File("").getCanonicalFile();
		String dataDir = dir.getParent() + "/Data/" + graph_file;
        File outDir = new File("").getCanonicalFile();
		String outD = outDir.getParent() + "/output/" + filename;
		String sol_out = outD + "_" + algorithmName + "_" + cutOffTime;
		String trace_out = outD + "_" + algorithmName + "_" + cutOffTime;

        try {
            outputSol = new PrintWriter(sol_out+".sol", "UTF-8");
            outputTrace = new PrintWriter(trace_out+".trace", "UTF-8");
            
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(dataDir));

            String line = br.readLine();
            String[] split = line.split("\\s+");
            totalVNum = Integer.parseInt(split[0]);
            totalENum = Integer.parseInt(split[1]);
            //store the graph data into adjacent list
            //tested!
            Graph_List G = new Graph_List(totalVNum+1);
            int count = 1;
            while ((line = br.readLine()) != null&&count<totalVNum+1){
                split = line.split("\\s+");
                G.head[count] = new Vertex(count);
                if(split[0].length()==0) {
                    G.head[count].adjacent = null;
                }
                else {
                    G.head[count].adjacent = new Edge(Integer.parseInt(split[0]));
                    Edge curE = G.head[count].adjacent;
                    for (int i = 1; i < split.length; i++) {
                        curE.link = new Edge(Integer.parseInt(split[i]));
                        curE = curE.link;
                    }
                }
                count++;
            }

            ans = new ArrayList<>();
            res = new ArrayList<>();
            min = totalVNum;

            startTime = System.nanoTime();

            expand(G, 0, new HashSet<realEdge>(), new HashSet<Integer>());

            double endTime = System.nanoTime();

            System.out.println("Total time is: "+(endTime-startTime)/1000000000);

            outputSol.println(min);
            for(int i=0;i<res.size();i++){
                if(i==res.size()-1)
                    outputSol.println(res.get(i));
                else outputSol.print(res.get(i)+",");
            }

            outputSol.close();
            outputTrace.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //curVC means current number of vertex cover, covered means the set of edges covered, explored means the nodes explored before
    public static void expand(Graph_List graph, int curVC, HashSet<realEdge> covered, HashSet<Integer> explored){
        if((System.nanoTime()-startTime)/1000000000>=cutOffTime)
            return;
        //prune the branch which was unnecessary
        if(curVC>=min)
            return;

        //if find the solution
        if(covered.size()==totalENum){
            if(curVC<min){
                min = curVC;
                res = new ArrayList<>(ans);
                outputTrace.println((System.nanoTime() - startTime)/1000000000+","+min);
            }
            return;
        }

        //find the node which has a maximum edges currently
        int curNode = 0;
        int degree = 0;

        for(int i=1;i<=totalVNum;i++){
            if(!explored.contains(i)&&graph.head[i].exist){
                int count = 0;
                Edge curE = graph.head[i].adjacent;
                while(curE!=null){
                    if(graph.head[curE.verAdj].exist)
                        count++;
                    curE = curE.link;
                }
                if(count>degree){
                    degree = count;
                    curNode = i;
                }
            }
        }

        //if there is no such node
        if(curNode==0)
            return;

        //store this node into explored set
        explored.add(curNode);

        graph.head[curNode].exist = false;

        if(min>1+curVC+approx(graph)) {
            //use the hashmap to store the current covered vertexes, key is vertex, value is whether is contained before in HashSet
            HashMap<realEdge, Integer> map = new HashMap<realEdge, Integer>();

            Edge curE = graph.head[curNode].adjacent;
            while (curE != null) {
                int temp = curE.verAdj;
                if (graph.head[temp].exist) {
                    realEdge tempEdge = new realEdge(curNode, temp);
                    if (!containEdge(covered, tempEdge))
                        map.put(tempEdge, 1);
                }
                curE = curE.link;
            }
            covered.addAll(map.keySet());
            ans.add(curNode);

            expand(graph, curVC + 1, covered, explored);

            ans.remove(ans.indexOf(curNode));
            //remove the vertexes which are uncovered before
            covered.removeAll(map.keySet());
        }

        graph.head[curNode].exist = true;

        expand(graph, curVC, covered, explored);
    }

    //test whether this edge are contained in hashset
    public static boolean containEdge(HashSet<realEdge> set, realEdge curE){
        for(realEdge i : set){
            if(i.source==curE.source&&i.target==curE.target)
                return true;
        }
        return false;
    }

    //judge if the vertex i is alone, which means no edge connected to it
    public static boolean isAlone(Graph_List graph, int i){
        if(!graph.head[i].exist)
            return true;
        Edge temp = graph.head[i].adjacent;
        int count = 0;
        while(temp!=null){
            if(graph.head[temp.verAdj].exist)
                count++;
            temp = temp.link;
        }
        return count==0;
    }

    //derive the approximation lower bound
    public static int approx(Graph_List graph){
        HashSet<Integer> C = new HashSet<>();
        HashSet<Integer> visited = new HashSet<>();
        int count = 0;
        for(int i=1;i<=totalVNum;i++)
            if(graph.head[i].exist&&!isAlone(graph,i))
                count++;
        while(visited.size()<count){
            for(int i=1;i<=totalVNum;i++){
                if(graph.head[i].exist){
                    //get an arbitrary edge {i,cur}
                    Edge curE = graph.head[i].adjacent;
                    //find the valid edge, which has no endpoints in C
                    while(curE!=null&&!graph.head[curE.verAdj].exist) {
                        curE = curE.link;
                    }
                    if(curE==null) continue;

                    int cur = curE.verAdj;
                    C.add(i);
                    C.add(cur);

                    Edge curE1 = graph.head[cur].adjacent;
                    curE = graph.head[i].adjacent;

                    while(curE!=null){
                        //delete(graph, curE.verAdj, i);
                        visited.add(curE.verAdj);
                        curE = curE.link;
                    }

                    while(curE1!=null){
                        //delete(graph, curE1.verAdj, cur);
                        visited.add(curE1.verAdj);
                        curE1 = curE1.link;
                    }

                    graph.head[i].exist = false;
                    graph.head[cur].exist = false;

                    break;
                }
            }
        }
        for(int i : C){
            graph.head[i].exist = true;
        }
        return C.size()/2;
    }
}
