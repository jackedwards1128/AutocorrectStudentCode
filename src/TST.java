public class TST {


    private TSTNode root;


    // Initialize the root node at the letter m, because m is in the center of the alphabet (shared with n), so when
    // a word is being searched, it will reach the target node faster since the m splits two of the three cases
    // (before letter & after letter) equally.
    public TST() {
        root = new TSTNode('m');
    }


    // Search through the TST for a given word and returns whether that word exists within the structure or not
    public boolean search(String word) {


        // Define variable to store the "location" within the tree that we're at
        TSTNode position = root;
        // For every letter in the given word
        for(int i = 0; i < word.length();) {


            char letter = word.charAt(i);


            char positions_letter = position.getLetter();


            // If the selected letter of the given word is equal to the letter of the node being looked at, go down
            // since we are in the right place
            if(letter == positions_letter) {
                position = position.getChild(1);


                // If there isn't anything left in the TST, our given word isn't a word
                if (position == null)
                    return false;


                // Increment the loop-variable to move forward onto the next letter in the given word
                i++;
                continue;
            }


            // Move left/right depending on whether the current letter is less than/greater than the current selected
            // node
            if (letter - positions_letter > 0) {
                position = position.getChild(2);
            } else {
                position = position.getChild(0);
            }


            // If there isn't anything left in the TST, our given word isn't a word
            if (position == null)
                return false;
        }
        // Once we finish out moving along the letter-chain, return the word-ness of the final node we arrived at
        return position.getIsWord();
    }


    // Inserts a given word into the TST
    public void insert(String word) {
        // Define variable to store the "location" within the tree that we're at
        TSTNode position = root;


        // For every letter in the given word
        for(int i = 0; i < word.length();) {


            char letter = word.charAt(i);
            char positions_letter = position.getLetter();


            // If the selected letter of the given word is equal to the letter of the node being looked at, go down
            // since we are in the right place
            if(letter == positions_letter) {


                // If there is nothing left at the spot in the TST, make new nodes to represent the current letter
                // of the given word
                if (position.getChild(1) == null)
                    position.setChild(1, letter);


                position = position.getChild(1);


                // Increment the loop-variable to move forward onto the next letter in the given word
                i++;
                continue;
            }


            // Move left/right depending on whether the current letter is less than/greater than the current selected
            // node, and if there isn't anything there, make a new node to represent the selected letter of the word
            if (letter - positions_letter > 0) {
                if (position.getChild(2) == null)
                    position.setChild(2, letter);


                position = position.getChild(2);
            } else {
                if (position.getChild(0) == null)
                    position.setChild(0, letter);


                position = position.getChild(0);
            }
        }


        // Once we reach the final spot, set the word-ness of our final node to be true (because we just inputted
        // real word)
        position.setWord();
    }
}













