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

public class HMM {

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

	public static HashMap<String, Integer> rareCounter(String countFile,
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

	public static void tagger(String devDataFile, String countFile,
			String trainDataFile) throws IOException {

		HashMap<String, TreeMap<Double, String>> emissionParams = eParamsCalculator(countFile);
		HashMap<String, Integer> wordToCount = rareCounter("ner_0.counts",
				trainDataFile);

		FileReader in = new FileReader(devDataFile);
		BufferedReader br = new BufferedReader(in);

		File rareCounts = new File("dev_results.dat");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		String input;
		double logP;
		String maxTag;

		while ((input = br.readLine()) != null) {
			if (input.length() > 0) {
				if (wordToCount.containsKey(input)) {
					if (wordToCount.get(input).intValue() < 5) {
						logP = Math.log(emissionParams.get("_RARE_").lastKey()
								.doubleValue())
								/ Math.log(2);
						maxTag = emissionParams.get("_RARE_").lastEntry()
								.getValue();
					} else {
						logP = Math.log(emissionParams.get(input).lastKey()
								.doubleValue())
								/ Math.log(2);
						maxTag = emissionParams.get(input).lastEntry()
								.getValue();
					}
				} else {
					logP = Math.log(emissionParams.get("_RARE_").lastKey()
							.doubleValue())
							/ Math.log(2);
					maxTag = emissionParams.get("_RARE_").lastEntry()
							.getValue();
				}
				bufferedWriter.write(input + " " + maxTag + " " + logP + "\n");
			}
			else {
				bufferedWriter.write("\n");
			}
		}
		bufferedWriter.close();

	}

	public static void main(String[] args) throws IOException {
		tagger("ner_dev.dat", "ner.counts", "ner_train.dat");
	}
}
