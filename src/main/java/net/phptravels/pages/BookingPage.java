package net.phptravels.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BookingPage extends BasePage {

    @FindBy(id = "terms_accepted")
    private WebElement termsCheckbox;

    @FindBy(xpath = "//button[contains(., 'Confirm Booking')]")
    private WebElement confirmBookingButton;

    public BookingPage(WebDriver driver) {
        super(driver);
    }

    public void selectPayLater() {
        waitForLoader();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        
        try {
            boolean selected = (Boolean) executeJS(
                "var labels = document.querySelectorAll('label');" +
                "for(var i=0; i<labels.length; i++) {" +
                "  if(labels[i].innerText.toLowerCase().includes('pay later')) {" +
                "    var inputId = labels[i].getAttribute('for');" +
                "    if(inputId) {" +
                "      document.getElementById(inputId).click();" +
                "      return true;" +
                "    }" +
                "  }" +
                "}" +
                "return false;"
            );

            if (!selected) {
                executeJS("var inputs = document.querySelectorAll('input[type=\"radio\"]'); for(var i=0; i<inputs.length; i++) { if(inputs[i].value && inputs[i].value.toLowerCase().includes('later')) { inputs[i].click(); break; } }");
            }
        } catch (Exception ignored) {}
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void acceptTermsAndConditions() {
        try {
            // Примусово встановлюємо статус чекбоксу через JS та клікаємо для Alpine.js
            executeJS(
                "var term = document.getElementById('terms_accepted');" +
                "if(term) {" +
                "  term.scrollIntoView({behavior: 'auto', block: 'center'});" +
                "  if(!term.checked) term.click();" +
                "  term.checked = true;" +
                "  term.dispatchEvent(new Event('change', { bubbles: true }));" +
                "}"
            );
            Thread.sleep(500);
        } catch (Exception ignored) {}
    }

    public void confirmBooking() {
        try {
            // Шукаємо кнопку Confirm Booking, яка залежить від стану чекбоксу
            executeJS(
                "var buttons = document.querySelectorAll('button[type=\"submit\"]');" +
                "for(var i=0; i<buttons.length; i++) {" +
                "  if(buttons[i].innerText.toLowerCase().includes('confirm')) {" +
                "    buttons[i].scrollIntoView({behavior: 'auto', block: 'center'});" +
                "    buttons[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;"
            );
        } catch (Exception ignored) {}
        waitForLoader();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    public void fillPassengerData() {
        try {
            executeJS("var inputs = document.querySelectorAll('input[name*=\"firstname\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='John'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            executeJS("var inputs = document.querySelectorAll('input[name*=\"lastname\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='Doe'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            executeJS("var selects = document.querySelectorAll('select[name*=\"nationality\"]'); for(var i=0; i<selects.length; i++) { selects[i].value='US'; selects[i].dispatchEvent(new Event('change', { bubbles: true })); }");
            executeJS("var inputs = document.querySelectorAll('input[name*=\"dob\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='1990-01-01'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            executeJS("var inputs = document.querySelectorAll('input[name*=\"passport\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='A1234567'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
        } catch (Exception ignored) {}
    }

    public boolean isBookingSuccessful() {
        try {
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            return extendedWait.until(d -> {
                String url = d.getCurrentUrl().toLowerCase();
                String source = d.getPageSource().toLowerCase();
                return url.contains("success") || url.contains("reservation") || url.contains("invoice") || url.contains("voucher") ||
                       source.contains("booking reserved") || source.contains("booking success") || source.contains("invoice");
            });
        } catch (Exception e) {
            return false;
        }
    }
}

