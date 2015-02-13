package hw2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.PriorityQueue;

/**
 * 10605 hw2 phrase finding
 * process bigram and unigram corpus
 * @author Xiaoxiang Wu
 *
 */

class Phrase implements Comparable<Phrase> {
	String phrase;
	double totalScore;
	double phrasenessScore;
	double informativeScore;
	public Phrase(String phrase, double phrasenessScore, 
			double informativeScore, double d) {
		this.phrase = phrase;
		this.phrasenessScore = phrasenessScore;
		this.informativeScore = informativeScore;
		this.totalScore = d;
	}
	@Override
	public int compareTo(Phrase o) {
		if (this.totalScore > o.totalScore) {
			return 1;
		} else if (this.totalScore < o.totalScore) {
			return -1;
		} else {
			return 0;
		}
	}
}

public class PhraseGenerator {
	PriorityQueue<Phrase> minHeap;
	private final int PHRASE_NUM = 20;
	public PhraseGenerator() {
		minHeap = new PriorityQueue<Phrase>(PHRASE_NUM);
	}

	public void generatePhrase() throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		String line = null;
		// special count: 0: bg bigram count, 1: bg unique bigram count, 2: fg bigram count
		// 	3: fg unigram count, 4: fg unique bigram count, 5: fg unqiue unigram count

		// some special count
		String tab = "\t";
		line = br.readLine();
		double bgBigramTotalCount = Double.parseDouble(line.substring(line.indexOf(tab) + 1));
		line = br.readLine();
		bgBigramTotalCount += Double.parseDouble(line.substring(line.indexOf(tab) + 1));  // smoothing
		line = br.readLine();
		double fgBigramTotalCount = Double.parseDouble(line.substring(line.indexOf(tab) + 1));
		line = br.readLine();
		double fgUnigramTotalCount = Double.parseDouble(line.substring(line.indexOf(tab) + 1));
		line = br.readLine();
		fgBigramTotalCount += Double.parseDouble(line.substring(line.indexOf(tab) + 1));  // smoothing
		line = br.readLine();
		fgUnigramTotalCount += Double.parseDouble(line.substring(line.indexOf(tab) + 1));  // smoothing

		// use add one smoothing
		long[] fgUnigramCount = new long[2];
		int index = 0;
		int lineCount = 0;
		double fgBigramCount = 0;
		double bgBigramCount = 0;
		while ((line = br.readLine()) != null) {
			String[] fields = line.split("\t");
			++lineCount;
			if (fields.length == 2) {
				fgUnigramCount[index++] = Integer.parseInt(fields[1]) + 1;
			} else {
				fgBigramCount = Integer.parseInt(fields[1]) + 1;
				bgBigramCount = Integer.parseInt(fields[2]) + 1;
			}
			if (lineCount == 3) {
				// compute Phraseness score
				double p = fgBigramCount / fgBigramTotalCount;
				double phrasenessScore = p * (Math.log(p) - Math.log(fgUnigramCount[0] / fgUnigramTotalCount) 
						- Math.log(fgUnigramCount[1] / fgUnigramTotalCount));
				// compute Informativeness score
				double q = bgBigramCount / bgBigramTotalCount;
				double informativenessScore = p * (Math.log(p) - Math.log(q)); 
				double score = phrasenessScore + informativenessScore;
				// update heap here
				if (minHeap.size() < PHRASE_NUM) {
					minHeap.add(new Phrase(fields[0], phrasenessScore, informativenessScore, score));
				} else if (score > minHeap.peek().totalScore) {
					minHeap.remove();
					minHeap.add(new Phrase(fields[0], phrasenessScore, informativenessScore, score));
				}
				lineCount = 0;
				index = 0;
			}
		}
		br.close();
	}

	public void outputResult() throws IOException {
		int phraseNum = Math.min(PHRASE_NUM, minHeap.size());
		Phrase[] phrases = new Phrase[phraseNum];
		// sort by score
		for (int i = phraseNum - 1; i >= 0; --i) {
			phrases[i] = minHeap.poll();
		}
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(System.out));
		String tab = "\t";
		for (int i = 0; i < phraseNum; ++i) {
			StringBuilder sb = new StringBuilder();
			sb.append(phrases[i].phrase);
			sb.append(tab);
			sb.append(phrases[i].totalScore);
			sb.append(tab);
			sb.append(phrases[i].phrasenessScore);
			sb.append(tab);
			sb.append(phrases[i].informativeScore);
			bw.write(sb.toString());
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException {
		PhraseGenerator pg = new PhraseGenerator();
		pg.generatePhrase();
		pg.outputResult();
	}
}
