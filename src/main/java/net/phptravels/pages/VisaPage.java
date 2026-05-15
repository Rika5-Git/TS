package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class VisaPage extends BasePage {

    @FindBy(name = "travel_date")
    private WebElement dateInput;

    @FindBy(xpath = "//button[contains(., 'Check Visa')]")
    private WebElement submitButton;

    public VisaPage(WebDriver driver) {
        super(driver);
    }

    public void selectFromCountry(String country) {
        selectCountryByLabel("From Country", country);
    }

    public void selectToCountry(String country) {
        selectCountryByLabel("To Country", country);
    }

    private void selectCountryByLabel(String labelText, String country) {
        waitForLoader();
        
        // Знаходимо контейнер по тексту мітки (label)
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'form-control') and .//label[contains(., '" + labelText + "')]]")));
        
        // Відкриваємо дропдаун через клік на "Select Country"
        executeJS("var el = arguments[0].querySelector('div.input'); if(el) el.click();", container);
        
        // Знаходимо поле пошуку всередині контейнера
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOf(
            container.findElement(By.cssSelector("input[x-ref='searchInput']"))));
        
        searchInput.clear();
        searchInput.sendKeys(country);

        try { Thread.sleep(800); } catch (InterruptedException e) {}

        // Вибираємо країну зі списку результатів
        boolean found = (Boolean) executeJS(
            "var container = arguments[0];" +
            "var country = arguments[1];" +
            "var items = container.querySelectorAll('.input-dropdown-item');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].innerText.trim().toLowerCase().includes(country.toLowerCase())) {" +
            "    items[i].click(); return true;" +
            "  }" +
            "}" +
            "return false;", container, country
        );
        
        if (!found) {
            System.out.println("Warning: Country '" + country + "' not found via precise JS. Clicking first available.");
            executeJS("var item = arguments[0].querySelector('.input-dropdown-item'); if(item) item.click();", container);
        }
        
        waitForLoader();
    }

    public void setDate(String date) {
        System.out.println("Setting travel date: " + date);
        executeJS("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dateInput, date);
        // Ховаємо календар, якщо він з'явився
        executeJS("var dp = document.querySelector('.datepicker'); if(dp) dp.style.display = 'none';");
    }

    public void clickSearch() {
        System.out.println("Clicking Check Visa button...");
        clickJS(submitButton);
        waitForLoader();
    }

    /**
     * Вибір деталей візи (Тип та Швидкість обробки)
     */
    public void setVisaDetails(String visaType, String speed) {
        System.out.println("Setting Visa Details: " + visaType + ", " + speed);
        
        // Вибір Visa Type
        executeJS(
            "var labels = document.querySelectorAll('label');" +
            "for(var i=0; i<labels.length; i++) {" +
            "  if(labels[i].innerText.includes('Visa Type')) {" +
            "    labels[i].parentElement.querySelector('.input').click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + visaType + "')) { items[i].click(); break; } }");

        // Вибір Processing Speed
        executeJS(
            "var labels = document.querySelectorAll('label');" +
            "for(var i=0; i<labels.length; i++) {" +
            "  if(labels[i].innerText.includes('Processing Speed')) {" +
            "    labels[i].parentElement.querySelector('.input').click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + speed + "')) { items[i].click(); break; } }");
    }

    /**
     * Встановлення кількості людей
     */
    public void setTravelersCount(int count) {
        System.out.println("Setting travelers count to: " + count);
        // Клікаємо на "+" потрібну кількість разів (спочатку завжди 1)
        for (int i = 1; i < count; i++) {
            executeJS("var btn = document.querySelector(\"button[@click*='increment']\"); if(btn) btn.click();");
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Заповнення детальних даних одного мандрівника (0-based index)
     */
    public void fillTravelerData(int index, String firstName, String lastName, String passportNumber, String dob) {
        System.out.println("Filling data for traveler #" + (index + 1));
        
        String prefix = "traveler[" + index + "]";
        
        executeJS("document.getElementsByName('" + prefix + "[first_name]')[0].value = '" + firstName + "';");
        executeJS("document.getElementsByName('" + prefix + "[last_name]')[0].value = '" + lastName + "';");
        executeJS("document.getElementsByName('" + prefix + "[passport_number]')[0].value = '" + passportNumber + "';");
        
        // Дата народження (якщо поле текстове або потребує вибору)
        executeJS("var el = document.getElementsByName('" + prefix + "[dob]')[0]; if(el) el.value = '" + dob + "';");
        
        // Вибір національності (якщо є дропдаун)
        executeJS(
            "var selects = document.getElementsByName('" + prefix + "[nationality]');" +
            "if(selects.length > 0) { selects[0].value = 'UA'; selects[0].dispatchEvent(new Event('change')); }"
        );
    }

    public void acceptTerms() {
        executeJS("var chk = document.querySelector('input[type=\"checkbox\"]'); if(chk) chk.checked = true;");
    }

    public void submitApplication() {
        System.out.println("Submitting Visa Application...");
        executeJS("var btn = document.querySelector('button[type=\"submit\"]'); if(btn) btn.click();");
        waitForLoader();
    }

    public void clearDate() {
        executeJS("arguments[0].value = '';", dateInput);
    }

    public String getErrorMessage() {
        try {
            WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-error p")));
            return errorAlert.getText();
        } catch (Exception e) {
            return "No error message found";
        }
    }

    public boolean isSubmissionSuccessful() {
        waitForLoader();
        return driver.getCurrentUrl().contains("success") || 
               driver.getPageSource().contains("Confirmation") ||
               driver.getPageSource().contains("Received");
    }
}