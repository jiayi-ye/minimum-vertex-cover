
public class RunAll {

	public static void main(String[] args) throws Exception {
		String filename, method;
		int cutoff, seed;

		filename = "str";
		method = "str";
		cutoff = 0;
		seed = 0;
		
		for (int i = 0; i < 8; i++) {
			if (args[i].equals("-inst")) {
				filename = args[i+1].substring(0, args[i+1].length()-6);
				System.out.println("filename: "+filename);
			}else if (args[i].equals("-alg")) {
				method = args[i+1].substring(0, args[i+1].length()); 
				System.out.println("method: "+method);
			} else if(args[i].equals("-time")){
				cutoff = Integer.parseInt(args[i+1].substring(0, args[i+1].length()));
				System.out.println("cutoff: "+cutoff);
			} else if(args[i].equals("-seed")) {
				seed = Integer.parseInt(args[i+1].substring(0, args[i+1].length()));
				System.out.println("seed: "+seed);
			}
		}
			
		if (method.equals("LS1")){
			FastVC.main(filename, cutoff, seed);
		} else if (method.equals("LS2")){
			SA.main(filename, cutoff, seed);
		} else if (method.equals("Approx")){
			Solution.main(filename, method, cutoff, seed);
		} else if (method.equals("BnB")){
			branchBound.main(filename, method, cutoff);
		}
		
	}
}
