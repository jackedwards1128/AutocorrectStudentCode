import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author JACK EDWARDS
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */

    private String[] dictionary;
    private ArrayList<Integer>[] associativeBigram;
    private TST dictionaryTST;
    private int threshold;
    private final static int WORDPAIRCOUNT = 676;

    public Autocorrect(String[] words, int threshold) {
        // Initialize dictionary objects
        this.dictionary = words;
        this.dictionaryTST = new TST();
        for (int i = 0; i < dictionary.length; i++) {
            dictionaryTST.insert(dictionary[i]);
        }

        // Threshold for how "far-away" of a potential word we consider as a replacement
        this.threshold = threshold;

        // Create array structure which stores all the words in the dictionary that contain
        // a certain 2-letter sequence (bigram)
        associativeBigram = new ArrayList[WORDPAIRCOUNT];
        for (int i = 0; i < WORDPAIRCOUNT; i++) {
            String bigram = "" + (char)('a' + (i / 26)) + (char)('a' + (i % 26));
            associativeBigram[i] = new ArrayList<>();
            for (int j = 0; j < dictionary.length; j++) {
                if (dictionary[j].contains(bigram)) {
                    associativeBigram[i].add(j);
                }
            }

        }
    }

    // Gathers user-inputted word and returns replacements for word if it is not in
    // the dictionary
    public void prompt() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a word: ");

        String input = scanner.nextLine();

        System.out.println("You typed: " + input);

        // If typed word is in dictionary, no autocorrect needed
        if (dictionaryTST.search(input))
            return;

        // Create sorted list of viable words with runTest()
        String[] options = runTest(input);

        System.out.println("Did you mean...");

        // Notify user if no words are within edit-distance threshold
        if (options.length == 0)
            System.out.println("No matches found :(");

        // Present options to user
        for (int i = 0; i < Math.min(3, options.length); i++) {
            System.out.println(options[i]);
        }

        System.out.println("~~~~~~~~~~");
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        // Create set to contain all words that are close enough to the mistyped word to be considered
        // Hashset used to prevent addition of duplicates
        ArrayList<String> viableWords = new ArrayList<>();
        HashSet<String> viableWordsHashed = new HashSet<>();

        // If the mistyped word is four letters or shorter, simply consider words of a similar length
        // This is because with shorter words, the limitations of bigrams are magnified
        if (typed.length() <= 4) {
            for (int i = 0; i < dictionary.length; i++) {
                if(dictionary[i].length() <= typed.length() + threshold) {
                    viableWords.add(dictionary[i]);
                }
            }
        } else {
            // Otherwise, consider every word that shares a bigram with the mistyped word
            for (int i = 0; i < typed.length() - 1; i++) {
                char letter1 = typed.charAt(i);
                char letter2 = typed.charAt(i+1);

                int wordPairHash = ((letter1 - 'a') * 26) + (letter2 - 'a');

                ArrayList<Integer> matches = associativeBigram[wordPairHash];
                for(int j = 0; j < matches.size(); j++) {
                    // Check whether word already exists in Hashset (constant lookup time) to prevent
                    // duplicates from being added
                    if (!viableWordsHashed.contains(dictionary[matches.get(j)])) {
                        viableWords.add(dictionary[matches.get(j)]);
                        viableWordsHashed.add(dictionary[matches.get(j)]);
                    }
                }
            }
        }

        // Create list of string-integer pairs to store each word and its edit distance from the mistyped word
        List<WordDistancePair> orderedWords = new ArrayList<>();

        // Parse viable words for words that are within the edit-distance threshold
        for(int i = 0; i < viableWords.size(); i++) {
            int editDistance = editDistance(typed, viableWords.get(i));

            if (editDistance <= threshold)
                orderedWords.add(new WordDistancePair(editDistance, viableWords.get(i)));
        }

        // Utilize built-in sorting functions to sort viable words by edit-distance/alphabetically
        orderedWords.sort(Comparator.comparing(WordDistancePair::getEditDistance)
                .thenComparing(WordDistancePair::getWord));

        // Convert to array
        String[] finalOrderedArray = new String[orderedWords.size()];

        for(int i = 0; i < orderedWords.size(); i++) {
            finalOrderedArray[i] = orderedWords.get(i).getWord();
        }

        return finalOrderedArray;
    }

    // Finds the minimum amount of edits needed to go from one string to another, by utilizing additions,
    // deletions, and substitutions
    public int editDistance(String word1, String word2) {
        // Tabulation approach where each axis of the 2D table represents substring versions of the original
        // strings
        int table[][] = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 1; i < word1.length() + 1; i++) {
            table[i][0] = i;
        }
        for (int i = 1; i < word2.length() + 1; i++) {
            table[0][i] = i;
        }

        for (int i = 1; i < word1.length() + 1; i++) {
            for (int j = 1; j < word2.length() + 1; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1];
                    // If the last letter of two words is equal, those words have the same edit distance as if
                    // that last letter wasn't there.
                } else
                    // Find minimum edit-distance way of getting to a string through either an addition, deletion
                    // or substitution
                    table[i][j] = Math.min(table[i - 1][j - 1], Math.min(table[i][j - 1], table[i - 1][j])) + 1;
            }
        }


        return table[word1.length()][word2.length()];
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}