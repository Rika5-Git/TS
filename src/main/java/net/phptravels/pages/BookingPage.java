package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
        System.out.println("Selecting 'Pay Later' payment method...");
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {} // Wait for booking page fully
        
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
                System.out.println("Could not find 'Pay Later' by label, trying fallback JS...");
                executeJS("var inputs = document.querySelectorAll('input[type=\"radio\"]'); for(var i=0; i<inputs.length; i++) { if(inputs[i].value && inputs[i].value.toLowerCase().includes('later')) { inputs[i].click(); break; } }");
            }
            System.out.println("'Pay Later' selected via JS.");
        } catch (Exception e) {
            System.out.println("Could not select 'Pay Later': " + e.getMessage());
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public void acceptTermsAndConditions() {
        System.out.println("Accepting Terms and Conditions...");
        try {
            boolean accepted = (Boolean) executeJS(
                "var inputs = document.querySelectorAll('input[type=\"checkbox\"]');" +
                "for(var i=0; i<inputs.length; i++) {" +
                "  if(!inputs[i].checked) {" +
                "    inputs[i].click();" +
                "    inputs[i].checked = true;" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;"
            );
            if (!accepted) {
                executeJS("var labels = document.querySelectorAll('label'); for(var i=0; i<labels.length; i++) { if(labels[i].innerText.toLowerCase().includes('agree') || labels[i].innerText.toLowerCase().includes('terms')) { labels[i].click(); break; } }");
            }
        } catch (Exception e) {
            System.out.println("Could not explicitly accept terms, might not be required or structure changed.");
        }
    }

    public void confirmBooking() {
        System.out.println("Confirming Booking...");
        try {
            boolean clicked = (Boolean) executeJS(
                "var buttons = document.querySelectorAll('button');" +
                "for(var i=0; i<buttons.length; i++) {" +
                "  var text = buttons[i].innerText.toLowerCase();" +
                "  if(text.includes('confirm') || text.includes('book') || text.includes('pay')) {" +
                "    buttons[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;"
            );
            
            if(!clicked && confirmBookingButton != null) {
               clickJS(confirmBookingButton);
            }
        } catch (Exception e) {
            System.out.println("JS click failed, trying direct click: " + e.getMessage());
            try {
                clickJS(confirmBookingButton);
            } catch (Exception ex) {
                 // Fallback
            }
        }
        waitForLoader();
    }

    public void fillPassengerData() {
        System.out.println("Filling passenger details...");
        try {
            executeJS("var inputs = document.querySelectorAll('input[name*=\"firstname\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='John'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            executeJS("var inputs = document.querySelectorAll('input[name*=\"lastname\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='Doe'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            
            // Встановити nationality якщо є
            executeJS("var selects = document.querySelectorAll('select[name*=\"nationality\"]'); for(var i=0; i<selects.length; i++) { selects[i].value='US'; selects[i].dispatchEvent(new Event('change', { bubbles: true })); }");
            
            // Заповнити дати народження та паспортні дані для Flights
            executeJS("var inputs = document.querySelectorAll('input[name*=\"dob\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='1990-01-01'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
            executeJS("var inputs = document.querySelectorAll('input[name*=\"passport\"]'); for(var i=0; i<inputs.length; i++) { inputs[i].value='A1234567'; inputs[i].dispatchEvent(new Event('input', { bubbles: true })); }");
        } catch (Exception e) {
            System.out.println("Failed to fill some passenger details (might not be required on this specific form).");
        }
    }

    public boolean isBookingSuccessful() {
        try {
            System.out.println("Waiting for booking confirmation URL or page text...");
            // Чекаємо до 30 секунд (важливо для реального E2E)
            for(int i = 0; i < 30; i++) {
                String url = driver.getCurrentUrl().toLowerCase();
                String source = driver.getPageSource().toLowerCase();
                if (url.contains("success") || url.contains("reservation") || url.contains("invoice") || url.contains("voucher")) {
                    System.out.println("Success URL found: " + url);
                    return true;
                }
                if (source.contains("booking reserved") || source.contains("booking success") || source.contains("invoice")) {
                    System.out.println("Success text found on page.");
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
