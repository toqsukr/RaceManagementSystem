package util;

/***
 * This class checks the entered string for correctness
 */

public class Validation {
    /***
     * 
     * @param input name passed
     * @return true if the correct name is passed else false
     */
    public static boolean isValidName(String input) {
        return input.matches("^([А-Яа-яA-Za-z]{2,20})(\\s[А-Яа-яA-Za-z]{0,30})?$");
    }

    /***
     * 
     * @param input age passed
     * @return true if the correct age is passed else false
     */

    public static boolean isValidAge(String input) {
        return input.matches("^[1-9]\\d?$");
    }

    /***
     * 
     * @param input team passed
     * @return true if the correct team is passed else false
     */

    public static boolean isValidTeam(String input) {
        return input.matches("^[А-Яа-я\\w\\s]{3,20}$");
    }

    /***
     * 
     * @param input point passed
     * @return true if the correct point is passed else false
     */

    public static boolean isValidPoint(String input) {
        return input.matches("^0|([1-9]\\d{0,5})$");
    }

}
