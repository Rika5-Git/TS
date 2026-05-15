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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightsTest extends BaseTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * ТЕСТ 1: Быстрая проверка поиска (UI & Filters)
     */
    @Test
    public void testCustomFlightBookingWithFilters() {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("One Way", "Economy", 1, 0);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("Muscat");
        
        String departureDate = LocalDate.now().plusDays(10).format(formatter);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.clickSearch();

        flightsPage.applyFilters("Direct", "Morning");

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful(), "Flight Booking failed.");
        }
    }

    /**
     * ТЕСТ 2: Полный цикл бронирования (End-to-End, Round Trip)
     */
    @Test
    public void testComplexFlightBookingE2E() {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");
        
        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("Round Trip", "Business", 2, 1);
        flightsPage.setFrom("LHE"); 
        flightsPage.setTo("DXB");   
        
        String departureDate = LocalDate.now().plusDays(15).format(formatter);
        String returnDate = LocalDate.now().plusDays(25).format(formatter);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.setReturnDate(returnDate);
        
        flightsPage.clickSearch();
        flightsPage.applyFilters("Direct", "Morning"); 

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();

            assertTrue(bookingPage.isBookingSuccessful(), "Booking should be successful");

            ProfilePage profilePage = new ProfilePage(driver);
            profilePage.goToMyCarsBookings(); 
            assertTrue(profilePage.isLastBookingPresent(), "Booking should be found in profile");
        }
    }

    /**
     * ТЕСТ 3: Успешный поиск на популярном направлении
     */
    @Test
    public void testSuccessfulFlightSearch() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("One Way", "Economy Premium", 1, 0);
        flightsPage.setFrom("London");
        flightsPage.setTo("Paris");
        
        String futureDate = LocalDate.now().plusDays(12).format(formatter);
        flightsPage.setDepartureDate(futureDate);
        flightsPage.clickSearch();

        flightsPage.applyFilters("1 Stop", "Afternoon");

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful());
        }
    }

    /**
     * ТЕСТ 4: Параметризованный тест (Data-Driven)
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/flights_data.csv", numLinesToSkip = 1)
    public void testFlightSearchData(String fromCity, String toCity, String departureDate, String flightType, String flightClass, int adults, int children) {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails(flightType, flightClass, adults, children);
        flightsPage.setFrom(fromCity);
        flightsPage.setTo(toCity);
        flightsPage.setDepartureDate(departureDate);
        
        if (flightType.equalsIgnoreCase("Round Trip")) {
            String returnDate = LocalDate.parse(departureDate, formatter).plusDays(10).format(formatter);
            flightsPage.setReturnDate(returnDate);
        }
        
        flightsPage.clickSearch();
        flightsPage.applyFilters("Direct", "Evening");

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful(), "CSV search booking failed");
        }
    }

    /**
     * ТЕСТ 5: Негативный сценарий (Одинаковые города)
     */
    @Test
    public void testFlightSearchSameCity() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("One Way", "Economy", 1, 0);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("Dubai");
        flightsPage.setDepartureDate(LocalDate.now().plusDays(5).format(formatter));
        flightsPage.clickSearch();

        boolean noFlights = !flightsPage.selectFirstFlight();
        assertTrue(noFlights, "Search should not find flights for the same city.");
    }

    /**
     * ТЕСТ 6: Поиск на далекое будущее
     */
    @Test
    public void testValidFutureDate() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.setFlightDetails("Round Trip", "Business", 1, 0);
        flightsPage.setFrom("New York");
        flightsPage.setTo("Singapore");
        
        String departureDate = LocalDate.now().plusMonths(2).format(formatter);
        String returnDate = LocalDate.now().plusMonths(2).plusDays(10).format(formatter);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.setReturnDate(returnDate);
        flightsPage.clickSearch();

        flightsPage.applyFilters("2+ Stops", "Early Morning");
        
        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful());
        }
    }
}
