import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.jobcontrol.*;
import org.apache.hadoop.io.*;
import java.io.*;
import java.net.URI;

public class run_hadoop_phrase {
    public static void main(String[] args) throws Exception {
        JobClient.runJob(getAggregateJobConf(args));
        JobClient.runJob(getCountSizeJobConf(args));
        JobClient.runJob(getUniMessageConf(args));
        JobClient.runJob(getComputeConf(args));
    }


    public static JobConf getAggregateJobConf(String[] args) throws IOException {
        JobConf conf = new JobConf(run_hadoop_phrase.class);
        conf.setJobName("Phrase Finding -- Aggregate");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Aggregate.MyMapper.class);
        conf.setReducerClass(Aggregate.MyReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        Path[] aggregateInputPaths = new Path[2];
        aggregateInputPaths[0] = new Path(args[0]);
        aggregateInputPaths[1] = new Path(args[1]);
        FileInputFormat.setInputPaths(conf, aggregateInputPaths);
        FileOutputFormat.setOutputPath(conf, new Path(args[2]));

        return conf;
    }

    public static JobConf getCountSizeJobConf(String[] args) throws IOException {
        JobConf conf = new JobConf(run_hadoop_phrase.class);
        conf.setJobName("Phrase Finding -- CountSize");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(CountSize.MyMapper.class);
        conf.setReducerClass(CountSize.MyReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[2]));
        FileOutputFormat.setOutputPath(conf, new Path(args[3]));

        conf.setNumReduceTasks(1);

        return conf;
    }

    public static JobConf getUniMessageConf(String[] args) throws IOException {
        JobConf conf = new JobConf(run_hadoop_phrase.class);
        conf.setJobName("Phrase Finding -- MessageUnigram");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(MessageUnigram.MyMapper.class);
        conf.setReducerClass(MessageUnigram.MyReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[2]));
        FileOutputFormat.setOutputPath(conf, new Path(args[4]));

        return conf;
    }

    public static JobConf getComputeConf(String[] args) throws IOException {
        JobConf conf = new JobConf(run_hadoop_phrase.class);
        conf.setJobName("Phrase Finding -- Compute");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Compute.MyMapper.class);
        conf.setReducerClass(Compute.MyReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        // read total counter as global variable
        String pathName = args[3] + File.separator + "part-00000";
        FileSystem fs = FileSystem.get(URI.create(pathName), new Configuration());
        Path path = new Path(pathName);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(fs.open(path)));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split("\t");
            conf.set(fields[0], fields[1]);
        }

        FileInputFormat.setInputPaths(conf, new Path(args[4]));
        FileOutputFormat.setOutputPath(conf, new Path(args[5]));

        return conf;
    }
} 
