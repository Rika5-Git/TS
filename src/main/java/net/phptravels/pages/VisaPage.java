package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class VisaPage extends BasePage {

    @FindBy(name = "travel_date")
    private WebElement dateInput;

    public VisaPage(WebDriver driver) {
        super(driver);
    }

    public void openViaServices() {
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if(attr && attr.includes(\"services\")) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var link = document.querySelector(\"a[href*='/visa']\"); if(link) link.click();");
        waitForLoader();
    }

    public void selectFromCountry(String country) {
        selectCountryInDropdown(0, country);
    }

    public void selectToCountry(String country) {
        selectCountryInDropdown(1, country);
    }

    private void selectCountryInDropdown(int index, String country) {
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "var count = 0;" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('toggleDropdown()')) {" +
            "    if(count === " + index + ") { divs[i].scrollIntoView({behavior: 'auto', block: 'center'}); divs[i].click(); break; }" +
            "    count++;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
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
            "    items[i].click(); break;" +
            "  }" +
            "}"
        );
        waitForLoader();
    }

    public void setDate(String date) {
        executeJS("var el = document.getElementsByName('travel_date')[0]; if(el) { el.value = '" + date + "'; el.dispatchEvent(new Event('change', { bubbles: true })); }");
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void clearDate() {
        executeJS("var el = document.getElementsByName('travel_date')[0]; if(el) { el.value = ''; el.dispatchEvent(new Event('change', { bubbles: true })); }");
    }

    public void setTravelersCount(int count) {
        executeJS(
            "var divs = document.querySelectorAll('div');" +
            "for(var i=0; i<divs.length; i++) {" +
            "  var attr = divs[i].getAttribute('@click');" +
            "  if(attr && attr.includes('open = !open') && divs[i].innerText.includes('Traveler')) {" +
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
        executeJS(
            "var allDivs = document.querySelectorAll('div');" +
            "for (var i = 0; i < allDivs.length; i++) {" +
            "  if (allDivs[i].innerText.includes('Visa Type')) {" +
            "    var btn = allDivs[i].querySelector('div.input') || allDivs[i].querySelector('[\\\\@click*=\"open\"]');" +
            "    if (btn) { btn.scrollIntoView({behavior: \"auto\", block: \"center\"}); btn.click(); break; }" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        executeJS(
            "var items = document.querySelectorAll('.input-dropdown-item');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].innerText.trim().toLowerCase().includes('" + visaType.toLowerCase().trim() + "')) {" +
            "    items[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        executeJS(
            "var allDivs = document.querySelectorAll('div');" +
            "for (var i = 0; i < allDivs.length; i++) {" +
            "  if (allDivs[i].innerText.includes('Processing speed')) {" +
            "    var btn = allDivs[i].querySelector('div.input') || allDivs[i].querySelector('[\\\\@click*=\"open\"]');" +
            "    if (btn) { btn.scrollIntoView({behavior: \"auto\", block: \"center\"}); btn.click(); break; }" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        executeJS(
            "var items = document.querySelectorAll('.input-dropdown-item');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].innerText.trim().toLowerCase().includes('" + speed.toLowerCase().trim() + "')) {" +
            "    items[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void fillTravelerData(int index, String firstName, String lastName, String passportNumber, String dob) {
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
    }

    public void acceptTerms() {
        // Пробуємо кілька варіантів кліку по чекбоксу
        executeJS(
            "var term = document.getElementById('terms_accepted');" +
            "if(term) { term.scrollIntoView({behavior: 'auto', block: 'center'}); term.click(); term.checked = true; }" +
            "else {" +
            "  var labels = document.querySelectorAll('label');" +
            "  for(var i=0; i<labels.length; i++) {" +
            "    if(labels[i].innerText.toLowerCase().includes('agree')) { labels[i].click(); break; }" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void submitApplication() {
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if((attr && attr.includes('submitBooking')) || btns[i].innerText.toLowerCase().includes('confirm')) {" +
            "    btns[i].scrollIntoView({behavior: 'auto', block: 'center'});" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        waitForLoader();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    public String getErrorMessage() {
        try {
            WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger p, .alert-error p")));
            return errorAlert.getText();
        } catch (Exception e) {
            Object msg = executeJS("var el = document.querySelector('.alert-danger, .alert-error'); return el ? el.innerText : 'No error message found';");
            return msg.toString();
        }
    }

    public boolean isSubmissionSuccessful() {
        try {
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            return extendedWait.until(d -> {
                String url = d.getCurrentUrl().toLowerCase();
                String source = d.getPageSource().toLowerCase();
                return url.contains("success") || url.contains("invoice") || 
                       source.contains("confirmation") || source.contains("reservation") || source.contains("invoice");
            });
        } catch (Exception e) {
            return false;
        }
    }
}


