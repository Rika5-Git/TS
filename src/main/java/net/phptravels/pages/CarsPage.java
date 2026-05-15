package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CarsPage extends BasePage {

    @FindBy(css = "input[placeholder='City or Airport']")
    private WebElement pickupInput;

    @FindBy(xpath = "//input[@placeholder='Same As Pick-up' or @x-ref='dropoffInput']")
    private WebElement dropoffInput;

    @FindBy(xpath = "//button[@type='submit' and contains(., 'Search')]")
    private WebElement searchButton;

    @FindBy(name = "pickup_date")
    private WebElement pickupDateInput;

    @FindBy(name = "return_date")
    private WebElement returnDateInput;

    @FindBy(name = "pickup_time")
    private WebElement pickupTimeInput;

    @FindBy(name = "return_time")
    private WebElement returnTimeInput;

    @FindBy(xpath = "//div[contains(@class, 'input') and .//span[contains(text(), 'Rental') or contains(text(), 'Transfer')]]")
    private WebElement serviceTypeDropdown;

    public CarsPage(WebDriver driver) {
        super(driver);
    }

    public void searchCars(String pickupLocation, String dropoffLocation) {
        waitForLoader();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        
        scrollToElement(serviceTypeDropdown);
        String currentService = serviceTypeDropdown.getText();
        
        if (!currentService.contains("Car Rental")) {
            clickJS(serviceTypeDropdown);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            try {
                WebElement rentalOption = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'input-dropdown-item')]//div[contains(., 'Car Rental')]")));
                clickJS(rentalOption);
            } catch (Exception e) {
                executeJS("var items = document.querySelectorAll('.input-dropdown-item'); items.forEach(el => { if(el.innerText.includes('Car Rental')) el.click(); });");
            }
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        scrollToElement(pickupInput);
        type(pickupInput, pickupLocation);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        By pickupResult = By.xpath("//div[contains(@class, 'cursor-pointer')]//div[contains(text(), 'Dubai International Airport')]");
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(pickupResult));
            clickJS(element);
        } catch (Exception e) {
            executeJS("document.querySelectorAll('.cursor-pointer').forEach(el => { if(el.innerText.includes('Dubai International')) el.click(); });");
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        type(dropoffInput, dropoffLocation);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        By dropoffResult = By.xpath("//div[contains(@class, 'cursor-pointer')]//div[contains(text(), 'Dubai International Airport')]");
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dropoffResult));
            clickJS(element);
        } catch (Exception e) {
            executeJS("var items = document.querySelectorAll('.cursor-pointer'); items.forEach(el => { if(el.innerText.includes('Dubai International')) el.click(); });");
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        executeJS("arguments[0].value = '25-05-2026';", pickupDateInput);
        executeJS("arguments[0].value = '30-05-2026';", returnDateInput);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        selectTime(pickupTimeInput, "12");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        selectTime(returnTimeInput, "15");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        
        clickJS(searchButton);
        waitForLoader();
    }

    private void selectTime(WebElement timeInput, String hour) {
        clickJS(timeInput);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        try {
            WebElement hourEl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-hour='" + hour + "']")));
            clickJS(hourEl);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            
            try {
                WebElement minutesTab = driver.findElement(By.className("tp-tab-minutes"));
                clickJS(minutesTab);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            } catch (Exception ignored) {}

            WebElement minutesEl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'tp-minute') and text()='00']")));
            clickJS(minutesEl);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            
        } catch (Exception e) {
            executeJS("arguments[0].value = '" + hour + ":00';", timeInput);
        }
        executeJS("document.body.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));");
        executeJS("document.body.click();");
    }

    @FindBy(xpath = "//input[@x-model='minPrice']")
    private WebElement minPriceInput;

    @FindBy(xpath = "//input[@x-model='maxPrice']")
    private WebElement maxPriceInput;

    public void applyFilters() {
        waitForLoader();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        try {
            scrollToElement(minPriceInput);
            type(minPriceInput, "10");
            type(maxPriceInput, "1000");
            executeJS("arguments[0].dispatchEvent(new Event('change'));", minPriceInput);
            executeJS("arguments[0].dispatchEvent(new Event('change'));", maxPriceInput);
            waitForLoader();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            WebElement autoEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(., 'Automatic')]")));
            scrollToElement(autoEl);
            clickJS(autoEl);
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            
            WebElement suvEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(., 'SUV')]")));
            scrollToElement(suvEl);
            clickJS(suvEl);
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            
        } catch (Exception e) {
            executeJS("document.getElementById('trans-auto').click();");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            executeJS("document.getElementById('cartype-suv').click();");
        }
    }

    public void selectFirstCar() {
        By bookNowButtons = By.xpath("//button[contains(., 'Book Now')]");
        WebElement firstButton = wait.until(ExpectedConditions.elementToBeClickable(bookNowButtons));
        clickJS(firstButton);
        waitForLoader();
    }
}

