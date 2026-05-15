package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MainPage extends BasePage {

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public void openFlights() {
        try {
            waitForLoader();
            WebElement servicesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Services']]")));
            scrollToElement(servicesButton);
            clickJS(servicesButton);
            
            WebElement flightsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, '/flights') and (contains(., 'Flights') or contains(., 'flights'))]")));
            clickJS(flightsLink);
        } catch (Exception e) {
            driver.get("https://phptravels.net/flights");
        }
        waitForLoader();
    }

    public void openVisa() {
        driver.get("https://phptravels.net/visa");
        waitForLoader();
    }

    public void openCars() {
        try {
            waitForLoader();
            WebElement servicesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Services']]")));
            scrollToElement(servicesButton);
            clickJS(servicesButton);

            WebElement carsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, '/cars') and (contains(., 'Cars') or contains(., 'cars'))]")));
            clickJS(carsLink);
        } catch (Exception e) {
            driver.get("https://phptravels.net/cars");
        }
        waitForLoader();
    }
}