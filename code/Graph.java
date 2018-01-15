
import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
	public HashMap<Integer, ArrayList<Integer>> map;
	public Graph(){
		this.map = new HashMap<>();
	}
	public Graph(Graph anotherGraph){
		this.map = anotherGraph.map;
	}
}
