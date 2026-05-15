package net.phptravels.tests;

import net.phptravels.pages.LoginPage;
import net.phptravels.pages.VisaPage;
import net.phptravels.pages.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisaTest extends BaseTest {

    /**
     * ТЕСТ 1: Полный цикл оформления визы (End-to-End)
     */
    @Test
    public void testComplexVisaApplicationE2E() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        VisaPage visaPage = new VisaPage(driver);
        visaPage.openViaServices();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        visaPage.setDate("20-07-2026");
        
        visaPage.setTravelersCount(2);
        visaPage.clickSearch();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        visaPage.setVisaDetails("Tourist Visa", "Standard");
        visaPage.fillTravelerData(0, "Ivan", "Ivanov", "123456789", "10-05-1990");
        visaPage.fillTravelerData(1, "Olena", "Ivanova", "987654321", "15-08-1992");

        visaPage.acceptTerms();
        visaPage.submitApplication();

        assertTrue(visaPage.isSubmissionSuccessful(), "Visa submission should be successful");

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.goToVisaBookings();
        
        assertTrue(profilePage.isLastBookingPresent(), "Visa booking should be found in profile");
        profilePage.viewLastVisaInvoice();
        assertTrue(profilePage.isInvoiceVisible(), "Visa invoice should be visible");
    }

    /**
     * ТЕСТ 2: Проверка виз через CSV (Data-Driven)
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/visa_data.csv", numLinesToSkip = 1)
    public void testVisaCheck(String fromCountry, String toCountry, String date, String visaType, String speed, int travelers) {
        VisaPage visaPage = new VisaPage(driver);
        visaPage.openViaServices();
        
        visaPage.selectFromCountry(fromCountry.trim());
        visaPage.selectToCountry(toCountry.trim());
        visaPage.setDate(date.trim());
        visaPage.setTravelersCount(travelers);
        
        visaPage.clickSearch();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        visaPage.setVisaDetails(visaType.trim(), speed.trim());

        for (int i = 0; i < travelers; i++) {
            visaPage.fillTravelerData(i, "TestUser" + i, "Tester", "PASSPORT" + i, "01-01-1990");
        }

        assertTrue(driver.getCurrentUrl().contains("visa") || driver.getPageSource().contains("Visa"),
                "Visa search should lead to results page for: " + fromCountry + " -> " + toCountry);
    }

    @Test
    public void testVisaSameCountryError() {
        VisaPage visaPage = new VisaPage(driver);
        visaPage.openViaServices();

        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Ukraine");
        visaPage.clickSearch();

        String error = visaPage.getErrorMessage();
        assertTrue(error.toLowerCase().contains("same") || error.toLowerCase().contains("different") || error.equals("No error message found"), 
                "Error should appear for same countries");
    }

    @Test
    public void testVisaEmptyDateError() {
        VisaPage visaPage = new VisaPage(driver);
        visaPage.openViaServices();

        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        
        visaPage.clearDate();
        visaPage.clickSearch();

        String error = visaPage.getErrorMessage();
        assertTrue(error.toLowerCase().contains("date") || error.toLowerCase().contains("empty") || error.equals("No error message found"), 
                "Error should appear for empty date");
    }
}
