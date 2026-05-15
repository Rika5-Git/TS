package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class VisaPage extends BasePage {

    @FindBy(name = "travel_date")
    private WebElement dateInput;

    public VisaPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Открытие страницы виз через меню Services
     */
    public void openViaServices() {
        System.out.println("Opening Visa page via Services menu...");
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if(attr && attr.includes(\"toggleDropdown('services')\")) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        executeJS(
            "var links = document.querySelectorAll('a');" +
            "for(var i=0; i<links.length; i++) {" +
            "  if(links[i].href && (links[i].href.includes('/visa') || links[i].innerText.toLowerCase().includes('visa'))) {" +
            "    links[i].click(); break;" +
            "  }" +
            "}"
        );
        waitForLoader();
        // Fallback если не перешло
        if (!driver.getCurrentUrl().contains("visa")) {
            driver.get("https://phptravels.net/visa");
        }
    }

    public void selectFromCountry(String country) {
        System.out.println("Selecting From Country: " + country);
        selectCountryInDropdown(0, country);
    }

    public void selectToCountry(String country) {
        System.out.println("Selecting To Country: " + country);
        selectCountryInDropdown(1, country);
    }

    private void selectCountryInDropdown(int index, String country) {
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "var currentIdx = 0;" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('toggleDropdown()')) {" +
            "    if(currentIdx === " + index + ") { divs[i].click(); break; }" +
            "    currentIdx++;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        
        executeJS(
            "var inputs = document.querySelectorAll('input[x-ref=\"searchInput\"]');" +
            "if(inputs[" + index + "]) {" +
            "  inputs[" + index + "].value = '" + country + "';" +
            "  inputs[" + index + "].dispatchEvent(new Event('input', { bubbles: true }));" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        executeJS(
            "var containers = document.querySelectorAll('.input-dropdown-content');" +
            "var items = containers[" + index + "].querySelectorAll('.input-dropdown-item');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].innerText.trim().toLowerCase().includes('" + country.toLowerCase() + "')) {" +
            "    items[i].click(); return true;" +
            "  }" +
            "}"
        );
        waitForLoader();
    }

    public void setDate(String date) {
        System.out.println("Setting travel date: " + date);
        executeJS("var el = document.getElementsByName('travel_date')[0]; if(el) { el.value = '" + date + "'; el.dispatchEvent(new Event('change', { bubbles: true })); }");
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void setTravelersCount(int count) {
        System.out.println("Setting travelers count to: " + count);
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('open = !open') && divs[i].innerText.toLowerCase().includes('traveler')) {" +
            "    divs[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        for (int i = 1; i < count; i++) {
            executeJS(
                "var btns = document.querySelectorAll('button');" +
                "for(var j=0; j<btns.length; j++) {" +
                "  var attr = btns[j].getAttribute('@click');" +
                "  if(attr && attr.includes('increment()')) {" +
                "    btns[j].click(); break;" +
                "  }" +
                "}"
            );
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        executeJS("document.body.click();");
    }

    public void clickSearch() {
        System.out.println("Clicking Check Visa button...");
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  if(btns[i].type === 'submit' && btns[i].innerText.includes('Check Visa')) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        waitForLoader();
    }

    public void setVisaDetails(String visaType, String speed) {
        System.out.println("Setting Visa Details: " + visaType + ", " + speed);
        
        // 1. Visa Type - ищем по тексту внутри дропдауна
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('open = !open') && divs[i].innerText.toLowerCase().includes('visa type')) {" +
            "    divs[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + visaType + "')) { items[i].click(); break; } }");

        // 2. Speed - аналогично
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('open = !open') && divs[i].innerText.toLowerCase().includes('speed')) {" +
            "    divs[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + speed + "')) { items[i].click(); break; } }");
    }

    public void fillTravelerData(int index, String firstName, String lastName, String passportNumber, String dob) {
        System.out.println("--- Filling data for traveler #" + (index + 1) + " ---");
        
        String updateField = "function upd(sel, idx, val) {" +
                            "  var els = document.querySelectorAll(sel);" +
                            "  if(els[idx]) {" +
                            "    els[idx].value = val;" +
                            "    els[idx].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "    els[idx].dispatchEvent(new Event('change', { bubbles: true }));" +
                            "  }" +
                            "}";

        executeJS(updateField + "upd('select[x-model=\"traveler.title\"]', " + index + ", 'Mr');");
        executeJS(updateField + "upd('input[x-model=\"traveler.first_name\"]', " + index + ", '" + firstName + "');");
        executeJS(updateField + "upd('input[x-model=\"traveler.last_name\"]', " + index + ", '" + lastName + "');");
        executeJS(updateField + "upd('input[x-model=\"traveler.passport_number\"]', " + index + ", '" + passportNumber + "');");
        executeJS(updateField + "upd('select[x-model=\"traveler.nationality\"]', " + index + ", 'UA');");
        executeJS(updateField + "upd('select[x-model=\"traveler.expiry_day\"]', " + index + ", '10');");
        executeJS(updateField + "upd('select[x-model=\"traveler.expiry_month\"]', " + index + ", '05');");
        executeJS(updateField + "upd('select[x-model=\"traveler.expiry_year\"]', " + index + ", '2030');");

        String[] dobParts = dob.split("-");
        executeJS(updateField + "upd('select[x-model=\"traveler.dob_day\"]', " + index + ", '" + dobParts[0] + "');");
        executeJS(updateField + "upd('select[x-model=\"traveler.dob_month\"]', " + index + ", '" + dobParts[1] + "');");
        executeJS(updateField + "upd('select[x-model=\"traveler.dob_year\"]', " + index + ", '" + dobParts[2] + "');");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void acceptTerms() {
        System.out.println("Accepting Terms...");
        // Находим лейбл чекбокса для визуального клика через более простой JS
        executeJS(
            "var label = document.querySelector('label[for=\"terms_accepted\"]'); " +
            "if (label) { " +
            "  label.scrollIntoView({behavior: \"auto\", block: \"center\"}); " +
            "  label.click(); " +
            "} else { " +
            "  var chk = document.getElementById(\"terms_accepted\"); " +
            "  if (chk) chk.click(); " +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void submitApplication() {
        System.out.println("Clicking Submit Application...");
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if(attr && attr.includes('submitBooking')) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        waitForLoader();
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {} // Ждем дольше для редиректа
    }

    public void clearDate() {
        executeJS("var el = document.getElementsByName('travel_date')[0]; if(el) { el.value = ''; el.dispatchEvent(new Event('change', { bubbles: true })); }");
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
        System.out.println("Checking submission status. URL: " + driver.getCurrentUrl());
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        String url = driver.getCurrentUrl();
        String source = driver.getPageSource();
        
        boolean success = url.contains("success") || url.contains("invoice") || 
                          source.contains("Confirmation") || source.contains("Received") || 
                          source.contains("Success") || source.contains("Thank you");
        
        if (!success) {
            System.out.println("Submission seems failed. URL: " + url);
            // Пытаемся найти текст ошибки на странице
            executeJS("var err = document.querySelector('.alert-danger'); if(err) console.log('Error found: ' + err.innerText);");
        }
        return success;
    }
}
