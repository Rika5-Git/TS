package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfileSettingsPage extends BasePage {

    @FindBy(name = "phone")
    private WebElement phoneInput;

    @FindBy(name = "state")
    private WebElement stateInput;

    @FindBy(name = "po_box")
    private WebElement poBoxInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(xpath = "//button[contains(., 'Update Profile')]")
    private WebElement updateButton;

    public ProfileSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void updateProfileDetails(String phone, String state, String address) {
        waitForLoader();
        
        // Очищаємо та вводимо нові дані
        executeJS("arguments[0].value = '';", phoneInput);
        phoneInput.sendKeys(phone);

        executeJS("arguments[0].value = '';", stateInput);
        stateInput.sendKeys(state);

        executeJS("arguments[0].value = '';", addressInput);
        addressInput.sendKeys(address);

        // Натискаємо кнопку оновлення
        clickJS(updateButton);
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public String getPhoneValue() {
        return phoneInput.getAttribute("value");
    }

    public String getStateValue() {
        return stateInput.getAttribute("value");
    }

    public String getAddressValue() {
        return addressInput.getAttribute("value");
    }

    public void logout() {
        // Відкриваємо меню користувача
        executeJS("var btns = document.querySelectorAll('button'); for(var i=0; i<btns.length; i++) { var attr = btns[i].getAttribute('@click'); if(attr && attr.includes('mobileUser')) { btns[i].click(); break; } }");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        // Клікаємо Logout
        executeJS("var links = document.querySelectorAll('a'); for(var i=0; i<links.length; i++) { if(links[i].href.includes('/logout')) { links[i].click(); break; } }");
        waitForLoader();
    }
}
