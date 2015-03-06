import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;


public class run {
    public static void main(String[] args) throws Exception {

        JobConf conf = new JobConf(run.class);
        conf.setJobName("Naive Bayes Training");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(NB_train_hadoop.NBMapper.class);
        conf.setCombinerClass(NB_train_hadoop.NBReducer.class);
        conf.setReducerClass(NB_train_hadoop.NBReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        conf.setNumReduceTasks(Integer.parseInt(args[2]));

        JobClient.runJob(conf);
    }
}
