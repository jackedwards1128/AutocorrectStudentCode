public class WordDistancePair {

    private int editDistance;
    private String word;

    public WordDistancePair(int ed, String str) {
        editDistance = ed;
        word = str;
    }

    public int getEditDistance() {
        return editDistance;
    }

    public String getWord() {
        return word;
    }
}
