public class TSTNode {


    // Instance variables to store whether the node represents a real word, the letter it holds, and its three children
    private boolean isWord;
    private char letter;
    private TSTNode[] children;


    public TSTNode(char letter) {
        this.letter = letter;
        children = new TSTNode[3];
    }


    // Returns whether the node represents a real word
    public boolean getIsWord() {
        return isWord;
    }


    public TSTNode getChild(int index) {
        return children[index];
    }


    public void setWord() {
        isWord = true;
    }


    public char getLetter() {
        return  letter;
    }


    public void setChild(int index, char letter) {
        children[index] = new TSTNode(letter);
    }




}

