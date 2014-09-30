import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * This class contains the various functions needed to produce the complete HMM
 * model.
 * 
 * @author Dhruv
 * 
 */
public class HMMHelpers {

	/**
	 * Calculates the emission parameters for each word. The parameters are
	 * organized in a tree map so that extracting the highest paramater value
	 * for a word (and therefore the most likely tag) occurs in O(1) time
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, TreeMap<Double, String>> eParamsCalculator(
			String fileName) throws IOException {

		HashMap<String, TreeMap<Double, String>> emissionParams = new HashMap<String, TreeMap<Double, String>>();
		FileReader in = new FileReader(fileName);
		BufferedReader br = new BufferedReader(in);

		HashMap<String, Integer> tagCounts = new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Integer>> wordToTagCount = new HashMap<String, HashMap<String, Integer>>();

		String input;
		String wordTag;
		String currentTag;
		String currentWord;

		StringTokenizer stk;
		while ((input = br.readLine()) != null) {

			stk = new StringTokenizer(input);
			int currentWordCount = Integer.parseInt(stk.nextToken());
			int currentTagCount = currentWordCount;
			wordTag = stk.nextToken();

			// System.out.println(input);
			if (wordTag.equals("WORDTAG")) {
				currentTag = stk.nextToken();
				currentWord = stk.nextToken();

				// Update tag counts
				if (tagCounts.containsKey(currentTag)) {
					currentTagCount += tagCounts.get(currentTag).intValue();
				}
				tagCounts.put(currentTag, new Integer(currentTagCount));

				// Update count for this word-tag combo
				if (!wordToTagCount.containsKey(currentWord)) {
					wordToTagCount.put(currentWord,
							new HashMap<String, Integer>());
				}
				wordToTagCount.get(currentWord).put(currentTag,
						new Integer(currentWordCount));
			}
		}

		for (String word : wordToTagCount.keySet()) {
			HashMap<String, Integer> currentMap = wordToTagCount.get(word);

			for (String tag : currentMap.keySet()) {
				int wordWithTagCount = currentMap.get(tag).intValue();
				int tagCount = tagCounts.get(tag).intValue();

				double currentEParam = ((double) wordWithTagCount) / tagCount;
				if (!emissionParams.containsKey(word)) {
					emissionParams.put(word, new TreeMap<Double, String>());
				}
				emissionParams.get(word).put(new Double(currentEParam), tag);
			}
		}

		for (String word : emissionParams.keySet()) {
			TreeMap<Double, String> currentTreeMap = emissionParams.get(word);
			for (Double d : currentTreeMap.keySet()) {
				// System.out.println(word + " " + currentTreeMap.get(d) + " "
				// + d.doubleValue());
			}
		}

		return emissionParams;
	}

	/**
	 * Marks rare words in the corpus by checking whether their count is less
	 * than 5.
	 * 
	 * @param countFile
	 * @param dataFile
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, Integer> rareWordMarker(String countFile,
			String dataFile) throws IOException {

		HashMap<String, Integer> wordToCount = new HashMap<String, Integer>();

		FileReader in = new FileReader(countFile);
		BufferedReader br = new BufferedReader(in);

		StringTokenizer stk;
		String input;
		String wordTag;
		String currentWord;

		/*
		 * Compute the word counts for each word
		 */
		while ((input = br.readLine()) != null) {
			stk = new StringTokenizer(input);
			int currentWordCount = Integer.parseInt(stk.nextToken());
			wordTag = stk.nextToken();

			if (wordTag.equals("WORDTAG")) {
				// System.out.println(input);
				stk.nextToken();
				currentWord = stk.nextToken();
				if (wordToCount.containsKey(currentWord)) {
					currentWordCount += wordToCount.get(currentWord).intValue();
				}
				wordToCount.put(currentWord, currentWordCount);
			}
		}

		in = new FileReader(dataFile);
		br = new BufferedReader(in);
		String tag;
		String write;

		File rareCounts = new File("ner_train_rare.dat");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		while ((input = br.readLine()) != null) {
			stk = new StringTokenizer(input);
			if (input.length() > 1) {
				currentWord = stk.nextToken();
				tag = stk.nextToken();
				if (wordToCount.get(currentWord).intValue() < 5) {
					currentWord = "_RARE_";
				}
				write = currentWord + " " + tag;
				// System.out.println(write);
				bufferedWriter.write(write);
				bufferedWriter.write("\n");
			} else {
				bufferedWriter.write("\n");
			}
		}
		bufferedWriter.close();

