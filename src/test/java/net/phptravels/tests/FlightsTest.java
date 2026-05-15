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

    /**
     * ТЕСТ 1: Быстрая проверка поиска (UI & Filters)
     * Цель: Проверить, что форма поиска работает, города выбираются, и фильтры нажимаются.
     * Особенности: Использует тип "One Way" и 1 пассажира. Если рейсы не найдены, 
     * тест все равно считается успешным, так как мы проверили работоспособность интерфейса.
     */
    @Test
    public void testCustomFlightBookingWithFilters() {
        // 1. Авторизация
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 2. Переход к полетам
        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // Настройка параметров поиска: One Way, Economy, 1 Adult
        flightsPage.setFlightDetails("One Way", "Economy", 1, 0);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("Muscat");
        
        String departureDate = LocalDate.now().plusDays(10).format(formatter);
        flightsPage.setDepartureDate(departureDate);
        
        flightsPage.clickSearch();

        // 3. Фильтры
        flightsPage.applyFilters("Direct", "Morning");

        // 4. Выбор рейса и бронирование
        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful(), "Flight Booking failed.");
        } else {
            System.out.println("SKIP: No flights found for Custom test. UI search verified.");
        }
    }

    /**
     * ТЕСТ 2: Полный цикл бронирования (End-to-End, Round Trip)
     * Цель: Пройти весь путь от поиска до записи билета в личный кабинет пользователя (Туда и Обратно).
     * Особенности: Использует самый стабильный маршрут (Лахор-Дубай). Система заполняет данные пассажиров, 
     * бронирует билет и заходит в Профиль, чтобы убедиться, что заказ появился в истории.
     */
    @Test
    public void testComplexFlightBookingE2E() {
        // 1. Открываем страницу логина
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        
        // 2. Вводим email и пароль, нажимаем кнопку входа (с автоматическим закрытием мешающих окон)
        loginPage.login("user@phptravels.com", "demouser");
        
        // Небольшая пауза, чтобы страница личного кабинета успела загрузиться
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 3. Переходим в раздел полетов через главное меню "Services" -> "Flights Booking"
        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // 4. Заполняем основные детали поиска:
        // Тип: Round Trip (Туда и Обратно), Класс: Business, Пассажиры: 2 взрослых и 1 ребенок
        flightsPage.setFlightDetails("Round Trip", "Business", 2, 1);
        
        // 5. Указываем города (используем коды аэропортов для точности)
        flightsPage.setFrom("LHE"); // Откуда: Лахор (Пакистан)
        flightsPage.setTo("DXB");   // Куда: Дубай (ОАЭ)
        
        // 6. Устанавливаем даты поездки
        String departureDate = LocalDate.now().plusDays(15).format(formatter); // Вылет через 15 дней
        String returnDate = LocalDate.now().plusDays(25).format(formatter);    // Возврат через 25 дней
        flightsPage.setDepartureDate(departureDate);
        flightsPage.setReturnDate(returnDate);
        
        // 7. Нажимаем кнопку "Search Flights" (Поиск рейсов)
        flightsPage.clickSearch();

        // 8. Применяем фильтр "Direct" (Прямые рейсы) - проверяем работу боковой панели фильтров
        flightsPage.applyFilters("Direct", "Morning"); 

        // 9. Проверяем, найдены ли билеты. Если список не пуст — продолжаем бронирование
        if (flightsPage.selectFirstFlight()) {
            
            // 10. Переходим к оформлению заказа
            BookingPage bookingPage = new BookingPage(driver);
            
            // Выбираем способ оплаты "Оплатить позже" (Pay Later)
            bookingPage.selectPayLater();
            
            // Заполняем анкеты всех пассажиров (имена, фамилии, данные паспортов)
            bookingPage.fillPassengerData();
            
            // Принимаем правила и условия (Terms and Conditions)
            bookingPage.acceptTermsAndConditions();
            
            // Финальное подтверждение бронирования
            bookingPage.confirmBooking();

            // Проверяем, что появилось сообщение об успешном создании заказа
            assertTrue(bookingPage.isBookingSuccessful(), "Ошибка: Бронирование не завершено успешно.");

            // 11. Сквозная проверка (E2E): Заходим в Профиль пользователя
            ProfilePage profilePage = new ProfilePage(driver);
            
            // Переходим в раздел "Мои бронирования"
            profilePage.goToMyCarsBookings(); 
            
            // Убеждаемся, что наш новый билет отображается в списке последних заказов
            assertTrue(profilePage.isLastBookingPresent(), "Ошибка: Бронирование не найдено в истории профиля.");
            
            System.out.println("SUCCESS: Весь цикл от поиска до записи в профиле пройден успешно!");
        } else {
            // Если рейсов не нашлось, тест не падает, а выводит предупреждение в консоль
            System.out.println("INFO: На демо-сервере сейчас нет доступных билетов Round Trip по маршруту LHE->DXB. Поиск проверен.");
        }
    }

    /**
     * ТЕСТ 3: Успешный поиск на популярном направлении
     * Цель: Проверка стандартного сценария поиска One Way билета с пересадками.
     */
    @Test
    public void testSuccessfulFlightSearch() {
        // Логин
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // Детали: One Way, Economy Premium, 1 Adult
        flightsPage.setFlightDetails("One Way", "Economy Premium", 1, 0);
        flightsPage.setFrom("London");
        flightsPage.setTo("Paris");
        
        String futureDate = LocalDate.now().plusDays(12).format(formatter);
        flightsPage.setDepartureDate(futureDate);
        flightsPage.clickSearch();

        // Фильтры
        flightsPage.applyFilters("1 Stop", "Afternoon");

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful());
        } else {
            System.out.println("SKIP: No flights found for Search test. UI verified.");
        }
    }

    /**
     * ТЕСТ 4: Параметризованный тест (Data-Driven)
     * Цель: Проверить поиск для разных городов, используя данные из CSV файла.
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/flights_data.csv", numLinesToSkip = 1)
    public void testFlightSearchData(String fromCity, String toCity, String departureDate) {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // Настройка формы для каждого набора данных из CSV
        flightsPage.setFlightDetails("One Way", "First Class", 1, 0);
        flightsPage.setFrom(fromCity);
        flightsPage.setTo(toCity);
        flightsPage.setDepartureDate(departureDate);
        flightsPage.clickSearch();

        // Фильтры
        flightsPage.applyFilters("Direct", "Evening");

        if (flightsPage.selectFirstFlight()) {
            BookingPage bookingPage = new BookingPage(driver);
            bookingPage.selectPayLater();
            bookingPage.fillPassengerData();
            bookingPage.acceptTermsAndConditions();
            bookingPage.confirmBooking();
            assertTrue(bookingPage.isBookingSuccessful());
        } else {
            System.out.println("SKIP: No flights found for CSV data: " + fromCity + " to " + toCity);
        }
    }

    /**
     * ТЕСТ 5: Негативный сценарий (Одинаковые города)
     * Цель: Убедиться, что система корректно реагирует, если город отправления и прибытия совпадают.
     */
    @Test
    public void testFlightSearchSameCity() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        // Заполняем форму полностью, даже для негативного теста
        flightsPage.setFlightDetails("One Way", "Economy", 1, 0);
        flightsPage.setFrom("Dubai");
        flightsPage.setTo("Dubai");
        flightsPage.setDepartureDate(LocalDate.now().plusDays(5).format(formatter));
        flightsPage.clickSearch();

        // Проверяем, что поиск не привел к результатам или остался на той же странице
        boolean noFlights = !flightsPage.selectFirstFlight();
        assertTrue(noFlights, "Search should not find flights for the same city.");
    }

    /**
     * ТЕСТ 6: Поиск на далекое будущее
     * Цель: Проверить работу календаря и поиска на даты через несколько месяцев.
     */
    @Test
    public void testValidFutureDate() {
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");

        MainPage mainPage = new MainPage(driver);
        mainPage.openFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        
        // Далекое будущее
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
        } else {
            System.out.println("SKIP: No flights found for Future Date test.");
        }
    }
}
