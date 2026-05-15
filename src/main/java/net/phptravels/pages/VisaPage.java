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
        
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'form-control') and .//label[contains(., '" + labelText + "')]]")));
        
        WebElement trigger = container.findElement(By.cssSelector("div.input-dropdown div.input"));
        wait.until(ExpectedConditions.elementToBeClickable(trigger)).click();
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOf(
            container.findElement(By.cssSelector("input[x-ref='searchInput']"))));
        
        searchInput.clear();
        for (char ch : country.toCharArray()) {
            searchInput.sendKeys(String.valueOf(ch));
        }

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        By optionLocator = By.cssSelector(".input-dropdown-item");
        List<WebElement> options = container.findElements(optionLocator);
        
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().trim().equalsIgnoreCase(country) || 
                option.getText().trim().contains(country)) {
                wait.until(ExpectedConditions.elementToBeClickable(option)).click();
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new RuntimeException("Country '" + country + "' not found in '" + labelText + "' dropdown");
        }
    }

    public void setDate(String date) {
        try {
            type(dateInput, date);
        } catch (Exception e) {
            executeJS("arguments[0].value = '" + date + "';", dateInput);
        }
    }

    public void clickSearch() {
        click(submitButton);
    }

    public void clearDate() {
        executeJS("arguments[0].value = '';", dateInput);
    }

    public String getErrorMessage() {
        WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-error p")));
        return errorAlert.getText();
    }
}