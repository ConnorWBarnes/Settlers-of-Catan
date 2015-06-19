package soc.base.model;

/**
 * Represents a number token on a tile. Consists of a number and a letter.
 * @author Connor Barnes
 */
public class NumberToken {
    /**
     * The number values for each token in alphabetical order
     */
    public static final int[] NUMBERS = {5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11};

    private int number;
    private char letter;

    /**
     * Constructs a number token with the specified number and letter.
     * @param number the number on the number token
     * @param letter the letter on the number token
     */
    public NumberToken(int number, char letter) {
        this.number = number;
        this.letter = letter;
    }

    /**
     * Returns the number on this number token.
     * @return the number on this number token
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the letter on this number token.
     * @return the letter on this number token
     */
    public char getLetter() {
        return letter;
    }
}
