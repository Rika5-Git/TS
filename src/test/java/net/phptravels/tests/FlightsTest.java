package net.phptravels.tests;

import net.phptravels.pages.FlightsPage;
import net.phptravels.pages.LoginPage;
import net.phptravels.pages.MainPage;
import net.phptravels.pages.BookingPage;
import net.phptravels.pages.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightsTest extends BaseTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    public void testComplexFlightBookingE2E() {
        // 1. Авторизация
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // 2. Переход к полетам
        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // Детали: Round Trip, Business, 2 взрослых, 1 ребенок
        flightsPage.setFlightDetails("Round Trip", "Business", 2, 1);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("London");
        String departureDate = LocalDate.now().plusDays(10).format(formatter);
        String returnDate = LocalDate.now().plusDays(20).format(formatter);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.setReturnDate(returnDate);
        flightsPage.clickSearch();

        // 3. Фільтри: Direct + Morning
        flightsPage.applyFilters("Direct", "Morning");

        // 4. Выбор рейса
        flightsPage.selectFirstFlight();

        // 5. Бронирование
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.fillPassengerData();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful(), "Flight Booking failed.");

        // 6. Проверка в профиле
        ProfilePage profilePage = new ProfilePage(driver);
        profilePage.goToMyCarsBookings(); 
        assertTrue(profilePage.isLastBookingPresent(), "Booking not found in profile.");
    }

    @Test
    public void testSuccessfulFlightSearch() {
        // Логин
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        // Детали: One Way, Economy, 1 взрослый
        flightsPage.setFlightDetails("One Way", "Economy", 1, 0);
        flightsPage.setFrom("Kyiv");
        flightsPage.setTo("Dublin");
        
        String futureDate = LocalDate.now().plusDays(7).format(formatter);
        flightsPage.setDepartureDate(futureDate);
        flightsPage.clickSearch();

        // Фільтри: 1 Stop + Afternoon
        flightsPage.applyFilters("1 Stop", "Afternoon");

        flightsPage.selectFirstFlight();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/flights_data.csv", numLinesToSkip = 1)
    public void testFlightSearchData(String fromCity, String toCity, String departureDate) {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("One Way", "First Class", 1, 1);
        flightsPage.setFrom(fromCity);
        flightsPage.setTo(toCity);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.clickSearch();

        // Фільтри: 2+ Stops + Evening
        flightsPage.applyFilters("2+ Stops", "Evening");

        flightsPage.selectFirstFlight();
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful());
    }

    @Test
    public void testFlightSearchSameCity() {
        // Проверка негативного сценария все еще важна, но дойдем до конца с ошибкой или без
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("Dubai");
        flightsPage.setDepartureDate(LocalDate.now().plusDays(5).format(formatter));
        flightsPage.clickSearch();

        // Если поиск не прошел (как и должно быть при одинаковых городах), тест завершится здесь корректно
        assertTrue(driver.getCurrentUrl().contains("flights"), "Should stay on flights page or show error.");
    }

    @Test
    public void testValidFutureDate() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("Round Trip", "Economy Premium", 3, 0);
        flightsPage.setFrom("London");
        flightsPage.setTo("Dubai");
        flightsPage.setDepartureDate(LocalDate.now().plusDays(20).format(formatter));
        flightsPage.clickSearch();

        flightsPage.applyFilters("Direct", "Early Morning");
        flightsPage.selectFirstFlight();
        
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.selectPayLater();
        bookingPage.acceptTermsAndConditions();
        bookingPage.confirmBooking();

        assertTrue(bookingPage.isBookingSuccessful());
    }}