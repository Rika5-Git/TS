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
        // 1. Авторизация (Предусловие)
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");

        // Даем время на редирект после логина
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // 2. Переход к машинам
        MainPage mainPage = new MainPage(driver);
        mainPage.openCars();

        // 3. Поиск авто (тепер вказуємо два місця)
        CarsPage carsPage = new CarsPage(driver);
        carsPage.searchCars("Dubai", "Dubai");

        // 4. Применение фильтров (Automatic + SUV)
        carsPage.applyFilters();

        // 5. Выбор первой машины
        carsPage.selectFirstCar();

        // 6. Завершение бронирования
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        // 7. Проверка успешного бронирования
        assertTrue(bookingPage.isBookingSuccessful(), 
                "E2E Car Booking failed. Final URL: " + driver.getCurrentUrl());

        // 8. ПРОВЕРКА В ПРОФИЛЕ
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.goToMyCarsBookings();

        assertTrue(profilePage.isLastBookingPresent(), 
                "The booked car was not found in 'My Bookings -> Cars'");

        // По желанию смотрим инвойс
        profilePage.viewLastInvoice();
    }
}
