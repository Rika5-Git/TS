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
            System.out.println("Attempting to open Flights via Services menu...");
            waitForLoader();
            WebElement servicesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Services']]")));
            scrollToElement(servicesButton);
            clickJS(servicesButton);
            
            WebElement flightsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, '/flights') and (contains(., 'Flights') or contains(., 'flights'))]")));
            clickJS(flightsLink);
            System.out.println("Flights page opened via menu.");
        } catch (Exception e) {
            System.out.println("Dropdown navigation failed: " + e.getMessage() + ". Using direct URL fallback.");
            driver.get("https://phptravels.net/flights");
        }
        waitForLoader();
    }

    public void openVisa() {
        System.out.println("Opening Visa page...");
        driver.get("https://phptravels.net/visa");
        waitForLoader();
    }

    public void openCars() {
        try {
            System.out.println("Attempting to open Cars via Services menu...");
            waitForLoader();
            WebElement servicesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Services']]")));
            scrollToElement(servicesButton);
            clickJS(servicesButton);

            WebElement carsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, '/cars') and (contains(., 'Cars') or contains(., 'cars'))]")));
            clickJS(carsLink);
            System.out.println("Cars page opened via menu.");
        } catch (Exception e) {
            System.out.println("Dropdown navigation failed: " + e.getMessage() + ". Using direct URL fallback.");
            driver.get("https://phptravels.net/cars");
        }
        waitForLoader();
    }
}