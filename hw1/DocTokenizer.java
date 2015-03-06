package hw1;
import java.util.HashSet;
import java.util.Set;

public class DocTokenizer {

	private Stemmer stemmer;
	Set<String> stopWords;


	public DocTokenizer() {
		stemmer = new Stemmer();
		stopWords = new HashSet<String>();
		String[] stopWordsArr = {"a","about","above","across","after","afterwards","again","against","all","almost","alone","along","already","also","although","always","am","among","amongst","amoungst","amount","an","and","another","any","anyhow","anyone","anything","anyway","anywhere","are","around","as","at","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","between","beyond","bill","both","bottom","but","by","call","can","cannot","cant","co","computer","con","could","couldnt","cry","de","describe","detail","do","done","down","due","during","each","eg","eight","either","eleven","else","elsewhere","empty","enough","etc","even","ever","every","everyone","everything","everywhere","except","few","fifteen","fify","fill","find","fire","first","five","for","former","formerly","forty","found","four","from","front","full","further","get","give","go","had","has","hasnt","have","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","him","himself","his","how","however","hundred","i","ie","if","in","inc","indeed","interest","into","is","it","its","itself","keep","last","latter","latterly","least","less","ltd","made","many","may","me","meanwhile","might","mill","mine","more","moreover","most","mostly","move","much","must","my","myself","name","namely","neither","never","nevertheless","next","nine","no","nobody","none","noone","nor","not","nothing","now","nowhere","of","off","often","on","once","one","only","onto","or","other","others","otherwise","our","ours","ourselves","out","over","own","part","per","perhaps","please","put","rather","re","same","see","seem","seemed","seeming","seems","serious","several","she","should","show","side","since","sincere","six","sixty","so","some","somehow","someone","something","sometime","sometimes","somewhere","still","such","system","take","ten","than","that","the","their","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they","thick","thin","third","this","those","though","three","through","throughout","thru","thus","to","together","too","top","toward","towards","twelve","twenty","two","un","under","until","up","upon","us","very","via","was","we","well","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","with","within","without","would","yet","you","your","yours","yourself","yourselves"};
		for (String word: stopWordsArr) {
			stopWords.add(word);
		}
	}

	public Set<String> tokenizeDoc(String cur_doc) {
		Set<String> tokens = new HashSet<String>();
		cur_doc = cur_doc.replace("_", " ");
		String[] words = cur_doc.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			int begin = 0;
			int len = words[i].length();
			while (begin < len) {
				int end = words[i].indexOf('%', begin);
				if (end < 0) {
					String word = words[i].substring(begin).replaceAll("\\W+", "").toLowerCase();
					if (word.length() > 1 && !hasDigit(word) && !stopWords.contains(word)) {
						for (int j = 0; j < word.length(); ++j) {
							stemmer.add(word.charAt(j));
						}
						stemmer.stem();
						tokens.add(stemmer.toString());
					}
					break;
				}
				String word = words[i].substring(begin, end).replaceAll("\\W+", "").toLowerCase();
				if (word.length() > 1 && !hasDigit(word) && !stopWords.contains(word)) {
					for (int j = 0; j < word.length(); ++j) {
						stemmer.add(word.charAt(j));
					}
					stemmer.stem();
					tokens.add(stemmer.toString());
				}
				begin = end + 3;
			}
		}
		return tokens;
	}

	private boolean hasDigit(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		for (int i = 0; i < s.length(); ++i) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
}