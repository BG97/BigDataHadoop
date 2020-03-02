import java.io.IOException;
//import java.util.StringTokenizer;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class pokerCardMRSolver {

  public static class pokerMapper
       extends Mapper<LongWritable, Text, Text, IntWritable>{

    //private final static IntWritable one = new IntWritable(1);
    //String card = new Text();

    public void map(LongWritable key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String itr = value.toString();
      String[] split = itr.split(",");
      Text Key = new Text(split[0]);
      IntWritable Val = new IntWritable(Integer.parseInt(split[1]));
      //int i;
      //int ar[];
      //for (i =0; i < split.length; i++){
        //System.out.println(split[1]);
       // ar[i]=split[1][i];
      //}

      context.write(Key, Val);

    }
  }

  public static class suitReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    //private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> value,
                       Context context
                       ) throws IOException, InterruptedException {

     // int sum = 0;
     // for (IntWritable val : values) {
      //  sum += val.get();
     // }
     // result.set(sum);
      //context.write(key, result);
        int sumRanks = 0;


        ArrayList<Integer> pokerContain = new ArrayList<Integer>();

        for (IntWritable val : value) {
          sumRanks+= val.get();
          pokerContain.add(val.get());
          //system.out.println(pokerOut);
        }

        if(sumRanks < 91){
          for (int i = 1;i <= 13;i++){
            if(!pokerContain.contains(i))
              context.write(key, new IntWritable(i));
          }
        }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf,"Poker Card Solver");
    job.setJarByClass(pokerCardMRSolver.class);
    job.setMapperClass(pokerMapper.class);
    //job.setCombinerClass(suitReducer.class);
    job.setReducerClass(suitReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
