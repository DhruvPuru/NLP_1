import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * This class represents the tagger that makes use of the Viterbi algorithm
 * 
 * @author Dhruv
 * 
 */
public class ViterbiTagger {

	public static void viterbiTagger(String devDataFile, String countFile, String resultFile) throws IOException {

		FileReader in = new FileReader(devDataFile);
		BufferedReader br = new BufferedReader(in);

		// Store results from Viterbi tagging in a new file.
		File taggedResults = new File(resultFile);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				taggedResults));

		String input;
		double maxProb = 0.0;
		String maxTag = "";
		String currentWord;
		ArrayList<String> sentenceArrayList = new ArrayList<String>();

		while ((input = br.readLine()) != null) {
			String sentence = "";
			sentenceArrayList = new ArrayList<String>();
			while (input.length() > 0) {
				sentence += input + " ";
				sentenceArrayList.add(input);
				input = br.readLine();
			}
			HashMap<Integer, HashMap<String, Double>> sentencePiTable = piTableGenerator(sentenceArrayList, countFile);
			for (int i = 0; i < sentenceArrayList.size(); i++) {
				maxProb = 0.0;
				maxTag = "";
				currentWord = sentenceArrayList.get(i);
				HashMap<String, Double> map = sentencePiTable.get((i + 1));
				for (String s : map.keySet()) {
					if (map.get(s).doubleValue() > maxProb) {
						maxProb = map.get(s).doubleValue();
						maxTag = s.substring(s.indexOf(' '));
					}
				}
				String write = currentWord + maxTag + " " + Math.log(maxProb)/Math.log(2) + "\n";
				bufferedWriter.write(write);
			}
			bufferedWriter.write("\n");
		}

		br.close();
		bufferedWriter.close();
	}

	/**
	 * This function generates a pi-table for any given sentence. Looking up the
	 * pi values is then a simple lookup through the table. Creating the table
	 * takes o(nK^3) time.
	 * 
	 * @param sentenceArrayList
	 * @return
	 * @throws IOException
	 */
	public static HashMap<Integer, HashMap<String, Double>> piTableGenerator(
			ArrayList<String> sentenceArrayList, String countFile) throws IOException {

		HashMap<Integer, HashMap<String, Double>> piValues = new HashMap<Integer, HashMap<String, Double>>();
		piFiller(sentenceArrayList, piValues, countFile);
		return piValues;
	}

	/**
	 * This function fills in the actual table values, starting with the base
	 * case pi(0, *, *) and using it to computer pi(1)...pi(n)
	 * 
	 * @param sentenceArrayList
	 * @param piValues
	 * @throws IOException
	 */
	private static void piFiller(ArrayList<String> sentenceArrayList,
			HashMap<Integer, HashMap<String, Double>> piValues, String countFile)
			throws IOException {

		HashMap<String, TreeMap<Double, String>> emissionParams = HMMHelpers
				.eParamsCalculator(countFile);

		HashMap<String, Integer> nGramMap = HMMHelpers
				.nGramMapper(countFile);

		HashMap<String, Integer> wordToCount = HMMHelpers.rareWordMarker(
				"ner_0.counts", "ner_train.dat");

		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("* *", new Double(1));
		piValues.put(new Integer(0), map);

		String w = "";
		String u = "";
		String v = "";
		String currentWord = "";
		String prevWord = "";
		String prevPrevWord = "";
		double bPiValue;

		for (int i = 1; i <= sentenceArrayList.size(); i++) {
			currentWord = sentenceArrayList.get(i - 1);
			map = new HashMap<String, Double>();

			if (!wordToCount.containsKey(currentWord)) {
				currentWord = "_RARE_";
			} else if (wordToCount.get(currentWord) < 5) {
				currentWord = "_RARE_";
			}

			for (Double v1 : emissionParams.get(currentWord).keySet()) {

				v = emissionParams.get(currentWord).get(v1);
				if (!prevWord.equals("")) {
					for (Double v2 : emissionParams.get(prevWord).keySet()) {
						u = emissionParams.get(prevWord).get(v2);
						String bigram = u + " " + v;
						bPiValue = 0.0;

						// If second word in the sentence
						if (prevPrevWord.equals("")) {
							w = "*";
							bPiValue = piValues.get(new Integer(i - 1))
									.get(w + " " + u).doubleValue()
									* HMMHelpers.qParam(w, u, v, nGramMap) * v1;
						} // Else if word 3...n in the sentence
						else {
							bPiValue = 0.0;
							double max;
							for (Double v3 : emissionParams.get(prevPrevWord)
									.keySet()) {
								w = emissionParams.get(prevPrevWord).get(v3);
								max = piValues.get(new Integer(i - 1))
										.get(w + " " + u).doubleValue()
										* HMMHelpers.qParam(w, u, v, nGramMap)
										* v1;
								if (max > bPiValue) {
									bPiValue = max;
								}
							}
						}
						map.put(bigram, bPiValue);
						piValues.put(new Integer(i), map);
					}
					// Else - first word in the sentence
				} else {
					u = "*";
					w = "*";
					String bigram = u + " " + v;
					bPiValue = piValues.get(new Integer(i - 1))
							.get(w + " " + u).doubleValue()
							* HMMHelpers.qParam(w, u, v, nGramMap) * v1;
					map.put(bigram, bPiValue);
					piValues.put(new Integer(i), map);
				}
			}

			prevPrevWord = prevWord;
			prevWord = currentWord;
		}
	}

	public static void main(String[] args) throws IOException {
		viterbiTagger("ner_dev.dat", "ner_1.counts", "dev_results_viterbi.dat");
	}
}
