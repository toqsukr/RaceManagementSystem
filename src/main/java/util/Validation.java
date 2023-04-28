package util;

public class Validation {
    public static boolean isValidName(String input) {
        return input.matches("^([A-Za-z]{2,20})(\\s[A-Za-z]{0,30})?$");
    }

    public static boolean isValidAge(String input) {
        return input.matches("^[1-9]\\d?$");
    }

    public static boolean isValidTeam(String input) {
        return input.matches("^[\\w\\s]{3,20}$");
    }

    public static boolean isValidPoint(String input) {
        return input.matches("^0|([1-9]\\d{0,5})$");
    }

}
