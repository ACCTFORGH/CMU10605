import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class ApproxPageRank {
	private static Map<String, Double> p;
	private static Map<String, Double> r;
	private static Map<String, String[]> cachedEdges;

	private static String inputPath;
	private static String seed;
	private static double alpha;
	private static double epsilon;
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Incorrect number of arguments!");
			System.exit(-1);
		}
		
		inputPath = args[0];
		seed = args[1];
		alpha = Double.parseDouble(args[2]);
		epsilon = Double.parseDouble(args[3]);
		p = new HashMap<String, Double>();
		r = new HashMap<String, Double>();
		cachedEdges = new HashMap<String, String[]>();
		
		pageRank();
		
		int size = p.size();
		Pair[] pairs = new Pair[size];
		int i = 0;
		for (Map.Entry<String, Double> entry: p.entrySet()) {
			Pair pair = new Pair(entry.getKey(), entry.getValue());
			pairs[i++] = pair;
		}
		
		int num = sweep(pairs, size);
	
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		for (i = 0; i < num; ++i) {
			bw.write(pairs[i].phrase + "\t" + pairs[i].score);
			bw.newLine();
		}
		bw.close();
	}
	
	public static void pageRank() throws IOException {
		boolean converged = false;
		
		// initial seed
		r.put(seed, (double) 1);

		while (!converged) {
			converged = scanFile();
			repeatedPush();
		}
	}
	
	private static boolean scanFile() throws IOException {
		boolean converged = true;
		BufferedReader br = new BufferedReader(new FileReader(inputPath));
		String line;
		while ((line = br.readLine()) != null) {
			int tabPos = line.indexOf("\t");
			String u = line.substring(0, tabPos);
			String[] v;
			// check cache first
			if (cachedEdges.containsKey(u)) {
				v = cachedEdges.get(u);
			} else {
				v = line.substring(tabPos + 1).split("\t");
			}
				
			double ru = r.containsKey(u)? r.get(u): 0;
			int d = v.length;
			if (ru / d > epsilon) {
				converged = false;
				push(u, v, ru, d);
				cachedEdges.put(u, v);
			}
		}
		br.close();
		return converged;
	}
	
	private static void repeatedPush() {
		boolean converged = false;
		while (!converged) {
			converged = true;
			for (Map.Entry<String, Double> entry: p.entrySet()) {
				String u = entry.getKey();
				String[] v = cachedEdges.get(u);
				double ru = r.get(u);
				int d = v.length;
				if (ru / d > epsilon) {
					converged = false;
					push(u, v, ru, d);
				}
			}
		}
	}
	
	private static void push(String u, String[] v, double ru, int d) {
		p.put(u, alpha * ru + (p.containsKey(u)? p.get(u): 0));
		
		double tmp = (1 - alpha) * ru * 0.5;
		// update ru
		r.put(u, tmp);
	
		// update rv
		tmp /= d;
		for (int i = 0; i < d; ++i) {
			double val = r.containsKey(v[i])? r.get(v[i]): 0;
			r.put(v[i], val + tmp);
		}
	}
	
	public static int sweep(Pair[] pairs, int size) {
		double minScore = Double.MAX_VALUE;
		int num = 0;
		double volume = 0;
		
		Set<String> S = new HashSet<String>();
		for (int i = 0; i < size; ++i) {
			String phrase = pairs[i].phrase;
			S.add(phrase);
			int boundary = 0;
			
			// compute volume
			volume += cachedEdges.get(phrase).length;

			// compute boundary
			for (int j = 0; j <= i; ++j) {
				String[] vStr = cachedEdges.get(phrase);
				for (String v: vStr) {
					if (!S.contains(v)) {
						++boundary;
					}
				}
			}
			
			if (boundary / volume < minScore) {
				num = i;
			}
		}
		
		return num + 1;
	}
}

class Pair implements Comparable<Pair> {
	String phrase;
	double score;
	
	public Pair(String phrase, double score) {
		this.phrase = phrase;
		this.score = score;
	}

	@Override
	public int compareTo(Pair that) {
		return Double.compare(that.score, this.score);
	}
}