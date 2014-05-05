import java.util.Comparator;
import java.util.Map.Entry;


public class WordCloudComparator<K extends Comparable<? super K>, V extends Comparable<? super V>> 
	implements Comparator<Entry<K, V>> 
{
	public int compare(Entry<K, V> value1, Entry<K, V> value2) {
		return -1*(value1.getValue().compareTo(value2.getValue()));		//multiply by negative 1 so that they are sorted in descending order
	}
	
}
