package hw2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 10605 hw2 phrase finding
 * process bigram and unigram corpus
 * @author Xiaoxiang Wu
 *
 */
public class MessageUnigramCombiner {

	public static void combine() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		String line = null;
		String prevKey = null;
		// ignore special count lines
		for (int i = 0; i < 6; ++i) {
			line = br.readLine();
			bw.write(line);
			bw.newLine();
		}
		line = br.readLine();
		String tab = "\t";
		int tabPos = line.indexOf(tab);
		prevKey = line.substring(0, tabPos++);
		String frontCounter = line.substring(tabPos);
		while ((line = br.readLine()) != null) {
			tabPos = line.indexOf(tab);
			String word = line.substring(0, tabPos++);
			if (!word.equals(prevKey)) {
				prevKey = word;
				frontCounter = line.substring(tabPos);
			} else {
				bw.write(line.substring(tabPos) + tab + frontCounter);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		combine();
	}
}
