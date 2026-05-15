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
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        // 1. Вибираємо Service Type (Car Rental), тільки якщо він ще не вибраний
        System.out.println("Checking Service Type...");
        scrollToElement(serviceTypeDropdown);
        String currentService = serviceTypeDropdown.getText();
        
        if (!currentService.contains("Car Rental")) {
            System.out.println("Changing Service Type to Car Rental...");
            clickJS(serviceTypeDropdown);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            try {
                WebElement rentalOption = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'input-dropdown-item')]//div[contains(., 'Car Rental')]")));
                clickJS(rentalOption);
            } catch (Exception e) {
                executeJS("var items = document.querySelectorAll('.input-dropdown-item'); items.forEach(el => { if(el.innerText.includes('Car Rental')) el.click(); });");
            }
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        } else {
            System.out.println("Service Type is already Car Rental. Skipping...");
        }

        // 2. Заповнюємо Pick-up Location
        System.out.println("Setting Pick-up Location...");
        scrollToElement(pickupInput);
        type(pickupInput, pickupLocation);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        By pickupResult = By.xpath("//div[contains(@class, 'cursor-pointer')]//div[contains(text(), 'Dubai International Airport')]");
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(pickupResult));
            clickJS(element);
        } catch (Exception e) {
            executeJS("document.querySelectorAll('.cursor-pointer').forEach(el => { if(el.innerText.includes('Dubai International')) el.click(); });");
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 3. Заповнюємо Return Location
        System.out.println("Setting Return Location...");
        type(dropoffInput, dropoffLocation);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        By dropoffResult = By.xpath("//div[contains(@class, 'cursor-pointer')]//div[contains(text(), 'Dubai International Airport')]");
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dropoffResult));
            clickJS(element);
        } catch (Exception e) {
            executeJS("var items = document.querySelectorAll('.cursor-pointer'); items.forEach(el => { if(el.innerText.includes('Dubai International')) el.click(); });");
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // 4. Встановлюємо дати
        System.out.println("Setting dates...");
        executeJS("arguments[0].value = '25-05-2026';", pickupDateInput);
        executeJS("arguments[0].value = '30-05-2026';", returnDateInput);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // 5. Встановлюємо час (Pick-up 12:00, Return 15:00)
        System.out.println("Setting times...");
        selectTime(pickupTimeInput, "12");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        // Щоб закрити таймпікер, клікнемо по заголовку або десь збоку
        executeJS("document.body.click();");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        selectTime(returnTimeInput, "15");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        // Закриваємо фінальне вікно перед пошуком
        executeJS("document.body.click();");
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        
        System.out.println("Clicking Search...");
        clickJS(searchButton);
        waitForLoader();
    }

    private void selectTime(WebElement timeInput, String hour) {
        clickJS(timeInput); // Відкриваємо таймпікер
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        try {
            // 1. Вибираємо годину
            WebElement hourEl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-hour='" + hour + "']")));
            clickJS(hourEl);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            
            // 2. Тиснемо кнопку "Minutes", щоб переключитись (якщо потрібно)
            try {
                WebElement minutesTab = driver.findElement(By.className("tp-tab-minutes"));
                clickJS(minutesTab);
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            } catch (Exception ignored) {}

            // 3. Вибираємо "00" хвилин, щоб закрити вікно (зазвичай після вибору хвилин воно закривається)
            WebElement minutesEl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'tp-minute') and text()='00']")));
            clickJS(minutesEl);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            
        } catch (Exception e) {
            System.out.println("Could not select time via UI, using JS fallback.");
            executeJS("arguments[0].value = '" + hour + ":00';", timeInput);
        }
        // Фінальний клік по body для впевненості, що вікно зникло
        executeJS("document.body.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));");
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    @FindBy(xpath = "//input[@x-model='minPrice']")
    private WebElement minPriceInput;

    @FindBy(xpath = "//input[@x-model='maxPrice']")
    private WebElement maxPriceInput;

    public void applyFilters() {
        waitForLoader();
        System.out.println("--- Starting to apply filters ---");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        try {
            // 1. Встановлюємо ціну
            System.out.println("Setting price range: 10 - 1000");
            scrollToElement(minPriceInput);
            type(minPriceInput, "10");
            type(maxPriceInput, "1000");
            executeJS("arguments[0].dispatchEvent(new Event('change'));", minPriceInput);
            executeJS("arguments[0].dispatchEvent(new Event('change'));", maxPriceInput);
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // 2. Вибираємо Automatic
            System.out.println("Selecting 'Automatic' filter...");
            WebElement autoEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(., 'Automatic')]")));
            scrollToElement(autoEl);
            clickJS(autoEl);
            waitForLoader();
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            
            // 3. Вибираємо SUV
            System.out.println("Selecting 'SUV' filter...");
            WebElement suvEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(., 'SUV')]")));
            scrollToElement(suvEl);
            clickJS(suvEl);
            waitForLoader();
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            
            System.out.println("Filters applied successfully.");
        } catch (Exception e) {
            System.out.println("Error applying filters via UI, trying JS fallback...");
            executeJS("document.getElementById('trans-auto').click();");
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            executeJS("document.getElementById('cartype-suv').click();");
            System.out.println("Filters applied via JS fallback.");
        }
    }

    public void selectFirstCar() {
        By bookNowButtons = By.xpath("//button[contains(., 'Book Now')]");
        WebElement firstButton = wait.until(ExpectedConditions.elementToBeClickable(bookNowButtons));
        clickJS(firstButton);
        waitForLoader();
    }
}
