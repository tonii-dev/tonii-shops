package io.github.toniidev.toniishops.utils;

import org.bukkit.Location;

import java.util.*;

public class StringUtils {

    /**
     * The generated serial codes, we make it so that's impossible to generate
     * the serial code twice. It's nearly impossible that a serial code gets
     * generated twice, but we make it even less possible
     * TODO: Store this in a file, so that every time the plugin gets enabled or disabled this set doesn't reset
     */
    private static final Set<String> generatedSerials = new HashSet<>();

    /**
     * Tells whether the given material name contains the specified field.
     * For example, let's suppose to call this function like this: doesMaterialContainField(Material.OAK_LOG, "log")
     * I consider a "material name field" every object that is in the Array obtained with material.name().split("_").
     * Material.OAK_LOG contains 2 fields: "oak" and "log". So, because one of the fields is equal to the given field
     * to search for, this function will return true.
     *
     * @param materialName The name of the Material we must check whether contains the specified field or not
     * @param field        The field to know whether is contained in the material name or not
     * @return true if the name of the specified material contains the specified field,
     * false if the name of the specified material doesn't contain the specified field
     */
    public static boolean doesMaterialNameContainField(String materialName, String field) {
        return Arrays.stream(materialName.toLowerCase().split("_"))
                .filter(x -> x.equals(field.toLowerCase()))
                .findFirst().orElse(null) != null;
    }

    /**
     * doesMaterialNameContainField() made compatible with checking Strings with multiple fields, for example:
     * doesMaterialContainField(Material.OAK_LOG, "oak_log"). It is different between String#contains() because
     * String#contains does not split the string considering "_".
     *
     * @param materialName The name of the Material we must check whether contains the specified string fields or not
     * @param string       The string to know whether is contained in the material name or not
     * @return true if the name of the specified material contains the specified string,
     * false if the name of the specified material doesn't contain the specified string
     */
    public static boolean doesMaterialNameContainString(String materialName, String string) {
        for (String s : string.split("_")) {
            if (!doesMaterialNameContainField(materialName, s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Edits the given string, so that the colorPrefix gets replaced with the character
     * that in Minecraft must be placed before any color code to give a color to a String
     *
     * @param colorPrefix The character that is in the String and has to be replaced with '§'
     * @param string      The string where the character has to be replaced
     * @return string#replace(% colorPrefix %, ' § ')
     */
    public static String formatColorCodes(char colorPrefix, String string) {
        return string.replace(colorPrefix, '§');
    }

    /**
     * Generates a section of a serial code
     *
     * @param length The length of the section that has to be generated
     * @return A serial code section
     */
    private static String generateSerialCodeSection(int length) {
        /// The serial code can contain uppercase and lowercase letters, and numbers
        final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder section = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            section.append(CHAR_POOL.charAt(index));
        }
        return section.toString();
    }

    /**
     * Generates an unique serial code
     *
     * @param sectionLength The length of each section of the serial code
     * @param sections      The number of sections of this serial code. Every section is split by the given char
     * @param split         The character between a section and another
     * @return A unique serial code made of a specified number of sections, every of which is made of the specified number of characters
     */
    public static String generateSerialCode(int sectionLength, int sections, char split) {
        String serialCode = "";
        do {
            /// Generate %sections% sections
            final List<String> generatedSections = new ArrayList<>();
            for (int i = 0; i < sections; i++) {
                generatedSections.add(generateSerialCodeSection(sectionLength));
            }

            /// Adds every section to the serialCode
            for (int i = 0; i < generatedSections.size(); i++) {
                serialCode += generatedSections.get(i);
                if (i < generatedSections.size() - 1) serialCode += split;
            }

        } while (generatedSerials.contains(serialCode)); /// Ensure it's unique

        /// Add the generated serial code to the set to avoid future duplicates
        generatedSerials.add(serialCode);
        return serialCode;
    }

    public static String convertLocation(Location location, char split) {
        return String.valueOf(location.getX() + split + location.getY() + split + location.getZ());
    }

    public static String convertLocation(Location location, char split, char splitsColor) {
        return StringUtils.formatColorCodes('&', "&f" + location.getX() + "&" + splitsColor + split + " &f" + location.getY() + "&" + splitsColor + split + " &f" + location.getZ());
    }

    public static String convertLocation(Location location, char split, char splitsColor, char coordinatesColor) {
        return StringUtils.formatColorCodes('&', "&" + coordinatesColor + Math.round(location.getX()) + "&" + splitsColor + split + " &" + coordinatesColor + Math.round(location.getY()) + "&" + splitsColor + split + " &" + coordinatesColor + Math.round(location.getZ()));
    }


    /**
     * Computes the affinity between two strings based on their Levenshtein distance.
     * A smaller distance indicates a higher affinity.
     *
     * @param string1 the first string
     * @param string2 the second string
     * @return the Levenshtein distance between the two strings;
     *         returns -1 if either string is null
     */
    public static int getAffinity(String string1, String string2) {
        if (string1 == null || string2 == null) {
            return -1; // Handle null inputs
        }

        int len1 = string1.length();
        int len2 = string2.length();

        // Initialize a matrix to store distances
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Fill the base cases
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i; // Cost of deleting characters from string1
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j; // Cost of inserting characters into string1
        }

        // Fill the DP table
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (string1.charAt(i - 1) == string2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No cost if characters match
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1], // Substitution
                            Math.min(
                                    dp[i - 1][j],   // Deletion
                                    dp[i][j - 1]    // Insertion
                            )
                    );
                }
            }
        }

        return dp[len1][len2]; // The bottom-right corner contains the result
    }

    /**
     * Finds the string in a list that has the highest affinity (lowest Levenshtein distance)
     * with the given string.
     *
     * @param target the string to compare against
     * @param candidates the list of strings to search
     * @return the string from the list that has the highest affinity with the target;
     *         returns null if the list is empty or null
     */
    public static String findMostAffineString(String target, List<String> candidates) {
        if (target == null || candidates == null || candidates.isEmpty()) {
            return null; // Handle null or empty inputs
        }

        String mostAffine = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int distance = getAffinity(target, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                mostAffine = candidate;
            }
        }

        return mostAffine;
    }
}
