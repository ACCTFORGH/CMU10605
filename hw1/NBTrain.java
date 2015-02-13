package hw1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NBTrain {
	private Map<String, Integer> featuresDict;
	private static final int BUFFER_SIZE = 5000;
	private DocTokenizer docTokenizer;

	public NBTrain() {
		featuresDict = new HashMap<String, Integer>();
		docTokenizer = new DocTokenizer();
	}

	private void train() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String doc = null;
		int featureCount = 0;
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		while ((doc = br.readLine()) != null) {
			Set<String> tokens = docTokenizer.tokenizeDoc(doc);
			int size = tokens.size();
			if (size < 1) {
				continue;
			}
			int pos = doc.indexOf('\t');
			String[] labels = doc.substring(0, pos).split(",");
			for (String label: labels) {
				featureCount += updateFeaturesDict(label, 1);
				featureCount += updateFeaturesDict("*", 1);
				for (String word: tokens) {
					featureCount += updateFeaturesDict(word + "," + label, 1);
				}
				featureCount += updateFeaturesDict("*," + label, size); 
			}
			if (featureCount > BUFFER_SIZE) {
				for (Map.Entry<String, Integer> entry: featuresDict.entrySet()) {
//					System.out.println(entry.getKey() + "\t" + entry.getValue());
					bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
				}
				featuresDict.clear();
				System.gc();
				featureCount = 0;
			}
		}
		if (featureCount != 0) {
			for (Map.Entry<String, Integer> entry: featuresDict.entrySet()) {
//				System.out.println(entry.getKey() + "\t" + entry.getValue());
				bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			}
		}
		bw.close();
	}

	private int updateFeaturesDict(String key, int cnt) {
		int res = 1;   // means we add a new entry into map
		int count = 0;
		if (featuresDict.containsKey(key)) {
			count = featuresDict.get(key);
			res = 0;
		}
		count += cnt;
		featuresDict.put(key, count);
		return res;
	}

	public static void main(String[] args) {
		NBTrain nbTrain = new NBTrain();
		try {
			nbTrain.train();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
