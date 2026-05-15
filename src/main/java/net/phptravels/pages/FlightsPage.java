package net.phptravels.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FlightsPage extends BasePage {

    @FindBy(css = "input[x-ref='fromInput']")
    private WebElement fromInput;

    @FindBy(css = "input[x-ref='toInput']")
    private WebElement toInput;

    @FindBy(name = "flights_departure_date")
    private WebElement departureDate;

    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;

    // Trigger divs for Alpine.js dropdowns
    @FindBy(xpath = "//div[contains(@class, 'input') and .//span[contains(text(), 'One Way') or contains(text(), 'Round Trip')]]")
    private WebElement flightTypeDropdown;

    @FindBy(xpath = "//div[contains(@class, 'input') and .//span[contains(text(), 'Economy') or contains(text(), 'Business') or contains(text(), 'First Class') or contains(text(), 'Economy Premium')]]")
    private WebElement flightClassDropdown;

    @FindBy(xpath = "//div[contains(@class, 'input') and .//span[contains(text(), 'Passenger')]]")
    private WebElement passengersDropdown;

    public FlightsPage(WebDriver driver) {
        super(driver);
    }

    public void setFrom(String city) {
        System.out.println("Setting departure: " + city);
        waitForLoader();
        scrollToElement(fromInput);
        
        executeJS("arguments[0].click(); arguments[0].value = '';", fromInput);
        fromInput.sendKeys(city);
        
        // Wait for results to be fetched and displayed
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {} 
        
        // Selecting result by searching text in divs that have selectFrom click handler
        boolean selected = (Boolean) executeJS(
            "var items = document.querySelectorAll('div');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].getAttribute('@click') && items[i].getAttribute('@click').includes('selectFrom')) {" +
            "    if(items[i].innerText.toLowerCase().includes('" + city.toLowerCase() + "')) {" +
            "      items[i].click(); return true;" +
            "    }" +
            "  }" +
            "}" +
            "return false;"
        );

        if (!selected) {
            System.out.println("Precise JS selection failed, trying first available result...");
            executeJS("var items = document.querySelectorAll('div'); for(var i=0; i<items.length; i++) { if(items[i].getAttribute('@click') && items[i].getAttribute('@click').includes('selectFrom')) { items[i].click(); break; } }");
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void setTo(String city) {
        System.out.println("Setting arrival: " + city);
        waitForLoader();
        scrollToElement(toInput);
        
        executeJS("arguments[0].click(); arguments[0].value = '';", toInput);
        toInput.sendKeys(city);
        
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {} 
        
        boolean selected = (Boolean) executeJS(
            "var items = document.querySelectorAll('div');" +
            "for(var i=0; i<items.length; i++) {" +
            "  if(items[i].getAttribute('@click') && items[i].getAttribute('@click').includes('selectTo')) {" +
            "    if(items[i].innerText.toLowerCase().includes('" + city.toLowerCase() + "')) {" +
            "      items[i].click(); return true;" +
            "    }" +
            "  }" +
            "}" +
            "return false;"
        );

        if (!selected) {
            executeJS("var items = document.querySelectorAll('div'); for(var i=0; i<items.length; i++) { if(items[i].getAttribute('@click') && items[i].getAttribute('@click').includes('selectTo')) { items[i].click(); break; } }");
        }
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void setFlightDetails(String type, String flightClass, int adults, int children) {
        System.out.println("--- Setting Flight Details: " + type + ", " + flightClass + " ---");
        
        // 1. Flight Type Dropdown
        executeJS(
            "var spans = document.querySelectorAll('span');" +
            "for(var i=0; i<spans.length; i++) {" +
            "  if(spans[i].innerText.includes('One Way') || spans[i].innerText.includes('Round Trip')) {" +
            "    spans[i].parentElement.click();" + // Відкриваємо дропдаун
            "    break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + type + "')) { items[i].click(); break; } }");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // 2. Flight Class Dropdown
        executeJS(
            "var spans = document.querySelectorAll('span');" +
            "for(var i=0; i<spans.length; i++) {" +
            "  if(spans[i].innerText === 'Economy' || spans[i].innerText.includes('Business') || spans[i].innerText.includes('First Class')) {" +
            "    spans[i].parentElement.click();" + // Відкриваємо дропдаун
            "    break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + flightClass + "')) { items[i].click(); break; } }");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // 3. Passengers
        for (int i = 1; i < adults; i++) {
            executeJS("var btns = document.querySelectorAll('button'); for(var i=0; i<btns.length; i++) { if(btns[i].getAttribute('@click') && btns[i].getAttribute('@click').includes(\"increment('adults')\")) { btns[i].click(); break; } }");
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
        for (int i = 0; i < children; i++) {
            executeJS("var btns = document.querySelectorAll('button'); for(var i=0; i<btns.length; i++) { if(btns[i].getAttribute('@click') && btns[i].getAttribute('@click').includes(\"increment('children')\")) { btns[i].click(); break; } }");
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
    }

    public void setDepartureDate(String date) {
        System.out.println("Setting departure date: " + date);
        executeJS("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", departureDate, date);
        
        // КРИТИЧНО ВАЖЛИВО: Календар перекриває інші елементи. Примусово ховаємо його через JS.
        executeJS("var dp = document.querySelector('.datepicker'); if(dp) { dp.style.display = 'none'; dp.style.opacity = '0'; }");
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void setReturnDate(String date) {
        System.out.println("Setting return date: " + date);
        executeJS("var el = document.querySelector('.FlightsArrival'); if(el) { el.value = '" + date + "'; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }");
        
        // Ховаємо календар
        executeJS("var dp = document.querySelector('.datepicker'); if(dp) { dp.style.display = 'none'; dp.style.opacity = '0'; }");
        executeJS("document.body.click();");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public void clickSearch() {
        System.out.println("Clicking Search Flights...");
        clickJS(searchButton);
        waitForLoader();
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {} // Wait for results page to load
    }

    public void applyFilters(String stops, String timeSlot) {
        waitForLoader();
        System.out.println("--- Applying Filters: " + stops + ", " + timeSlot + " ---");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        try {
            // 1. Stops Filter
            String stopIndex = "0";
            if (stops.contains("1")) stopIndex = "1";
            if (stops.contains("2")) stopIndex = "2";
            
            executeJS("var el = document.getElementById('stop-' + arguments[0]); if(el) { el.click(); el.dispatchEvent(new Event('change', { bubbles: true })); }", stopIndex);
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            // 2. Time Slot Filter
            String timeId = "time-morning";
            if (timeSlot.equalsIgnoreCase("Early Morning")) timeId = "time-early";
            if (timeSlot.equalsIgnoreCase("Afternoon")) timeId = "time-afternoon";
            if (timeSlot.equalsIgnoreCase("Evening")) timeId = "time-evening";
            
            executeJS("var el = document.getElementById('" + timeId + "'); if(el) { el.click(); el.dispatchEvent(new Event('change', { bubbles: true })); }");
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            
            System.out.println("Filters applied successfully via JS.");
        } catch (Exception e) {
            System.out.println("Filter application failed: " + e.getMessage());
        }
    }

    public void selectFirstFlight() {
        System.out.println("Selecting first available flight...");
        
        // Додаткове очікування для завершення анімацій фільтрації (Alpine.js crossfade)
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        
        boolean selected = (Boolean) executeJS(
            "var buttons = document.querySelectorAll('button');" +
            "for(var i=0; i<buttons.length; i++) {" +
            "  var text = buttons[i].innerText.toLowerCase();" +
            "  if(text.includes('select') || text.includes('book now')) {" +
            "    buttons[i].click();" +
            "    return true;" +
            "  }" +
            "}" +
            "return false;"
        );

        if (!selected) {
            System.out.println("Could not find Select button via JS. Trying fallback link click...");
            executeJS("var links = document.querySelectorAll('a'); for(var i=0; i<links.length; i++) { if(links[i].href && links[i].href.includes('/checkout/')) { links[i].click(); break; } }");
        }
        
        waitForLoader();
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {} // Чекаємо переходу на сторінку бронювання
    }
}
