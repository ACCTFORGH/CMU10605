import java.util.*;
import java.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;

public class NB_train_hadoop {

    public static class NBMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private static String[] targetLabels = {"CCAT", "ECAT", "GCAT", "MCAT"};
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, 
                OutputCollector<Text, IntWritable> output, 
                Reporter reporter) throws IOException { 
            String doc = value.toString();
            Vector<String> tokens = tokenizeDoc(doc);
            String labels = tokens.get(0);
            int size = tokens.size();
            for (int k = 0; k < 4; ++k) {
                if (labels.contains(targetLabels[k])) {
                    String label = targetLabels[k];
                    for (int i = 1; i < size; ++i) {
                        word.set("Y=" + label + ",W=" + tokens.get(i));
                        output.collect(word, one);
                        word.set("Y=" + label + ",W=*");
                        output.collect(word, one);
                    }
                    word.set("Y=" + label);
                    output.collect(word, one);
                    word.set("Y=*");
                    output.collect(word, one);
                }
            }
        }

        static Vector<String> tokenizeDoc(String cur_doc) {
            String[] words = cur_doc.split("\\s+");
            Vector<String> tokens = new Vector<String>();
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("\\W", "");
                if (words[i].length() > 0) {
                    tokens.add(words[i]);

                }

            }
            return tokens;
        }
    }

    public static class NBReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }
}
