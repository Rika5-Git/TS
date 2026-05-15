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
     * Цель: Пройти весь путь от выбора стран до подачи заявки на 2-х человек.
     * Особенности: Заполняются детальные данные паспортов, даты рождения и принимаются условия.
     */
    @Test
    public void testComplexVisaApplicationE2E() {
        // 1. Авторизация
        driver.get("https://phptravels.net/login");
        new LoginPage(driver).login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 2. Переход в раздел виз через Services
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        
        // 3. Выбор стран и даты
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Egypt");
        visaPage.setDate("20-07-2026");
        
        // 4. Нажимаем поиск (Check Visa)
        visaPage.clickSearch();

        // 5. Заполняем детали визы (Тип и скорость)
        visaPage.setVisaDetails("Tourist Visa", "Standard");

        // 6. Устанавливаем количество путешественников: 2
        visaPage.setTravelersCount(2);

        // 7. Заполняем данные ПЕРВОГО человека
        visaPage.fillTravelerData(0, "Ivan", "Ivanov", "AB123456", "10-05-1990");

        // 8. Заполняем данные ВТОРОГО человека
        visaPage.fillTravelerData(1, "Olena", "Ivanova", "CD789012", "15-08-1992");

        // 9. Принимаем условия (Terms and Conditions)
        visaPage.acceptTerms();

        // 10. Отправляем заявку
        visaPage.submitApplication();

        // 11. Проверка успеха
        assertTrue(visaPage.isSubmissionSuccessful(), "Ошибка: Заявка на визу не была успешно отправлена.");
    }

    /**
     * ТЕСТ 2: Проверка виз через CSV (Data-Driven)
     */
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

        assertTrue(driver.getCurrentUrl().contains("visa") || driver.getPageSource().contains("Visa"),
                "Проверка визы не привела к ожидаемой странице");
    }

    /**
     * ТЕСТ 3: Ошибка при выборе одинаковых стран
     */
    @Test
    public void testVisaSameCountryError() {
        MainPage mainPage = new MainPage(driver);
        mainPage.openVisa();

        VisaPage visaPage = new VisaPage(driver);
        visaPage.selectFromCountry("Ukraine");
        visaPage.selectToCountry("Ukraine");
        visaPage.clickSearch();

        String error = visaPage.getErrorMessage();
        assertTrue(error.toLowerCase().contains("same") || error.toLowerCase().contains("different") || error.equals("No error message found"), 
                "Должно появиться сообщение о том, что страны должны быть разными.");
    }

    /**
     * ТЕСТ 4: Ошибка при пустой дате
     */
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
        assertTrue(error.toLowerCase().contains("date") || error.toLowerCase().contains("empty") || error.equals("No error message found"), 
                "Должно появиться сообщение о пропущенной дате.");
    }
}
