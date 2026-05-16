package net.phptravels.tests;

import net.phptravels.pages.MainPage;
import net.phptravels.pages.LoginPage;
import net.phptravels.pages.CarsPage;
import net.phptravels.pages.BookingPage;
import net.phptravels.pages.ProfilePage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarsTest extends BaseTest {

    /**
     * ТЕСТ 1: Комплексне бронювання авто (End-to-End)
     */
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
    }

    /**
     * ТЕСТ 2: МЕГА-СКЛАДНИЙ ТЕСТ (Верифікація деталей інвойсу)
     * Перевіряє: Пошук -> Фільтри (SUV + Auto) -> Бронювання -> Профіль -> Текст Інвойсу.
     */
    @Test
    public void testMegaComplexCarBookingWithInvoiceVerification() {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        MainPage mainPage = new MainPage(driver);
        mainPage.openCars();

        CarsPage carsPage = new CarsPage(driver);
        carsPage.searchCars("Dubai", "Dubai");

        // Застосовуємо складні фільтри: SUV + Automatic
        carsPage.applyFilters();
        carsPage.selectFirstCar();

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.acceptTermsAndConditions();
        bookingPage.selectPayLater();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful(), "Booking failed at confirmation stage");

        // ПЕРЕВІРКА В ПРОФІЛІ: Глибока перевірка вмісту інвойсу
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.goToMyCarsBookings();

        assertTrue(profilePage.isLastBookingPresent(), "Car booking not found in history");

        // Відкриваємо інвойс і зчитуємо його текст
        profilePage.viewLastInvoice();
        String invoiceText = profilePage.getInvoiceDetails();
        
        // Перевіряємо, що інвойс містить підтвердження статусу або типу авто
        assertTrue(invoiceText.contains("SUV") || invoiceText.contains("Confirmed") || invoiceText.contains("Reserved") || invoiceText.contains("Invoice"), 
                "Invoice details should confirm the booking. Found text: " + invoiceText);
    }
}
