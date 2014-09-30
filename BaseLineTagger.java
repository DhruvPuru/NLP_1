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
 * Here is the base-line tagger implementation for question 4. It makes use of
 * the emission params as well as word counts that are stored in a
 * String-->Integer hashmap
 * 
 * @author Dhruv
 * 
 */
public class BaseLineTagger {

	public static void tagger(String devDataFile, String countFile,
			String trainDataFile) throws IOException {

		HashMap<String, Integer> wordToCount = HMMHelpers.rareWordMarker(
				"ner_0.counts", trainDataFile);
		HashMap<String, TreeMap<Double, String>> emissionParams = HMMHelpers
				.eParamsCalculator(countFile);

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
			} else {
				bufferedWriter.write("\n");
			}
		}
		bufferedWriter.close();
	}

	public static void main(String[] args) throws IOException {
		tagger("ner_dev.dat", "ner.counts", "ner_train.dat");
	}
}
