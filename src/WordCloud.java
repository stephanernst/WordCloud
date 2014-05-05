import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class WordCloud{
	public static void main(String[] args) {
		Scanner infile = null;
		Scanner filename = new Scanner(System.in);
        
		//Continue to poll user until a proper file name has been entered
        boolean filefound = false;
        while (!filefound) {
        	System.out.println("Enter name of stop words text file.");
    		File file = new File(filename.next());
	        try {
				infile = new Scanner(file);
				filefound = true;
			} catch (FileNotFoundException e) {
				System.out.println("The file specified was not found.");
			}
        }
        //Store all the stop words in a hash set
        Set<String> stopwords = CreateStopWordSet(infile);
        
        filefound = false;
        while (!filefound) {
        	System.out.println("Enter name of text file to be turned into word cloud.");
    		File file = new File(filename.next());
	        try {
				infile = new Scanner(file);
				filefound = true;
			} catch (FileNotFoundException e) {
				System.out.println("The file specified was not found.");
			}
        }
        
        //create the word cloud and then sort it by frequency of the words (descending order)
        Map<String, Integer> wordcloud = CreateWordCloud(infile, stopwords);
        Map<String, Integer> sortedcloud = sortByFrequency(wordcloud);
        
        //output the top 10 words used in the text
        System.out.println("The 10 most commonly occuring words are:");
        int i = 0;
        for(Entry<String, Integer> s : sortedcloud.entrySet()) {
        	if (i==10)
        		break;
        	System.out.println(s);
        	i++;
        }  
        
        //sort the word cloud alphabetically this time and output the html file
        sortedcloud = sortAlphabetical(wordcloud);
        try {
			WordCloudHTML(sortedcloud);
		} catch (IOException e) {
			System.out.println("The file could not be created.");
		}
        
        filename.close();
	}
	
	//Compiles all stop words from a Scanner object into a set of strings
	//returns a set of strings
	private static Set<String> CreateStopWordSet(Scanner file) {
		Set<String> stopwords = new HashSet<String>();
		if (file == null) {
			return stopwords;
		}
		while(file.hasNext()) {		//while the scanner object still has words to add continue doing so
			stopwords.add(file.next());
		}
		return stopwords;
	}
	
	//Creates a hash map consisting of words as the key and their number of occurences as the value
	//Any of the words in the stop words set will not be added to the word cloud (hash map)
	//returns a hash map consisting of only words and their frequency (all non-ascii characters as well as punctuation is stripped)
	private static Map<String, Integer> CreateWordCloud(Scanner file, Set<String> stopwords) {
		Map<String, Integer> wordcloud = new HashMap<String, Integer>();
		if (file == null) {
			return wordcloud;
		}
		while(file.hasNext()) {
			String word = file.next();
			word = word.replaceAll("[^\\x00-\\x7F]", "");				//strip all non-ascii characters from the string
			word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();		//strip all punctuation
			if (word.length() < 3) {									//do not include words that are less than 3 characters long
				continue;
			} else if (stopwords.contains(word)) {						//do not add words to the hash map that are found in the stop words set
				continue;
			} else if (wordcloud.containsKey(word)) {					//if word is already in hash map then increments its value
				int count = wordcloud.get(word);
				wordcloud.put(word, count+1);
			} else {
				wordcloud.put(word, 1);									//add the word to the wordcloud
			}
		}
		return wordcloud;
	}
	
	//Takes in a map and orders it in a list in descending order based on its value
	//returns a linked hash map
	public static <K extends Comparable<? super K>,V extends Comparable<? super V>> 
		Map<K, V> sortByFrequency(Map<K, V> map) {
	        List<Entry<K, V>> wordlist = new LinkedList<Entry<K, V>>(map.entrySet());
	        
	        Collections.sort(wordlist, new WordCloudComparator<K, V>());		//sort in descending order
	        
	        Map<K, V> sortedmap = new LinkedHashMap<K, V>();
	      
	        for(Entry<K,V> word: wordlist){										//add all words in list into linked hash map in sorted order
	            sortedmap.put(word.getKey(), word.getValue());
	        }
	      
	        return sortedmap;
	}
	
	//takes in a map reorders it in a treemap
	//returns a tree map
	public static <K extends Comparable<? super K>,V extends Comparable<? super V>> 
		Map<K, V> sortAlphabetical(Map<K, V> map) {
	        List<Entry<K, V>> wordlist = new LinkedList<Entry<K, V>>(map.entrySet());		//temporarily store map in a list
	        
	        Map<K, V> sortedmap = new TreeMap<K, V>();										//use list to generate the tree map
	      
	        for(Entry<K,V> word: wordlist){
	            sortedmap.put(word.getKey(), word.getValue());
	        }
	      
	        return sortedmap;
	}
	
	//outputs a word cloud to an html file called wordcloud.html
	static void WordCloudHTML(Map<String, Integer> map) throws IOException {
		String header = "<style type=\"text/css\">\n" +
						".smallestTag {font-size: xx-small;}\n" + 
						".smallTag {font-size: small;}\n" +
						".mediumTag {font-size: medium;}\n" +
						".largeTag {font-size: large;}\n" +
						".largestTag {font-size: xx-large;}\n" +
						"</style>\n";
		
		String smallestTag = "<span class=\"smallestTag\">";
		String smallTag = "<span class=\"smallTag\">";
		String mediumTag = "<span class=\"mediumTag\">";
		String largeTag = "<span class=\"largeTag\">";
		String largestTag = "<span class=\"largestTag\">";
		String span = "</span> ";
		
		int maxcount = 0;
		int mincount = 0;
		
		//determine the max and min frequency of words used in the text
		boolean firstentry = true;
		for (Map.Entry<String, Integer> word : map.entrySet()) {
			if (firstentry == true) {
				maxcount = word.getValue();
				mincount = word.getValue();
				firstentry = false;
			}
			if (word.getValue() > maxcount) {
				maxcount = word.getValue();
			}
			if (word.getValue() < mincount) {
				mincount = word.getValue();
			}
		}
		
		//create the output file
		File output = new File("wordcloud.html");
		output.createNewFile();
		
		//write the header to the file
		FileWriter fw = new FileWriter(output.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(header);
		
		//depending on its frequency relative to all the other word frequencies the word will be printed in a different sized font
		int maxfont = 10;
		for (Map.Entry<String, Integer> word : map.entrySet()) {
			int fontvalue = maxfont*(word.getValue()-mincount)/(maxcount - mincount);		//formula used to obtain the words relative importance
			
			//depending upon the relative importance of the word it will be printed accordingly
			if (fontvalue <= 3) {
				bw.write(smallestTag+word.getKey()+span);
			} else if (fontvalue <= 5) {
				bw.write(smallTag+word.getKey()+span);
			} else if (fontvalue <= 7) {
				bw.write(mediumTag+word.getKey()+span);
			} else if (fontvalue <= 9) {
				bw.write(largeTag+word.getKey()+span);
			} else {
				bw.write(largestTag+word.getKey()+span);
			}
		}
		bw.close();
	}
}
