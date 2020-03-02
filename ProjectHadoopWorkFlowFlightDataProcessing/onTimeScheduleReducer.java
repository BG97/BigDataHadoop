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

public class OnTimeScheduleReducer extends Reducer<Text, Text, Text, Text> {
	private Map<String, Double> map = new TreeMap<String, Double>();

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		int total = 0;
		double onT = 0.0;	    
		Iterator<Text> line = values.iterator();
		while (line.hasNext()) {
			int time = Integer.parseInt(line.next().toString());
			onT = onT+time;
			total =total+1;
		}
		double result = onT/total;
		map.put(key.toString(), Double.valueOf(result));
	}
	
	@Override
	protected void cleanup(Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		
		List<Entry<String, Double>> sortList = new ArrayList<Entry<String, Double>>(map.entrySet());  
		Collections.sort(sortList,new Comparator<Map.Entry<String, Double>>() {  

            public int compare(Entry<String, Double> one, Entry<String, Double> two) {  
                return two.getValue().compareTo(one.getValue());  
            }  
        }); 
		context.write(new Text("Highest"), new Text(" "));
		for(int i= 0;i<3;i++){
		    
			Entry<String, Double> entry = sortList.get(i);
			context.write(new Text(entry.getKey()), new Text(entry.getValue()+" "));
		}
		context.write(new Text("lowest"), new Text(" "));
		for(int j = sortList.size() - 1; j > sortList.size() - 4; j--){
			Entry<String, Double> entry = sortList.get(j);
			context.write(new Text(entry.getKey()), new Text(entry.getValue()+ " "));
		}
	}
}