package net.phptravels.tests;

import net.phptravels.pages.MainPage;
import net.phptravels.pages.VisaPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisaTest extends BaseTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/visa_data.csv", numLinesToSkip = 1)
    public void testVisaCheck(String fromCountry, String toCountry, String date) {
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        visaPage.selectFromCountry(fromCountry);
        visaPage.selectToCountry(toCountry);
        visaPage.setDate(date);
        visaPage.clickSearch();

        assertTrue(driver.getCurrentUrl().contains("visa/submit") || driver.getPageSource().contains("Visa"),
                "Visa check did not lead to the expected page");
    }

    @Test
    public void testVisaSubmissionProcess() {
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        visaPage.setDate("15-05-2026");
        visaPage.clickSearch();

        assertTrue(driver.getPageSource().contains("Egypt"), "Visa results for Egypt not found");
    }

    @Test
    public void testVisaSameCountryError() {
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Ukraine");
        visaPage.clickSearch();

        String error = visaPage.getErrorMessage();
        assertTrue(error.contains("same") || error.contains("different"), 
                "Error message should mention that countries must be different. Got: " + error);
    }

    @Test
    public void testVisaEmptyDateError() {
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        
        visaPage.clearDate();
        visaPage.clickSearch();

        String error = visaPage.getErrorMessage();
        assertTrue(error.contains("Date") || error.contains("empty") || error.contains("select"), 
                "Error message should mention missing date. Got: " + error);
    }
}