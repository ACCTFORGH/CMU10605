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
			String[] stopWordsArr = {"a","about","above","across","after","afterwards","again","against","all","almost","alone","along","already","also","although","always","am","among","amongst","amoungst","amount","an","and","another","any","anyhow","anyone","anything","anyway","anywhere","are","around","as","at","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","between","beyond","bill","both","bottom","but","by","call","can","cannot","cant","co","computer","con","could","couldnt","cry","de","describe","detail","do","done","down","due","during","each","eg","eight","either","eleven","else","elsewhere","empty","enough","etc","even","ever","every","everyone","everything","everywhere","except","few","fifteen","fify","fill","find","fire","first","five","for","former","formerly","forty","found","four","from","front","full","further","get","give","go","had","has","hasnt","have","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","him","himself","his","how","however","hundred","i","ie","if","in","inc","indeed","interest","into","is","it","its","itself","keep","last","latter","latterly","least","less","ltd","made","many","may","me","meanwhile","might","mill","mine","more","moreover","most","mostly","move","much","must","my","myself","name","namely","neither","never","nevertheless","next","nine","no","nobody","none","noone","nor","not","nothing","now","nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise","our","ours","ourselves","out","over","own","part","per","perhaps","please","put","rather","re","same","see","seem","seemed","seeming","seems","serious","several","she","should","show","side","since","sincere","six","sixty","so","some","somehow","someone","something","sometime","sometimes","somewhere","still","such","system","take","ten","than","that","the","their","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they","thick","thin","third","this","those","though","three","through","throughout","thru","thus","to","together","too","top","toward","towards","twelve","twenty","two","un","under","until","up","upon","us","very","via","was","we","well","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","with","within","without","would","yet","you","your","yours","yourself","yourselves"};
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
