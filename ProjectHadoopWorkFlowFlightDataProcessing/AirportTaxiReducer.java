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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AirportTaxiReducer extends Reducer<Text, Text, Text, Text> {
	private Map<String, Double> map = new TreeMap<String, Double>();
	private Map<String, Double> execptionMap = new TreeMap<String, Double>();

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Iterator<Text> line = values.iterator();
		int totol = 0;
		Integer Tp = 0;

		while (line.hasNext()) {
			String temp =line.next().toString();
			int time = Integer.parseInt(temp);
			Tp = Tp + time;
			totol = totol + 1;
		}
		double result = Tp * 1.0 / total;
		if(result==0.0){
			execptionMap.put(key.toString(), result);
		}else{
			map.put(key.toString(), Double.valueOf(result));
		}

	}

	@Override
	protected void cleanup(Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {

	
		List<Entry<String, Double>> sortList  = new ArrayList<Entry<String, Double>>(map.entrySet());
		Collections.sort(sortList , new Comparator<Map.Entry<String, Double>>() {

			public int compare(Entry<String, Double> one, Entry<String, Double> two) {
				return two.getValue().compareTo(one.getValue());
			}
		});
		context.write(new Text("highest"), new Text(" "));
		for (int i = 0; i < 3; i++) {
			Entry<String, Double> entry = sortList.get(i);
			context.write(new Text(entry.getKey()), new Text(entry.getValue() + " "));
		}
		context.write(new Text("lowest"), new Text(" "));

		for (int j = sortList.size() - 1; j > sortList.size() - 4; j--) {
			Entry<String, Double> entry = sortList.get(j);
			context.write(new Text(entry.getKey()), new Text(entry.getValue() + " "));
		}

	}
}