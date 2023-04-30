package test.util;

import org.junit.*;

import util.Validation;

/***
 * This class is responsible for testing the methods of the Validation class
 */

public class TestValidation {
    /***
     * This method tests isValidName method of Validation class
     */
    @Test
    public void testIsValidName() {
        Assert.assertTrue(Validation.isValidName("Andrew Petrovich"));
        Assert.assertTrue(Validation.isValidName("Nick"));
        Assert.assertTrue(Validation.isValidName("Po"));
        Assert.assertFalse(Validation.isValidName("Nickfjksljdfioncodsjifklsmdcv"));
        Assert.assertFalse(Validation.isValidName("Sergeyk123 Ivanov"));
        Assert.assertFalse(Validation.isValidName(" Pegasov"));
        Assert.assertFalse(Validation.isValidName("A!ek T1nkoff"));
        Assert.assertFalse(Validation.isValidName(""));
    }

    /***
     * This method tests isValidAge method of Validation class
     */
    @Test
    public void testIsValidAge() {
        Assert.assertTrue(Validation.isValidAge("45"));
        Assert.assertTrue(Validation.isValidAge("18"));
        Assert.assertFalse(Validation.isValidAge("700"));
        Assert.assertFalse(Validation.isValidAge("Andrew47"));
        Assert.assertFalse(Validation.isValidAge("54Kolya"));
        Assert.assertFalse(Validation.isValidAge("-19"));
        Assert.assertFalse(Validation.isValidAge(""));
    }

    /***
     * This method tests isValidTeam method of Validation class
     */
    @Test
    public void testIsValidTeam() {
        Assert.assertTrue(Validation.isValidTeam("Hounds Dogs37"));
        Assert.assertTrue(Validation.isValidTeam("Horses"));
        Assert.assertTrue(Validation.isValidTeam("Ice"));
        Assert.assertFalse(Validation.isValidTeam("M()nke!"));
        Assert.assertFalse(Validation.isValidTeam("A"));
        Assert.assertFalse(Validation.isValidTeam("jfklsdjvoimdofjlsdjf3628974"));

    }

    /***
     * This method tests isValidPoint method of Validation class
     */
    @Test
    public void testIsValidPoint() {
        Assert.assertTrue(Validation.isValidPoint("53824"));
        Assert.assertTrue(Validation.isValidPoint("0"));
        Assert.assertFalse(Validation.isValidPoint("asdf57424"));
        Assert.assertFalse(Validation.isValidPoint("-4734"));
        Assert.assertFalse(Validation.isValidPoint("573fj943"));
        Assert.assertFalse(Validation.isValidPoint("1000000"));
        Assert.assertFalse(Validation.isValidPoint(""));
    }

}
