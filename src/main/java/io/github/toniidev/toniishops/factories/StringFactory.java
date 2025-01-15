package io.github.toniidev.toniishops.factories;

/**
 * Class used any time we need to create a string with multiple colors
 */
public class StringFactory {
    private String result;
    private String last;

    /**
     * Creates a blank StringFactory instance
     */
    public StringFactory(){}

    /**
     * Creates a StringFactory instance starting from a String.
     * The text coming from this StringFactory instance will be added to the initialString one
     * @param initialString The String that this StringFactory will add the text to
     */
    public StringFactory(String initialString){
        result = initialString;
    }

    /**
     * Adds some text to the string that will be got with this#get().
     * It can be used along with this#setColor() to set a custom color code,
     * else it will be written in white
     * @param string The text that has to be added to the string
     * @return This StringFactory instance
     */
    public StringFactory append(String string){
        /*
          The "last" string is added to the String that is being built only
          when this#setColor() is called. If it doesn't get called, as written in the comment,
          the "last" string will be written in white. In this "if" statement, we are also
          adding the "last" string to the String that is being built, because if it doesn't
          start with "§" it means that this#setColor() has never being called on this string,
          so it hasn't been added.
         */
        if(this.last != null && !this.last.startsWith("§")){
            last = "§r§f" + last;
            addLast();
        }

        this.last = string;
        return this;
    }

    /**
     * Sets the color of the "last" string. The "last" string is the last string that
     * has been appended to this factory.
     * @param colorCode The code of the color that the last string that has been appended has to have.
     *                  Color codes:
     *                  0 black, 1 dark blue, 2 dark green,
     *                  3 dark aqua, 4 dark red, 5 dark purple,
     *                  6 gold, 7 gray, 8 dark gray,
     *                  9 blue, a green, b aqua,
     *                  c red, d light purple, e yellow
     *                  f white, k encrypted, l bold
     *                  m strikethrough, n underline, o italic
     * @return This StringFactory instance
     */
    public StringFactory setColor(char colorCode){
        last = "§" + colorCode + last;
        addLast();
        return this;
    }

    /**
     * Adds the String that is being now being built to the String that will be given
     * at the end of the process with this#get()
     */
    private void addLast(){
        if(this.result == null) result = last;
        else result += " " + last;
    }

    /**
     * Finally, gets the created string
     * @return The String that this StringFactory instance has created
     */
    public String get(){
        return result;
    }
}
