package net.phptravels.tests;

import net.phptravels.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTest {

    @Test
    public void testLoginRedirect() {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isOnLoginPage(), "Login page URL is incorrect or not redirected properly");
    }

    @Test
    public void testInvalidLoginMessage() throws InterruptedException {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("wrong@user.com", "wrongpassword");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert, .alert-danger, .alert-error, .message-error, .alert-success")));
        
        String errorText = errorElement.getText();
        System.out.println("Detected error message: " + errorText);
        
        Thread.sleep(3000);
        
        assertTrue(errorText.contains("Invalid") || errorText.contains("Wrong") || errorText.contains("failed"), 
                "Error message was found but didn't contain expected keywords. Text: " + errorText);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/login_data.csv", numLinesToSkip = 1)
    public void testLoginNegativeData(String email, String password) throws InterruptedException {
        driver.get("https://phptravels.net/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert, .alert-danger, .alert-error")));
        } catch (Exception e) {
            assertTrue(driver.getCurrentUrl().contains("login"), "Should still be on login page after failure");
        }

        Thread.sleep(2000);

        boolean isDashboard = driver.getCurrentUrl().contains("/account") || driver.getCurrentUrl().contains("/dashboard");
        assertTrue(!isDashboard, "CRITICAL: System allowed login with invalid credentials for: " + email);
    }
}