package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

    @FindBy(xpath = "//button[contains(., 'Demo User')]")
    private WebElement accountDropdown;

    @FindBy(xpath = "//a[contains(@href, 'profile')]//span[contains(., 'Profile')]")
    private WebElement profileLink;

    @FindBy(xpath = "//span[contains(text(), 'My Bookings')]/ancestor::div[contains(@onclick, 'accordion1')]")
    private WebElement myBookingsAccordion;

    @FindBy(xpath = "//a[contains(@href, 'filter=cars')]")
    private WebElement carsBookingsLink;

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public void goToVisaBookings() {
        // 1. Открываем меню пользователя ( Demo User )
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if(attr && attr.includes(\"toggleDropdown('mobileUser')\")) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 2. Клик на Dashboard
        executeJS("var links = document.querySelectorAll('a'); for(var i=0; i<links.length; i++) { if(links[i].href.includes('/dashboard')) { links[i].click(); break; } }");
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 3. Раскрываем My Bookings (аккордеон)
        executeJS("var accordion = document.querySelector('div[onclick*=\"toggleAccordion(\\'accordion1\\')\"]'); if(accordion) accordion.click();");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 4. Кликаем на Visa
        executeJS("var visaLink = document.querySelector('a[href*=\"filter=visa\"]'); if(visaLink) visaLink.click();");
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public void viewLastVisaInvoice() {
        // Кликаем на первую иконку глаза (View) в списке виз
        executeJS(
            "var links = document.querySelectorAll('a[title=\"View\"]');" +
            "if(links.length > 0) { links[0].scrollIntoView({behavior: 'auto', block: 'center'}); links[0].click(); }"
        );
        waitForLoader();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }

    public boolean isInvoiceVisible() {
        return driver.getCurrentUrl().contains("invoice") || driver.getPageSource().contains("Invoice");
    }

    public void goToMyCarsBookings() {
        // 1. Открываем меню пользователя ( Demo User )
        executeJS(
            "var btns = document.querySelectorAll('button');" +
            "for(var i=0; i<btns.length; i++) {" +
            "  var attr = btns[i].getAttribute('@click');" +
            "  if(attr && attr.includes(\"toggleDropdown('mobileUser')\")) {" +
            "    btns[i].click(); break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 2. Клик на Dashboard
        executeJS("var links = document.querySelectorAll('a'); for(var i=0; i<links.length; i++) { if(links[i].href.includes('/dashboard')) { links[i].click(); break; } }");
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 3. Раскрываем My Bookings (аккордеон)
        executeJS("var accordion = document.querySelector('div[onclick*=\"toggleAccordion(\\'accordion1\\')\"]'); if(accordion) accordion.click();");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 4. Кликаем на Cars
        executeJS("var carsLink = document.querySelector('a[href*=\"filter=cars\"]'); if(carsLink) carsLink.click();");
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public boolean isLastBookingPresent() {
        try {
            WebElement viewButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//a[contains(@href, 'invoice')])[1]")));
            return viewButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void viewLastInvoice() {
        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//a[contains(@href, 'invoice')])[1]")));
        clickJS(viewButton);
        waitForLoader();
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
    }
}
