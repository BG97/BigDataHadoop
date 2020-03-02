import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OnTimeScheduleMapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String[] col = value.toString().split(",");
		
		if (!"Year".equals(col[0])) {
			String onT = "0";
			if(!"NA".equals(col[14])){
				if (Integer.parseInt(col[14]) <= 20 ) {
					onT = "1";
				}
			context.write(new Text(col[8]), new Text(onT));
			}
			
		}
	}
}