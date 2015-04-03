/**
 * Represents a number token on a tile. Consists of a number and a letter.
 * @author Connor Barnes
 */
public class NumberToken {
    private int number;
    private char letter;

    /**
     * Constructs a number token with the specified number and letter.
     * @param inNumber the number on the number token
     * @param inLetter the letter on the number token
     */
    public NumberToken(int inNumber, char inLetter) {
        number = inNumber;
        letter = inLetter;
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
