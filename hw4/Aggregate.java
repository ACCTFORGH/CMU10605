import java.util.*;
import java.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;

public class Aggregate {

    private final static String FRONT_YEAR = "1960";
    
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        // stop words
        private final static String[] stopWordsArr = {"i", "the", "to", "and", "a", "an", "of", "it", "you", "that", "in", "my", "is", "was", "for"};
        private final static Set<String> stopWordsSet = new HashSet<String>(Arrays.asList(stopWordsArr));
        private Text outputKey = new Text();
        private Text outputValue = new Text();
        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            String data = value.toString();
            String[] fields = data.split("\t");

            // skip stop unigram/bigram that contains stop word 
            String[] words = fields[0].split(" ");
            for (int i = 0; i < words.length; ++i) {
                if (stopWordsSet.contains(words[i])) {
                    return;
                }
            }

            // in order to sort by unigram/bigram
            outputKey.set(fields[0]);
            if (isBigram(fields[0])) {
                if (isFrontground(fields[1])) {
                    outputValue.set("C" + fields[2]);
                } else {
                    outputValue.set("B" + fields[2]);
                }
                output.collect(outputKey, outputValue);
            } else if (isFrontground(fields[1])) {  // ignore background unigram
                outputValue.set("C" + fields[2]);
                output.collect(outputKey, outputValue);
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterator<Text> values,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            long fgCounter = 0;
            long bgCounter = 0;

            while (values.hasNext()) {
                String value = values.next().toString();
                if (value.startsWith("C")) {
                    fgCounter += Long.parseLong(value.substring(1));
                } else {
                    bgCounter += Long.parseLong(value.substring(1));
                }
            }
            if (isBigram(key.toString())) {
                output.collect(key, new Text(fgCounter + "\t" + bgCounter));
            } else {
                output.collect(key, new Text(String.valueOf(fgCounter)));
            }
        }
    }

    public static boolean isBigram(String phrase) {
        return phrase.indexOf(" ") != -1;
    }

    static boolean isFrontground(String year) {
        return year.equals(FRONT_YEAR);
    }
}
