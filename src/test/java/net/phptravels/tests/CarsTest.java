package net.phptravels.tests;

import net.phptravels.pages.MainPage;
import net.phptravels.pages.LoginPage;
import net.phptravels.pages.CarsPage;
import net.phptravels.pages.BookingPage;
import net.phptravels.pages.ProfilePage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarsTest extends BaseTest {

    @Test
    public void testComplexCarBookingE2E() {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        MainPage mainPage = new MainPage(driver);
        mainPage.openCars();

        CarsPage carsPage = new CarsPage(driver);
        carsPage.searchCars("Dubai", "Dubai");

        carsPage.applyFilters();
        carsPage.selectFirstCar();

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful(), 
                "E2E Car Booking failed. Final URL: " + driver.getCurrentUrl());

        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.goToMyCarsBookings();

        assertTrue(profilePage.isLastBookingPresent(), 
                "The booked car was not found in profile history");

        profilePage.viewLastInvoice();
    }
}
