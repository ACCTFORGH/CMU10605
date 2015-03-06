package hw2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 10605 hw2 phrase finding
 * process bigram and unigram corpus
 * @author Xiaoxiang Wu
 * 
 * Possible Improvement:
 * 1. We do not need to output Bx actually, since we are not 
 * using it when computing score. 
 * Note: we should modify MessageGenerator and MessageUnigramCombiner as well!
 *
 */

public class Aggregate {
	/** 
	 * 1 means processing bigram corpus 
	 * 0 means processing unigram corpus
	 */
	private int mode = 0;
	private static Set<String> stopWordsSet;
	/**
	 * Constants to define front corpus and background corpus
	 * front: 1960-1969
	 * background: 1970-1999
	 */
	private final static String FRONT_YEAR = "1960";

	public Aggregate(int mode) {
		this.mode = mode;
		// define the stop words list if not exist
		if (stopWordsSet == null) {
			String[] stopWordsArr = {"i", "the", "to", "and", "a", "an", "of", "it", "you", "that", "in", "my", "is", "was", "for"};
			stopWordsSet = new HashSet<String>(Arrays.asList(stopWordsArr));
			stopWordsArr = null;
		}
	}

	/**
	 * Process the corpus. Output all counter to the bigrams or unigrams. 
	 * Note that we should ignore all bigram or unigram that contains stopwords.
	 * If the mode is 1, process bigram corpus
	 * If the mode is 0, process unigram corpus
	 * @throws IOException 
	 */
	public void processCorpus() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		if (mode == 0) {
			processUnigramCorpus(br, bw);
		} else if (mode == 1) {
			processBigramCorpus(br, bw);
		} else {
			System.err.println("Invalid mode");
		}
		br.close();
		bw.close();
	}

	// keep in mind to remove stopwords
	private void processBigramCorpus(BufferedReader br, BufferedWriter bw) throws IOException {
		String line = null;
		int frontCounter = 0;
		int backCounter = 0;
		String prevKey = null;
		String tab = "\t";
		String space = " ";
		while ((line = br.readLine()) != null) {
			// there should be three fields:
			// 1. bigram  2. year  3. counter
			int tabPos1 = line.indexOf(tab);
			String bigram = line.substring(0, tabPos1++);
			int tabPos2 = line.indexOf(tab, tabPos1);
			String year = line.substring(tabPos1, tabPos2++);
			int counter = Integer.parseInt(line.substring(tabPos2));
			// there should be two words in the bigram
			int spacePos = bigram.indexOf(space);
			String word1 = bigram.substring(0, spacePos);
			String word2 = bigram.substring(spacePos + 1);
			// check if this bigram contains stopwords, if it is, ignore it!
			if (stopWordsSet.contains(word1) || stopWordsSet.contains(word2)) {
				continue;
			}

			// tell apart front corpus and background corpus
			boolean isFrontYear = year.equals(FRONT_YEAR);
			//			if (fields[1].equals(FRONT_YEAR)) {
			if (prevKey != null && !bigram.equals(prevKey)) {
				bw.write(prevKey + tab + frontCounter + tab + backCounter);
				bw.newLine();
				prevKey = bigram;
				frontCounter = isFrontYear? counter : 0;
				backCounter = isFrontYear? 0: counter;
			} else {
				prevKey = bigram;
				frontCounter += isFrontYear? counter: 0;
				backCounter += isFrontYear? 0: counter;
			}
		}
		// output last key
		bw.write(prevKey + tab + frontCounter + tab + backCounter);
		bw.newLine();
	}

	private void processUnigramCorpus(BufferedReader br, BufferedWriter bw) throws NumberFormatException, IOException {
		String line = null;
		int frontCounter = 0;
		String prevKey = null;
		long fgUnigramCount = 0;
		long fgUniqueUnigramCount = 0;
		boolean hasFrontYear = false;
		String tab = "\t";
		while ((line = br.readLine()) != null) {
			int tabPos1 = line.indexOf(tab);
			String unigram = line.substring(0, tabPos1++);
			int tabPos2 = line.indexOf(tab, tabPos1);
			String year = line.substring(tabPos1, tabPos2++);
			int counter = Integer.parseInt(line.substring(tabPos2));
			// remove stop words
			if (stopWordsSet.contains(unigram)) {
				continue;
			}
			// only care about front unigram
			boolean isFrontYear = year.equals(FRONT_YEAR);
			if (prevKey != null && !unigram.equals(prevKey)) {
				fgUniqueUnigramCount += hasFrontYear? 1: 0;
				fgUnigramCount += frontCounter;
				bw.write(prevKey + tab + frontCounter);
				bw.newLine();
				prevKey = unigram;
				hasFrontYear = false;
				frontCounter = isFrontYear? counter: 0;
			} else {
				prevKey = unigram;
				frontCounter += isFrontYear? counter: 0;
			}
			hasFrontYear |= isFrontYear;
		}
		fgUniqueUnigramCount += hasFrontYear? 1: 0;
		fgUnigramCount += frontCounter;
		// output last key
		bw.write(prevKey + tab + frontCounter);
		bw.newLine();
		bw.write(" fgUni\t" + fgUnigramCount);
		bw.newLine();
		bw.write(" fgUniqueUni\t" + fgUniqueUnigramCount);
		bw.newLine();
	}

	public static void main(String[] args) throws IOException {
		Aggregate ag = new Aggregate(Integer.parseInt(args[0]));
		ag.processCorpus();
	}
}
