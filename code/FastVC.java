/**
 * @author Jiaxing Su jsu38
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FastVC {
	public static PrintWriter sol_writer;
	public static PrintWriter trace_writer;
	public static void main(String filename, int cutoff, int seed) throws IOException {
//		File outDir = new File("./output/" + filename);
		File outDir = new File("").getCanonicalFile();
		String outD = outDir.getParent() + "/output/" + filename;
//		System.out.println("output dir: " + outDir);
		// initialize writers
		String method = "LS1";
		String sol_out = outD + "_" + method + "_" + cutoff + "_" + seed + ".sol";
//		System.out.println("sol_out dir: " + sol_out);
		String trace_out = outD + "_" + method + "_" + cutoff + "_" + seed + ".trace";

		sol_writer = new PrintWriter(sol_out);
		trace_writer = new PrintWriter(trace_out);
		HashMap<String,ArrayList<String>> graph = parseGraph(filename);
		Set<String> vc = runFVC(graph, cutoff, seed);
		// write to solution
		Iterator<String> iter = vc.iterator();
		sol_writer.printf("%d%n",vc.size());
		
		while (iter.hasNext()) {
			sol_writer.printf("%s",iter.next());
			if (iter.hasNext()) {
				sol_writer.printf(",");
			}
		}
		sol_writer.close();
		trace_writer.close();
	}
	
	public static HashMap<String,ArrayList<String>> parseGraph(String filename) throws IOException {
		HashMap<String,ArrayList<String>> graph = new HashMap<>();
		String[] size; 
		String[] neighbors;
		// file path
		File dir = new File("").getCanonicalFile();
		String dataDir = dir.getParent() + "/Data/" + filename + ".graph";
		BufferedReader br = new BufferedReader(new FileReader(dataDir));
		// first line
		String line = br.readLine();
		size = line.split(" ");
		// graph info
		int num_vertex = Integer.parseInt(size[0].trim());
		// loop through all the vertices
		int vertex_id = 1;
		while (vertex_id < num_vertex + 1) {
			line = br.readLine();
			neighbors=line.split(" ");
			
			String v_id = String.valueOf(vertex_id);
			ArrayList<String> nei = new ArrayList<>(Arrays.asList(neighbors));
			// put current entry to the HashMap
			graph.put(v_id, nei); 
			vertex_id++; 
		}
		br.close();
		System.out.println("parseGraph done!");
		return graph;
	} 
	
	public static Set<String> runFVC(HashMap<String, 
										   ArrayList<String>> graph, 
										   int cutoff, int seed) {
		
		// 1. parse the edges of the current graph
		ArrayList<Edge> edges = new ArrayList<>();
		
		// loop through all the entries in the graph
		for (Map.Entry<String, ArrayList<String>> entry : graph.entrySet()) {
			String cur_v = entry.getKey();
			ArrayList<String> neighbor_v = entry.getValue();
			for (String cur_n_v : neighbor_v) {
				// some of the lines are empty
				if (!cur_n_v.equals("")) {
					Edge cur_e = new Edge(cur_v, cur_n_v);
					// check if the current edge is already in the list
					if (edges.contains(cur_e)) {
						continue;
					} else {
						edges.add(cur_e);
					}
				}
			}
		}
		
		// start timing
		long start_t = System.currentTimeMillis();
		long elapsed_t_milis = 0;
		float elapsed_t = 0;
		// used for solution trace
		int cur_num_vertex = 0;
		
		// 2. create the initial solution
		// key set is vertex cover, value set is loss 
		Map<String, Integer> C = initVC(edges, graph);
		
		// 3. initialize gain values of v's not in the solution to 0
		Set<String> graph_set = graph.keySet();
		ArrayList<String> gain_set = new ArrayList<>(graph_set);
		Set<String> C_set = C.keySet();
		gain_set.removeAll(C_set);
		
		int num_gain = gain_set.size();
		HashMap<String, Integer> vertex_gain = new HashMap<>(num_gain);
		for (String cover_v : gain_set) {
			vertex_gain.put(cover_v, 0);
		}

		// 4. loop 
		
		// an indicator to know if there is uncovered edges
		Boolean not_cover = false;
		Set<String> C_star_set = new HashSet<String>();
		while (elapsed_t < cutoff) {
			
			// 5. remove vertices from C until there's uncovered edge
			while (not_cover == false) {
				// if there is an edge uncovered, end the loop
				for (Edge e : edges) {
					if (!C.keySet().contains(e.start_v) && 
							!C.keySet().contains(e.end_v)) {
						not_cover = true;
					}
				}
				if (not_cover == false) {
					
					// C* = C.keySet() update the current best C*
					C_star_set = new HashSet<String>();
					C_star_set.addAll(C.keySet());

					elapsed_t_milis = System.currentTimeMillis() - start_t;
					elapsed_t = (float) elapsed_t_milis / 1000;
					
					// write into trace with current best solution
					int cur_len_c_star = C_star_set.size();
					if (cur_len_c_star != cur_num_vertex) {
						cur_num_vertex = cur_len_c_star;
						trace_writer.printf("%.6f, %d%n", elapsed_t, cur_num_vertex);
						System.out.println("elapsed time: " + elapsed_t);
						System.out.println("current num of vertices: " + cur_num_vertex);
					}
					if (elapsed_t > cutoff) {
						break;
					}
					
					// remove a vertex with min loss from C (remove both vertex and loss)
					String min_v = null;
					int min_loss = Integer.MAX_VALUE;
					for (Map.Entry<String, Integer> entry : C.entrySet()) {
						if (entry.getValue() < min_loss) {
							min_v = entry.getKey();
							min_loss = entry.getValue();
						}
					}
					C.remove(min_v);

					// update loss and gain values accordingly
					vertex_gain.put(min_v, 0);
					// get the neighbor v's of min_v
					ArrayList<String> neighbors_min_v = graph.get(min_v);
					for (String n_v : neighbors_min_v) {
						// update loss
						if (C.keySet().contains(n_v)) {
							C.put(n_v, C.get(n_v) + 1);
						} else { // update gain
							vertex_gain.put(n_v, vertex_gain.get(n_v) + 1);
							vertex_gain.put(min_v, vertex_gain.get(min_v) + 1);
						}
					} // end for
				} // end if
			} // end while
			
			// set the indicator back to false for next iteration
			not_cover = false;
			
			// 6. remove randomly selected vertex u

			String u = chooseV(C, 50, seed);

			C.remove(u);
			vertex_gain.put(u, 0);
			// update loss and gain
			ArrayList<String> neighbors_u = graph.get(u);
			for (String n_u : neighbors_u) {
				// update loss
				if (C.keySet().contains(n_u)) {
					C.put(n_u, C.get(n_u) + 1);
				} else { // update gain
					vertex_gain.put(n_u, vertex_gain.get(n_u) + 1);
					vertex_gain.put(u, vertex_gain.get(u) + 1);
				}
			}
			
			// construct the uncovered edges
			List<Edge> u_edges = new ArrayList<>();
			for (Edge e : edges) {
				// both vertices are not in the vertex cover
				if (!C.keySet().contains(e.start_v) && !C.keySet().contains(e.end_v)) {
					u_edges.add(e);
				}
			}
			
			// 7. randomly choose an uncovered edge
			Random rd = new Random();
			Collections.shuffle(u_edges, rd);
			Edge u_e = u_edges.get(0);
			// the vertex with greater gain
			String x = (vertex_gain.get(u_e.start_v) >= vertex_gain.get(u_e.end_v)) ? 
						u_e.start_v : u_e.end_v;
			
			elapsed_t_milis = System.currentTimeMillis() - start_t;
			elapsed_t = (float)elapsed_t_milis / 1000;
			if (elapsed_t > cutoff) {
				break;
			}
			
			C.put(x, 0);
			vertex_gain.remove(x);
			// update loss and gain
			ArrayList<String> neighbors_x = graph.get(x);
			for (String n_x : neighbors_x) {
				// loss update
				if (C.keySet().contains(n_x)) {
					C.put(n_x, C.get(n_x) - 1);
				} else { // update gain
					vertex_gain.put(n_x, vertex_gain.get(n_x) - 1);
					C.put(x, C.get(x) + 1);
				}
			}
		}
		return C_star_set;
	}
	
	// randomly choose the next String to remove
	public static String chooseV(Map<String, Integer> c, int k, int seed) {
		// random seed
		Random rd = new Random();
		// no out of bound
		// best String with best score
		// convert the current vertex cover to a list
		List<String> C_v = new ArrayList<String>(c.keySet());
		// shouldn't shuffle because C_v.size() maybe be smaller than 50
//		Collections.shuffle(C_v, rd);
//		String best_v = C_v.get(0);
//		int best_loss = c.get(best_v);
		int rand_size = C_v.size();
		int best_id = rd.nextInt(rand_size);
		String best_v = C_v.get(best_id);
		int best_loss = c.get(best_v);
		// k random vertices
		// update the best vertex having the least loss
		for (int i = 1; i < k; i++) {
			int r_id = rd.nextInt(rand_size);
			String r_v = C_v.get(r_id);
			int r_loss = c.get(r_v);
			if (r_loss < best_loss) {
				best_v = r_v;
			}
		}
		return best_v;
	}
	
	
	public static Map<String, Integer> initVC(ArrayList<Edge> edges, 
												  HashMap<String, ArrayList<String>> graph) {
		// key set is the vertex cover, value set is the loss values
		Map<String, Integer> cover_map = new ConcurrentHashMap<String, Integer>();
		
		for (Edge cur_e : edges) {
			// if the end points of current edge is not in the cover set
			Set<String> keyset = cover_map.keySet();
			if (!keyset.contains(cur_e.start_v) && !keyset.contains(cur_e.end_v)) {
				ArrayList<String> neighbor_start = graph.get(cur_e.start_v);
				ArrayList<String> neighbor_end = graph.get(cur_e.end_v);
				int start_n_len = neighbor_start.size();
				int end_n_len   = neighbor_end.size();
				// put the vertex with larger degree in the set and 
				// initialize the loss value to 0
				String cur_v = (start_n_len > end_n_len) ? cur_e.start_v : cur_e.end_v;
				cover_map.put(cur_v, 0);
			}
		}
		// calculate the loss of each String
		// defined as: loss(v) number of covered edges that would become 
		// uncovered by removing v from cover set
		
		Set<String> keyset = cover_map.keySet();
		for (Edge cur_e : edges) {
			String cur_start_v = cur_e.start_v;
			String cur_end_v   = cur_e.end_v;
			if ((keyset.contains(cur_start_v)) && (!keyset.contains(cur_end_v))) {
				cover_map.put(cur_start_v, cover_map.get(cur_start_v) + 1);
			}
			if ((keyset.contains(cur_end_v)) && (!keyset.contains(cur_start_v))) {
				cover_map.put(cur_end_v, cover_map.get(cur_end_v) + 1);
			}
		}
		// remove floating vertices
		for (Map.Entry<String, Integer> entry : cover_map.entrySet()) {
			String cur_v = entry.getKey();
			int cur_cover_num = entry.getValue();
			// remove the entry in the cover map having zero covered edges
			if (cur_cover_num == 0) {
				cover_map.remove(cur_v);
				// for karate.graph, vertex 24 is removed
				// add one coverage on vertices connecting with the removed String
				ArrayList<String> neighbor_v = graph.get(cur_v);
				for (String n_v : neighbor_v) {
					cover_map.put(n_v,  cover_map.get(n_v) + 1);
				}
			}
		}
		return cover_map;
	}
}
