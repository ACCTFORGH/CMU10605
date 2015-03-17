import java.util.*;
import java.io.*;

public class LR {
	static double overflow=20;
    static String[] allLabels = {"nl", "el", "ru", "sl", "pl", "ca", "fr", "tr", "hu", "de", "hr", "es", "ga", "pt"};

    int vocabSize;
    double initLearningRate;
    double regCoeff;
    int maxIter;
    int trainingSize;
    String testDataFileName;
    int[][] A;  // the value of k last time B[j] was updated
    double[][] B;  // parameter
    Map<String, Integer> labelsMap;
    int labelsNum;
    DocTokenizer docTokenizer;

    public LR(String[] args) {
        // parse arguments
		vocabSize = Integer.parseInt(args[0]);
		initLearningRate = Double.parseDouble(args[1]);
		regCoeff = Double.parseDouble(args[2]);
		maxIter = Integer.parseInt(args[3]);
		trainingSize = Integer.parseInt(args[4]);
		testDataFileName = args[5];

        // put all possible labels into map
        labelsMap = new HashMap<String, Integer>();
        labelsNum = allLabels.length;
        for (int i = 0; i < labelsNum; ++i) {
            labelsMap.put(allLabels[i], i);
        }
        docTokenizer = new DocTokenizer(vocabSize);
    }

    /**
     * Train memory efficient SGD Logistic regression model
     */
    public void train() throws IOException {
		A = new int[labelsNum][vocabSize];  
		B = new double[labelsNum][vocabSize];  
		
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String doc = null;
        double learningRate = initLearningRate;
        Map<Integer, Integer> tokens;

        double regularTerm = 0;
        int k = 0;

        for (int t = 1; t <= maxIter; ++t) {
            // update learning rate
            learningRate = initLearningRate / (t * t);
            regularTerm = 2 * learningRate * regCoeff;

            // iterate all training examples
            for (int docCount = 0; docCount < trainingSize; ++docCount) {
                doc = br.readLine();
                ++k;

                // get labels and words
                int tabPos = doc.indexOf("\t");
                int[] labels = getLabels(doc.substring(0, tabPos));
                //tokens = docTokenizer.defaultTokenizeDoc(doc.substring(tabPos + 1));
                tokens = docTokenizer.tokenizeDoc(doc.substring(tabPos + 1));

                // train classifers for all labels
                for (int i = 0; i < labelsNum; ++i) {
                    double p = getProb(B[i], tokens);
                    
                    // update all non-zero features
                    for (Map.Entry<Integer, Integer> entry: tokens.entrySet()) {
                        int j = entry.getKey();
                        B[i][j] *= Math.pow(1 - regularTerm, k - A[i][j]);
                        B[i][j] += learningRate * (labels[i] - p) * entry.getValue();
                        A[i][j] = k;
                    }
                }
            }
        }
        // batch udpate parameter beta
        for (int i = 0; i < labelsNum; ++i) {
            for (int j = 0; j < vocabSize; ++j) {
                B[i][j] *= Math.pow(1- regularTerm, k - A[i][j]);
            }
        }

        br.close();
    }

    public void predict() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(testDataFileName));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        
        String doc = null;
        Map<Integer, Integer> tokens;
        while ((doc = br.readLine()) != null) {
            int tabPos = doc.indexOf("\t");
            int[] labels = getLabels(doc.substring(0, tabPos));
            //tokens = docTokenizer.defaultTokenizeDoc(doc.substring(tabPos + 1));
            tokens = docTokenizer.tokenizeDoc(doc.substring(tabPos + 1));
            // compute score for each label
            // The first label
            double score = getProb(B[0], tokens);
            System.out.print(allLabels[0] + "\t" + String.valueOf(score));
            // remaining labels
            for (int i = 1; i < labelsNum; ++i) {
                score = getProb(B[i], tokens);
                System.out.print("," + allLabels[i] + "\t" + String.valueOf(score));
            }
            System.out.println();
        }

        br.close();
    }

    private int[] getLabels(String labelStr) {
        String[] docLabels = labelStr.split(",");
        int[] labels = new int[labelsNum];
        for (int i = 0; i < docLabels.length; ++i) {
            labels[labelsMap.get(docLabels[i])] = 1;
        }
        return labels;
    }

    private double getProb(double[] beta, 
            Map<Integer, Integer> tokens) {
        double score = 0;
        for (Map.Entry<Integer,Integer> entry: tokens.entrySet()) {
            score += entry.getValue() * beta[entry.getKey()];
        }

        // compute sigmoid
		if (score > overflow) {
			score = overflow;
		} else if (score < -overflow) {
			score = -overflow;
		}
		double exp = Math.exp(score);
		return exp / (1 + exp);
    }

	public static void main(String[] args) throws Exception {
		// parse input arguments
		if (args.length != 6) {
			System.err.println("Usage: java LR <vacab size> <learning rage> <regularization coef> "
					+ "<max iter> <training dataset size> <test data>");
			System.exit(-1);
		}
		
        LR lr = new LR(args);
        lr.train();
        lr.predict();
	}

}
