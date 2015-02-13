package hw2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * 10605 hw2 phrase finding
 * generate message from bigram processed data
 * @author Xiaoxiang Wu
 *
 */
public class MessageGenerator {
	public static void generateMessage() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		String line = null;
		long fgBigramTotalCount = 0;
		long bgBigramTotalCount = 0;
		long fgUniqueBigramCount = 0;
		long bgUniqueBigramCount = 0;
		String tab = "\t";
		String space = " ";
		while ((line = br.readLine()) != null) {
			int tabPos1 = line.indexOf(tab);
			String bigram = line.substring(0, tabPos1++);
			// bigram front count
			int tabPos2 = line.indexOf(tab, tabPos1);
			int count1 = Integer.parseInt(line.substring(tabPos1, tabPos2++));
			fgBigramTotalCount += count1;
			fgUniqueBigramCount = (count1 == 0)? fgUniqueBigramCount: fgUniqueBigramCount + 1;
			// bigram backgroudn count
			int count2 = Integer.parseInt(line.substring(tabPos2));
			bgBigramTotalCount += count2;
			bgUniqueBigramCount = (count2 == 0)? bgUniqueBigramCount: bgUniqueBigramCount + 1;
			// output
			int spacePos = bigram.indexOf(space);
			bw.write(bigram.substring(0, spacePos++) + tab + bigram);
			bw.newLine();
			bw.write(bigram.substring(spacePos) + tab + bigram);
			bw.newLine();
		}
		// output special counter
		bw.write(" fgBi\t" + fgBigramTotalCount);
		bw.newLine();
		bw.write(" bgBi\t" + bgBigramTotalCount);
		bw.newLine();
		bw.write(" fgUniqBi\t" + fgUniqueBigramCount);
		bw.newLine();
		bw.write(" bgUniqBi\t" + bgUniqueBigramCount);
		bw.newLine();
		br.close();
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		generateMessage();
	}
}
