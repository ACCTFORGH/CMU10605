import java.util.*;
import java.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;



// input : output from Aggregate
public class MessageUnigram {
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        Text outputKey = new Text();
        Text outputVal = new Text();

        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            String valueStr = value.toString();
            String[] fields = valueStr.split("\t");

            if (fields.length == 3) {  // bigram, should output message
                String[] words = fields[0].split(" ");
                // output message
                outputVal.set(fields[0]);
                for (int i = 0; i < words.length; ++i) {
                    outputKey.set(words[i]);
                    output.collect(outputKey, outputVal);
                }
                // output counter
                outputKey.set(fields[0]);
                outputVal.set(fields[1] + "\t" + fields[2]);
                output.collect(outputKey, outputVal);
            } else {
                outputKey.set(fields[0]);
                outputVal.set(fields[1]);
                output.collect(outputKey, outputVal);
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
        Text outputKey = new Text();
        Text outputVal = new Text();

        public void reduce(Text key, Iterator<Text> values,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            // bigram counter
            String keyStr = key.toString();
            if (keyStr.indexOf(" ") != -1) {
                output.collect(key, values.next());
                return;
            }

            List<String> requests = new ArrayList<String>();
            String answer = "0";
            while (values.hasNext()) {
                String value = values.next().toString();
                if (value.indexOf(" ") == -1) {
                    answer = value;
                    continue;
                }
                requests.add(value);
            }

            outputVal.set(answer);
            for (String request: requests) {
                outputKey.set(request);
                output.collect(outputKey, outputVal);
            }
        }
    }
}
