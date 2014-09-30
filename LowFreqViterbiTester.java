import java.io.IOException;


public class LowFreqViterbiTester {

	public static void main(String[] args) throws IOException {
		ViterbiTagger.viterbiTagger("ner_dev.dat", "ner_low_freq.counts", "dev_results_low_freq.dat");
	}
}
