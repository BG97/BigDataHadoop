import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

public class ReasonForCancellationsReducer extends Reducer<Text, IntWritable, Text, Text> {
	private Map<String, Integer> map = new TreeMap<String, Integer>();

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		Iterator<IntWritable> line = values.iterator();
		Integer onT = 0;
		while (line.hasNext()) {
			int change = Integer.parseInt(line.next().toString());
			onT = onT + change;
		}
		map.put(key.toString(), onT);
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {


		List<Entry<String, Integer>> sortList = new ArrayList<Entry<String, Integer>>(map.entrySet());
		Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> one, Entry<String, Integer> two) {
				return two.getValue().compareTo(one.getValue());
			}
		});
		Entry<String, Integer> entry = sortList.get(0);

	
		if("A".equals(entry.getKey())){
			context.write(new Text("Cancellation Code is A: carrier"), new Text("Total: "+entry.getValue() + ""));
		}
		else if("B".equals(entry.getKey())){
			context.write(new Text("Cancellation Code is B: weather"), new Text("Total: "+entry.getValue() + ""));
		}
		else if("C".equals(entry.getKey())){
			context.write(new Text("Cancellation Code is C: NAS"), new Text("Total: "+entry.getValue() + ""));			
					}
		else if("D".equals(entry.getKey())){
			context.write(new Text("Cancellation Code is D: security"), new Text("Total: "+entry.getValue() + ""));
		}
		

		
	}
}