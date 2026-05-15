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

    public void goToMyCarsBookings() {
        System.out.println("Navigating to Profile -> My Bookings -> Cars...");
        
        // 1. Відкриваємо дропдаун акаунта
        scrollToElement(accountDropdown);
        clickJS(accountDropdown);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 2. Переходимо в Profile
        clickJS(profileLink);
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 3. Відкриваємо акордеон My Bookings
        scrollToElement(myBookingsAccordion);
        clickJS(myBookingsAccordion);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 4. Тиснемо на Cars
        clickJS(carsBookingsLink);
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public boolean isLastBookingPresent() {
        // Перевіряємо наявність кнопки "View" (око) для останнього бронювання
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
