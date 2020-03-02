import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ReasonForCancellationMapper extends Mapper<Object, Text, Text, IntWritable> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

		String[] col = value.toString().split(",");

		if (!"Year".equals(col[0])) {
			if (!"NA".equals(col[22])&&"1".equals(col[21])&&col[22].trim().length()>0) {
				context.write(new Text(col[22]), new IntWritable(1));
			}

		}
	}
}