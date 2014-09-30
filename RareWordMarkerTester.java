import java.io.IOException;

/**
 * This program sends the original count file (renamed "ner_0.counts") and the
 * training data file to the function that checks for rare words and then
 * creates a new file with rare words replaced by "_RARE_"
 * 
 * @author Dhruv
 * 
 */
public class RareWordMarkerTester {

	public static void main(String[] args) throws IOException {
		HMMHelpers.rareWordMarker("ner_0.counts", "ner_train.dat");
	}
}
