import java.util.*;
import java.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;

public class CountSize {
    private final static Text key1 = new Text("1");
    private final static Text key2 = new Text("2");
    private final static Text key3 = new Text("3");

    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        Text outputVal = new Text();

        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            String valueStr = value.toString();
            String[] fields = valueStr.split("\t");


            if (Aggregate.isBigram(fields[0])) {
                outputVal.set(fields[1]);
                output.collect(key1, outputVal);
                outputVal.set(fields[2]);
                output.collect(key2, outputVal);
            } else {
                outputVal.set(fields[1]);
                output.collect(key3, outputVal);
            }
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, LongWritable> {

        public void reduce(Text key, Iterator<Text> values,
                OutputCollector<Text, LongWritable> output,
                Reporter reporter) throws IOException {
            long totalCount = 0;
            long uniqueCount = 0;

            while (values.hasNext()) {
                long counter = Long.parseLong(values.next().toString());
                if (counter != 0) {
                    totalCount += counter;
                    uniqueCount += 1;
                }
            }
            if (key.equals(key1)) {
                output.collect(new Text("fgBigramCount"),
                        new LongWritable(totalCount));
                output.collect(new Text("fgUniqueBigram"), 
                        new LongWritable(uniqueCount));
            } else if (key.equals(key2)) {
                output.collect(new Text("bgBigramCount"),
                        new LongWritable(totalCount));
                output.collect(new Text("bgUniqueBigram"), 
                        new LongWritable(uniqueCount));

            } else {
                output.collect(new Text("fgUnigramCount"),
                        new LongWritable(totalCount));
                output.collect(new Text("fgUniqueUnigram"), 
                        new LongWritable(uniqueCount));
            }
        }
    }
}
