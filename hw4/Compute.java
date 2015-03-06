import java.util.*;
import java.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;

public class Compute {
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        Text outputKey = new Text();
        Text outputVal = new Text();
        public void map(LongWritable key, Text value,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            String valueStr = value.toString();
            int tabPos = valueStr.indexOf("\t");
            outputKey.set(valueStr.substring(0, tabPos));
            outputVal.set(valueStr.substring(tabPos + 1));
            output.collect(outputKey, outputVal);
        }
    }

    public static class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
        static long fgBigramCount;
        static long bgBigramCount;
        static long fgUnigramCount;

        @Override
        public void configure(JobConf job) {
            // add one smoothning
            fgBigramCount = Long.parseLong(job.get("fgBigramCount")) +
                Long.parseLong(job.get("fgUniqueBigram"));
            bgBigramCount = Long.parseLong(job.get("bgBigramCount")) + 
                Long.parseLong(job.get("bgUniqueBigram"));
            fgUnigramCount = Long.parseLong(job.get("fgUnigramCount")) + 
                Long.parseLong(job.get("fgUniqueUnigram"));
        }

        public void reduce(Text key, Iterator<Text> values,
                OutputCollector<Text, Text> output,
                Reporter reporter) throws IOException {
            double Cxy = 0, Bxy = 0, Cx = 0, Cy = 0;
            boolean flag = true;

            while (values.hasNext()) {
                String value = values.next().toString();
                String[] counters = value.split("\t");
                if (counters.length == 1) {
                    if (flag) {
                        Cx = Double.parseDouble(counters[0]) + 1;
                        flag = false;
                    } else {
                        Cy = Double.parseDouble(counters[0]) + 1;
                    }
                } else {
                    Cxy = Double.parseDouble(counters[0]) + 1;
                    Bxy = Double.parseDouble(counters[1]) + 1;
                }
            }

            // phraseness score
            double p = Cxy / fgBigramCount;
            double q = Math.log(Cx / fgUnigramCount) + Math.log(Cy / fgUnigramCount);
            double pScore = p * (Math.log(p) - q);
            
            // informativeness score
            q = Bxy / bgBigramCount;
            double iScore = p * (Math.log(p) - Math.log(q));

            // total score
            double totalScore = pScore + iScore;
            String scores = totalScore + "\t" + pScore + "\t" + iScore;
            output.collect(key, new Text(scores));
        }
    }
}
