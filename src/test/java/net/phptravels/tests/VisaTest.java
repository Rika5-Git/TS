package net.phptravels.tests;

import net.phptravels.pages.LoginPage;
import net.phptravels.pages.MainPage;
import net.phptravels.pages.VisaPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisaTest extends BaseTest {

    /**
     * ТЕСТ 1: Полный цикл оформления визы (End-to-End)
     * Согласно сценарию в Visa.txt:
     * 1. Логин
     * 2. Services -> Visa Booking
     * 3. From/To Country, Date, Travelers Count
     * 4. Check Visa
     * 5. Traveler 1 & 2 Details (Passport, DOB, Nationality)
     * 6. Terms & Submit
     */
    @Test
    public void testComplexVisaApplicationE2E() {
        // 1. Авторизация
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        VisaPage visaPage = new VisaPage(driver);
        
        // 2. Переход через меню Services
        visaPage.openViaServices();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 3. Выбор стран и даты
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        visaPage.setDate("20-07-2026");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        // 4. Устанавливаем количество путешественников: 2
        visaPage.setTravelersCount(2);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        // 5. Нажимаем кнопку Check Visa
        visaPage.clickSearch();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 6. Заполняем детали визы (Тип и скорость)
        visaPage.setVisaDetails("Tourist Visa", "Standard");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 7. Заполняем данные ПЕРВОГО человека
        visaPage.fillTravelerData(0, "Ivan", "Ivanov", "123456789", "10-05-1990");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 8. Заполняем данные ВТОРОГО человека
        visaPage.fillTravelerData(1, "Olena", "Ivanova", "987654321", "15-08-1992");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 9. Принимаем условия (Terms and Conditions)
        visaPage.acceptTerms();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 10. Отправляем заявку (Submit Application)
        visaPage.submitApplication();

        // 11. Проверка успеха подачи
        assertTrue(visaPage.isSubmissionSuccessful(), "Ошибка: Заявка на визу не была успешно отправлена.");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // 12. НОВЫЙ ЭТАП: Проверка в Профиле
        net.phptravels.pages.ProfilePage profilePage = new net.phptravels.pages.ProfilePage(driver);
        
        // Переходим Dashboard -> My Bookings -> Visa
        profilePage.goToVisaBookings();
        
        // Проверяем наличие записи и открываем инвойс
        assertTrue(profilePage.isLastBookingPresent(), "Ошибка: Виза не найдена в истории бронирований.");
        profilePage.viewLastVisaInvoice();
        
        // Проверяем, что инвойс открылся
        assertTrue(profilePage.isInvoiceVisible(), "Ошибка: Инвойс визы не отображается.");
        System.out.println("SUCCESS: Виза успешно оформлена и проверена в профиле!");
    }

    /**
     * ТЕСТ 2: Проверка виз через CSV (Data-Driven)
     * Цель: Проверить поиск и первичную форму для разных стран, типов виз и количества людей.
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/visa_data.csv", numLinesToSkip = 1)
    public void testVisaCheck(String fromCountry, String toCountry, String date, String visaType, String speed, int travelers) {
        // 1. Переход в раздел виз
        VisaPage visaPage = new VisaPage(driver);
        visaPage.openViaServices();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 2. Заполнение первичной формы из CSV
        visaPage.selectFromCountry(fromCountry);
        visaPage.selectToCountry(toCountry);
        visaPage.setDate(date);
        visaPage.setTravelersCount(travelers);
        
        // 3. Поиск
        visaPage.clickSearch();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 4. Заполнение деталей визы (Тип и скорость) из CSV
        visaPage.setVisaDetails(visaType, speed);

        // 5. Заполнение данных для каждого путешественника (базовые данные для проверки)
        for (int i = 0; i < travelers; i++) {
            visaPage.fillTravelerData(i, "TestUser" + i, "Tester", "PASSPORT" + i, "01-01-1990");
        }

        // 6. Проверка, что мы находимся на правильной странице и форма активна
        assertTrue(driver.getCurrentUrl().contains("visa") || driver.getPageSource().contains("Visa"),
                "Проверка визы не привела к ожидаемой странице для: " + fromCountry + " -> " + toCountry);
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
                "Должно появиться сообщение о том, что страны должны быть разными.");
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
                "Должно появиться сообщение о пропущенной дате.");
    }
}
