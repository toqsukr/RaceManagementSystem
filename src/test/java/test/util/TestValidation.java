package test.util;

import org.junit.*;

import util.Validation;

public class TestValidation {
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

    @Test
    public void testIsValidTeam() {
        Assert.assertTrue(Validation.isValidTeam("Hounds Dogs37"));
        Assert.assertTrue(Validation.isValidTeam("Horses"));
        Assert.assertTrue(Validation.isValidTeam("Ice"));
        Assert.assertFalse(Validation.isValidTeam("M()nke!"));
        Assert.assertFalse(Validation.isValidTeam("A"));
        Assert.assertFalse(Validation.isValidTeam("jfklsdjvoimdofjlsdjf3628974"));

    }

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

    @BeforeClass // Фиксируем начало тестирования
    public static void allTestsStarted() {
        System.out.println("Test begining");
    }

    @AfterClass // Фиксируем конец тестирования
    public static void allTestsFinished() {
        System.out.println("Test ending");
    }

    @Before // Фиксируем запуск теста
    public void testStarted() {
        System.out.println("Test start");
    }

    @After // Фиксируем завершение теста
    public void testFinished() {
        System.out.println("Test finish");
    }

}
