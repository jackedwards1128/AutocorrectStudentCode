import org.junit.Test;
import org.junit.jupiter.api.Timeout;
import java.io.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AutocorrectTester {
    private Autocorrect studentSolution;
    private String[] dictionary, matches;
    private int threshold;
    private String typed;

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    public void testSmall() {
        setTestData(0);
        studentSolution = new Autocorrect(dictionary, threshold);
        assertArrayEquals(matches, studentSolution.runTest(typed), "Incorrect words returned.");
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    public void testMed() {
        setTestData(1);
        studentSolution = new Autocorrect(dictionary, threshold);
        assertArrayEquals(matches, studentSolution.runTest(typed), "Incorrect words returned.");
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    public void testLarger() {
        setTestData(2);
        studentSolution = new Autocorrect(dictionary, threshold);
        String[] words = studentSolution.runTest(typed);
        for (int i = 0; i < matches.length; i++) {
            boolean found = false;
            for (int j = 0; j < words.length; j++) {
                if (words[j].equals(matches[i])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(matches[i]);
            }
        }

        assertArrayEquals(matches, studentSolution.runTest(typed), "Incorrect words returned.");
    }

    private void setTestData(int test) {
        // Open files
        try {
            BufferedReader testReader = new BufferedReader(new FileReader("test_files/" + test + ".txt"));
            BufferedReader answerReader = new BufferedReader(new FileReader("test_files/" + test + "_answers.txt"));

            typed = testReader.readLine();
            threshold = Integer.parseInt(testReader.readLine());

            dictionary = loadWords(testReader);
            matches = loadWords(answerReader);

        } catch (IOException e) {
            System.out.println("Error opening test file " + test + ".txt");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader testReader = new BufferedReader(new FileReader("dictionaries/large.txt"));
        AutocorrectTester tester = new AutocorrectTester();
        String[] dictionary = tester.loadWords(testReader);

        Autocorrect autocorrect = new Autocorrect(dictionary, 2);

        while (1 == 1)
            autocorrect.prompt();
    }

    private String[] loadWords(BufferedReader br) {
        String line;
        try {
            line = br.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = br.readLine();
                words[i] = line;
            }
            return words;
        } catch (IOException e) {
            System.out.println("Error opening test file");
            e.printStackTrace();
        }
        return null;
    }
}