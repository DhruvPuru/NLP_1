import java.io.IOException;

public class LowFreqTaggerTester {
	public static void main(String[] args) throws IOException {
		HMMHelpers.lowFrequencyTagger("ner_0.counts", "ner_train.dat");
	}
}
