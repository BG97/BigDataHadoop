import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
public class exe{

	public static class ResultPair implements Comparable<ResultPair> {
		
		double count;
		String key;
		String value;
		double rF;
		ResultPair(double rF, double count, String key) {
			this.rF = rF;
			this.count = count;
			this.key = key;
			  
		}
@Override		
		public int compareTo(ResultPair resultPair) {
			
				if(this.rF>resultPair.rF){	
				return -1;
			} else {
				return 1;
			}
			
		}

}
	public static TreeSet<ResultPair> TreePair = new TreeSet<ResultPair>();
	public static class Map extends Mapper<LongWritable, Text, Text, LongWritable>
		{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
			{

			String[] words = value.toString().split("\\s+");
			Text keys = new Text();
			LongWritable vals = new LongWritable(1);
			StringBuilder sb = new StringBuilder();

			for (String word : words) 
			{
				if (word.matches("\\w+")) 
				{
					keys.set(word.trim()+ " " + "!");
					context.write(keys,vals);
				}
			}

			
			for (int i = 0; i < words.length-1; i++)  
			{
				if (words[i].matches("\\w+") && words[i + 1].matches("\\w+"))
					{
						keys.set(sb.append(words[i]).append(" ").append(words[i + 1]).toString());
						context.write(keys,vals);
						sb.delete(0, sb.length());
					}

			}
			}
		}

		











	public static class conbine extends Reducer<Text, LongWritable, Text, LongWritable>
		{
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException
			{
				long count = 0;
				LongWritable vals= new LongWritable();

				for (LongWritable val : values)
				{
				count = count + val.get();
				}
				vals.set(count);
				context.write(key, vals);
			}

		}














		public static class Reduce extends Reducer<Text, LongWritable, Text, Text> {

		private DoubleWritable CountTotal = new DoubleWritable();
		private DoubleWritable CountRelativeF = new DoubleWritable();

		private HashMap<String,Double> hm = new HashMap<String,Double>();

		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

			if (key.toString().split(" ")[1].equals("!")) {
				String keyH = key.toString().split(" ")[0];
				CountTotal.set(0);
				
				double sum = 0;
                        	for (LongWritable value : values) {
                                	sum += value.get();
                        	}

				CountTotal.set(sum);
				hm.put(keyH,sum);
			} 


			else {
				String keyH = key.toString().split(" ")[0];

			
				double countValue = 0;
	                        for (LongWritable value : values) {
        	                        countValue += value.get();
                	        }

				double countKey = hm.get(keyH);
				CountRelativeF.set((double) countValue / countKey);
				Double CountRelative = CountRelativeF.get();

					if(CountRelative !=1.0d)
						{

							TreePair.add(new ResultPair(CountRelative,countValue, key.toString()));
							if (TreePair.size() > 100) { TreePair.pollLast(); }

						}
					
			} 
			
		} 


    protected void cleanup(Context context) throws IOException, InterruptedException
                  {
                        while (!TreePair.isEmpty())
                                        {
                                           ResultPair pair = TreePair.pollFirst();
                                           context.write(new Text(pair.key), new Text(Double.toString(pair.rF)));
                                        }
                        }

		}

     
	
	
	
	public static void main(String[] args) throws Exception 
	{
		Job job = Job.getInstance(new Configuration());
		job.setJarByClass(exe.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setCombinerClass(conbine.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.waitForCompletion(true);




	}


}
