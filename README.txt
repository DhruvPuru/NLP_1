README.txt

Name: Dhruv Purushottam
UNI: dp2631
Natural Language Processing (FAll 2014): Assignment 1

Part 1: Instructions

All code is written in Java. I created different main methods that run and test each question and sub-question.

1. Compile all java files. 
2. Run "java RareWordMarkerTester" to see function mark words with count < 5 as "_RARE_". Creates new file with rare words called 
"ner_train_rare.dat". Python counter can be run on this to generate a new count file. I called this file "ner_1.counts".
3. Run "java BaseLineTagger" to see the base line tagger tag words based on e(x|y) params and store results in "dev_results.dat"
Can then run python script on this file to evaluate results.
4. The q(v | w, u) function can be found in the HMMHelpers.java class along with most other functionality that the HMM uses. There is also
a method trigramProbabilityGenerator(String trigramFile) that takes in a file with a list of trigrams and outputs their q params, as per the
questions specfications. However, I did not use this function in any later implementations.
5. Run "java ViterbiTagger" to tag the development data words using the Viterbi algorithm. Results are stored in file called "dev_results_viterbi.dat"
One can run the python evaluater with this file to see its performance.
6. Run "java LowFreqTaggerTester" to put low-frequency words within certain buckets - all caps, all numbers, contains dashes and generic "_RARE_".
Results are stored in "ner_train_low_freq.dat". Python counter can be run on this to generate new count file. I called this "ner_low_freq.counts"
7. Run "java LowFreqViterbiTester" to test the viterbi model with buckets. Results stored in "dev_results_low_freq.dat"

NOTE: 3 will not work without the count file generated in 2. 7 will not work without the count file generated in 6. The count file names have been hard-coded
so it's best to store the counts in files named the same. I have included the generated count files in the submission. To test that the generation works, feel
free to delete them. Starting from step 1 above, only the java files, ner_0.counts, python files, and initial data files are required.

Part 2: function performance and observations

Base-line tagger performance: 

Found 14043 NEs. Expected 5931 NEs; Correct: 3117.

         precision      recall          F1-Score
Total:   0.221961       0.525544        0.312106
PER:     0.435451       0.231230        0.302061
ORG:     0.475936       0.399103        0.434146
LOC:     0.147750       0.870229        0.252612
MISC:    0.491689       0.610206        0.544574

Viterbi tagger performance: 

Found 4657 NEs. Expected 5931 NEs; Correct: 3145.

         precision      recall          F1-Score
Total:   0.675327       0.530265        0.594069
PER:     0.538406       0.404244        0.461778
ORG:     0.539554       0.397608        0.457831
LOC:     0.850667       0.695747        0.765447
MISC:    0.750948       0.644951        0.693925

We see a significant improvement using the Viterbi model

Viterbi with low-frequency buckets: 

Found 4802 NEs. Expected 5931 NEs; Correct: 3147.

         precision      recall          F1-Score
Total:   0.655352       0.530602        0.586416
PER:     0.488220       0.405876        0.443256
ORG:     0.539796       0.395366        0.456428
LOC:     0.850299       0.696838        0.765957
MISC:    0.750948       0.644951        0.693925

Here the buckets don't seem to have helped the Viterbi algorithm very much, probably due to too few buckets or
the fact that several words were still marked "_RARE_"