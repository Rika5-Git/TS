package net.phptravels.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String email, String password) {
        waitForLoader();
        type(emailInput, email);
        type(passwordInput, password);
        dismissModals();
        click(loginButton);
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("login");
    }
}