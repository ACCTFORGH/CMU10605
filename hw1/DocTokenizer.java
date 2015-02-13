package hw1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
					//					String word = parseWord(words[i].substring(begin));
					//					if (word.length() > 0) { 
					//						tokens.add(word);
					//					}
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
				//				String word = parseWord(words[i].substring(begin, end));
				//				if (word.length() > 0) {
				//					tokens.add(word);
				//				}
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

	/*
	private String parseWord(String word) {
		word = word.replaceAll("\\W+", "").toLowerCase();
		//		if (stopWords.contains(word)) {
		//			return "";
		//		}
		//		return word.length() > 1 && !hasDigit(word) && !stopWords.contains(word)? word: "";
		if (word.length() > 1 && !hasDigit(word) && !stopWords.contains(word)) {
			for (int j = 0; j < word.length(); ++j) {
				stemmer.add(word.charAt(j));
			}
			stemmer.stem();
			return stemmer.toString();
		}
		return "";
	}
	*/

	/*
	// adding stemming
	public List<String> myTokenizer(String doc) {
		List<String> tokens = new ArrayList<String>();
		int len = doc.length();
		int start = 0, end = 0;
		while (end < len) {
			char c = doc.charAt(end++);
			if (Character.isLetter(c)) { 
				continue;
			} else if (c == ' ' || c == '_' || c == '\t') {
				String s = doc.substring(start, end - 1).trim();
				// skip number 
				if (s.length() > 0 && !hasDigit(s) && !stopWords.contains(s)) {
					tokens.add(s.toLowerCase());
					//					for (int i = 0; i < s.length(); ++i) {
					//						stemmer.add(s.charAt(i));
					//					}
					//					stemmer.stem();
					//					tokens.add(stemmer.toString());
				}
				start = end;
			} else if (c == '%') {  // skip url entity
				// !!!!!! need to add word to tokens here!!!
				String s = doc.substring(start, end - 1);
				if (s.length() > 0 && !hasDigit(s) && !stopWords.contains(s)) {
					tokens.add(s.toLowerCase());
					//					for (int i = 0; i < s.length(); ++i) {
					//						stemmer.add(s.charAt(i));
					//					}
					//					stemmer.stem();
					//					tokens.add(stemmer.toString());
				}

				if (end + 1 < doc.length() && 
						Character.isLetterOrDigit(doc.charAt(end)) &&
						Character.isLetterOrDigit(doc.charAt(end + 1))) {
					end += 2;
					start = end;
				}
			}
		}   
		String s = doc.substring(start, len).trim();
		if (s.length() > 0 && !hasDigit(s) && !stopWords.contains(s)) {
			tokens.add(s.toLowerCase());
			//			for (int i = 0; i < s.length(); ++i) {
			//				stemmer.add(s.charAt(i));
			//			}
			//			stemmer.stem();
			//			tokens.add(stemmer.toString());
		}
		return tokens;
	}
	 */

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

	public static void main(String[] args) throws IOException {
		//		BufferedReader br = new BufferedReader(
		//				new FileReader("/Users/wxx/IDE/workspace/CMU605/hw1Data/abstract.train"));
		//		Map<String, Long> count = new HashMap<String, Long>();
		//		String line = null;
		//		while ((line = br.readLine()) != null) {
		//			Vector<String> tokens = tokenizeDoc(line);
		//			String[] labels = tokens.get(0).split(",");
		//			long n = tokens.size() - 1;
		//			for (String label: labels) {
		//				if (count.containsKey(label)) {
		//					count.put(label, count.get(label) + n);
		//				} else {
		//					count.put(label, n);
		//				}
		//			}
		//		}
		//		for (String label: count.keySet()) {
		//			System.out.println(label + ": " + count.get(label));
		//		}
		//		String doc = "de	DbSNP Gilean_McVean%20_20'20 Genome_Reference_Consortium Human_genetic_variation Population_groups_in_biomedicine Mark_Bender_Gerstein Wellcome_Trust_Sanger_Institute";
		//		String doc = "pt,es,ru,pl,fr,de	mcintosh red mcintosh colloquially mac apple cultivar red green skin tart flavor tender white flesh ripens late september traditionally most popular cultivar eastern canada new england well known pink applesauce unpeeled mcintoshes make also wellsuited cider pies common find cultivar packed children's lunches north america owing small medium size longstanding reputation healthy snack";
		//		String doc = "pt	children's";
		//		String doc = "hr	003 %C4%90or%C4%91e_Bala%C5%A1evi%C4%87 %C4%90or%C4%91e_Bala%C5%A1evi%C4%87 Celove%C4%8Dernji_the_Kid Bezdan_%28album%29";
				String doc = "pt,tr,hu,es,ru,pl,ca,nl,sl,fr,ga,de,hr,el	autism disorder neural development characterized impaired social interaction communication restricted repetitive behavior these signs all begin before child three years old autism affects information processing brain altering how nerve cells their synapses connect organize how occurs well understood one three recognized disorders autism spectrum asds other two being asperger syndrome lacks delays cognitive development language pervasive developmental disordernot otherwise specified commonly abbreviated pddnos diagnosed when full set criteria autism asperger syndrome met autism strong genetic basis although genetics autism complex unclear whether asd explained more rare mutations rare combinations common genetic variants rare cases autism strongly associated agents cause birth defects controversies surround other proposed environmental causes such heavy metals pesticides childhood vaccines vaccine hypotheses biologically implausible lack convincing scientific evidence prevalence autism 1u20132 per 1000 people worldwide however centers disease control prevention cdc reports approximately 9 per 1000 children united states diagnosed asd number people diagnosed autism increased dramatically since 1980s partly due changes diagnostic practice question whether actual prevalence increased unresolved parents usually notice signs first two years their child's life signs usually develop gradually some autistic children first develop more normally then regress early behavioral cognitive intervention can help autistic children gain selfcare social communication skills although there no known cure there been reported cases children who recovered many children autism live independently after reaching adulthood though some become successful autistic culture developed some individuals seeking cure others believing autism should accepted difference treated disorder";
		DocTokenizer docTokenizer = new DocTokenizer();
//		String doc = "t	";
//		for (String word: docTokenizer.stopWords) {
//			doc += word + " ";
//		}
		System.out.println(docTokenizer.tokenizeDoc(doc));
	}
}