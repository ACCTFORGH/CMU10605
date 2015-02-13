package hw1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NBTest {
	private Map<String, Float> featuresDict;
	private String testFileName;
	private int vocabularySize;
	private int labelNum; 
	private Set<String> labels;
	private Set<String> uniqueWord;
	private DocTokenizer docTokenizer;

	public NBTest(String testFileName) throws NumberFormatException, IOException {
		this.testFileName = testFileName;
		featuresDict = new HashMap<String, Float>();
		uniqueWord = new HashSet<String>();
		labels = new HashSet<String>();
		docTokenizer = new DocTokenizer();
	}
	
	/**
	 * Preprocess test data to get all unique words
	 * @throws IOException
	 */
	public void getUniqueWordInTest() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(testFileName));
		String line = null;
		while ((line = br.readLine()) != null) {
			Set<String> tokens = docTokenizer.tokenizeDoc(line);
			for (String word: tokens) {
				uniqueWord.add(word);
			}
		}
	}

	/**
	 * Store those features appeared in test file into a map
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void buildMap() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String line = null;
		String[] fields = new String[2];
		String[] words = new String[2];
		String label = null;
		while ((line = br.readLine()) != null) {
			fields = line.split("\t");
			words = fields[0].split(",");
			if (line.startsWith("V")) {  // vocabulary size
				vocabularySize = Integer.parseInt(fields[1]);
			} else if (words.length > 1) {  // words count
				if (uniqueWord.contains(words[0])) {
					featuresDict.put(fields[0], Float.parseFloat(fields[1]));
				}
				label = words[1];
				labels.add(label);
			} else {   // labels count
				featuresDict.put(fields[0], Float.parseFloat(fields[1]));
				label = fields[0];
				if (!label.equals("*")) {
					labels.add(label);
				}
			}
		}
		uniqueWord = null;
		System.gc();
		labelNum = labels.size();
	}

	/**
	 * Predict the label
	 * @throws IOException
	 */
	private void predict() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(testFileName));
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		String doc = null;
		while ((doc = br.readLine()) != null) {
			Set<String> tokens = docTokenizer.tokenizeDoc(doc);
			float maxProbability = Float.NEGATIVE_INFINITY;
			String bestLabel = "";
			for (String label: labels) {
				float probability = getProbability(tokens, label);
				if (probability > maxProbability) {
					maxProbability = probability;
					bestLabel = label;
				}
			}
//			System.out.println(bestLabel + "\t" + maxProbability);
			bw.write(bestLabel + "\t" + maxProbability + "\n");
		}
		bw.close();
	}
	
	/**
	 * Get log probability of a label
	 * @param tokens tokens in the test doc
	 * @param label the label to predict
	 * @return log probability of the given label
	 */
	private float getProbability(Set<String> tokens, String label) {
		float numerator = getFeatureValue(label);
		float denominator = getFeatureValue("*") + labelNum;
		float probability = (float) Math.log(numerator / denominator);
		String eventSuffix = "," + label;
		denominator = getFeatureValue("*" + eventSuffix) + vocabularySize;
		for (String word: tokens) {
			String event = word + eventSuffix;
			numerator = getFeatureValue(event) + 1;
			probability += Math.log(numerator / denominator);
		}
		return probability;
	}
	
	private float getFeatureValue(String key) {
		return featuresDict.containsKey(key)? featuresDict.get(key) : 0;
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		NBTest nbTest = new NBTest(args[0]);
		nbTest.getUniqueWordInTest();
		nbTest.buildMap();
		nbTest.predict();
	}
}