		return wordToCount;
	}

	/**
	 * Marks rare words in the corpus by checking whether their count is less
	 * than 5. Further it categorizes these rare words into buckets - words with
	 * all capitals, words with all numbers, words containing dashes, other
	 * words marked simply by "_RARE_" as before.
	 * 
	 * @param countFile
	 * @param dataFile
	 * @return
	 * @throws IOException
	 */
	public static void lowFrequencyTagger(String countFile, String dataFile)
			throws IOException {

		HashMap<String, Integer> wordToCount = new HashMap<String, Integer>();

		FileReader in = new FileReader(countFile);
		BufferedReader br = new BufferedReader(in);

		StringTokenizer stk;
		String input;
		String wordTag;
		String currentWord;

		/*
		 * Compute the word counts for each word
		 */
		while ((input = br.readLine()) != null) {
			stk = new StringTokenizer(input);
			int currentWordCount = Integer.parseInt(stk.nextToken());
			wordTag = stk.nextToken();

			if (wordTag.equals("WORDTAG")) {
				// System.out.println(input);
				stk.nextToken();
				currentWord = stk.nextToken();
				if (wordToCount.containsKey(currentWord)) {
					currentWordCount += wordToCount.get(currentWord).intValue();
				}
				wordToCount.put(currentWord, currentWordCount);
			}
		}

		in = new FileReader(dataFile);
		br = new BufferedReader(in);
		String tag;
		String write;

		File rareCounts = new File("ner_train_low_freq.dat");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		while ((input = br.readLine()) != null) {
			stk = new StringTokenizer(input);
			if (input.length() > 1) {
				currentWord = stk.nextToken();
				tag = stk.nextToken();
				if (wordToCount.get(currentWord).intValue() < 5) {
					if (allCaps(currentWord)) {
						currentWord = "_ALLCAPS_";
					} else if (allNumbers(currentWord)) {
						currentWord = "_NUMBERS_";
					} else if (currentWord.contains("-")) {
						currentWord = "_DASHED_";
					} else {
						currentWord = "_RARE_";
					}
				}
				write = currentWord + " " + tag;
				// System.out.println(write);
				bufferedWriter.write(write);
				bufferedWriter.write("\n");
			} else {
				bufferedWriter.write("\n");
			}
		}
		bufferedWriter.close();
	}

	private static boolean allCaps(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) < 65 || word.charAt(i) > 90)
				return false;
		}
		return true;
	}

	private static boolean allNumbers(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) < 48 || word.charAt(i) > 57)
				return false;
		}
		return true;
	}

	/**
	 * Creates a mapping for lines of the form <count> n-GRAM (y1...yn) from
	 * n-Gram (y1...yn) to <count>
	 * 
	 * @param countFile
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, Integer> nGramMapper(String countFile)
			throws IOException {

		HashMap<String, Integer> nGramMap = new HashMap<String, Integer>();
		FileReader in = new FileReader(countFile);
		BufferedReader br = new BufferedReader(in);

		String input;

		while ((input = br.readLine()) != null) {
			if (!input.contains("WORDTAG") && input.length() > 0) {
				int firstSpacePos = input.indexOf(' ');
				int nGramCount = Integer.parseInt(input.substring(0,
						firstSpacePos));
				String nGram = input.substring(firstSpacePos + 1);
				nGramMap.put(nGram, new Integer(nGramCount));
			}
		}

		return nGramMap;
	}

	/**
	 * This function generates the q parameters from its arguments by searching
	 * for count values in the nGramMap
	 * 
	 * @return
	 */
	public static double qParam(String u, String v, String w,
			HashMap<String, Integer> nGramMap) {

		String trigram = "3-GRAM" + " " + u + " " + v + " " + w;
		String bigram = "2-GRAM" + " " + u + " " + v;
		double trigramCount = 0.0;
		double bigramCount = 0.0;
		double result = 0.0;

		if (!nGramMap.containsKey(trigram)) {
			return 0;
		}

		trigramCount = nGramMap.get(trigram).doubleValue();
		bigramCount = nGramMap.get(bigram).doubleValue();
		result = trigramCount / bigramCount;

		return result;
	}

	/**
	 * Given a file of trigrams (each word separated by a space and each trigram
	 * by a newline), this method will print out the trigrams with their log(q)
	 * probabilities.
	 * 
	 * @param trigramFile
	 * @throws IOException
	 */
	public static void trigramProbabilityGenerator(String trigramFile)
			throws IOException {

		FileReader in = new FileReader(trigramFile);
		BufferedReader br = new BufferedReader(in);

		String trigramInput;
		StringTokenizer stk;
		String u, v, w;

		HashMap<String, Integer> mGramMap = nGramMapper("ner_1.counts");

		while ((trigramInput = br.readLine()) != null) {
			stk = new StringTokenizer(trigramInput);
			u = stk.nextToken();
			v = stk.nextToken();
			w = stk.nextToken();

			System.out.println("Log probability for trigram: " + trigramInput
					+ " is:" + Math.log(qParam(u, v, w, mGramMap))
					/ Math.log(2));
		}
	}
}
