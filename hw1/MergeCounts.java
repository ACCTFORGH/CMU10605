package hw1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MergeCounts {
	int vocabularyCount = 1;

	public void merge() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String line = br.readLine();
		if (line == null) {
			return;
		}
		String[] fields = line.split("\t");
		String prevKey = fields[0];
		String[] twoParts = fields[0].split(",");
		String prevWord = twoParts[0];
		int count = Integer.parseInt(fields[1]);
		while ((line = br.readLine()) != null) {
			fields = line.split("\t");
			twoParts = fields[0].split(",");
			if (fields[0].equals(prevKey)) {
				count += Integer.parseInt(fields[1]);
			} else {
				if (twoParts.length > 1 && !twoParts[0].equals("*") 
						&& !twoParts[0].equals(prevWord)) {  // get unique vocabulary count
					prevWord = twoParts[0];
					++vocabularyCount;
				}
				System.out.println(prevKey + "\t" + count);
				prevKey = fields[0];
				count = Integer.parseInt(fields[1]);
			}
		}
		System.out.println(prevKey + "\t" + count);
		System.out.println("V\t" + vocabularyCount);
	}

	public static void main(String[] args) throws IOException {
		MergeCounts mergeCount = new MergeCounts();
		mergeCount.merge();
	}
}
