import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AirportTaxiMapper extends Mapper<Object, Text, Text, Text> {

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

		String[] col = value.toString().split(",");
		if (!"Year".equals(col[0])) {
			if (!"NA".equals(col[20])) {
				context.write(new Text(col[16]), new Text(col[20]));
			}
			if (!"NA".equals(col[19])) {
				context.write(new Text(col[17]), new Text(col[19]));
			}

		}
	}
}