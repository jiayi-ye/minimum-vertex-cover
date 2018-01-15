
public class Edge {
	// constructor of the Edge object
	public String start_v, end_v;
	public Edge(String start_v, String end_v) {
		this.start_v = start_v;
		this.end_v = end_v;
	}
	
	// print edges
	@Override
	public String toString() {
		return String.format("start vertex: " + start_v + ", " + "end vertex: " + end_v);
	}
	
	// compare two edges, if they have the same vertices, they are the same
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		// type check
		if (!(o instanceof Edge)) {
            return false;
        }
        // type cast o to Edge
		Edge e = (Edge) o;
		
		if ((this.start_v.equals(e.start_v) && this.end_v.equals(e.end_v)) ||
			(this.start_v.equals(e.end_v) && this.end_v.equals(e.start_v))) {
			return true;
		}
		return false;
	}
}
