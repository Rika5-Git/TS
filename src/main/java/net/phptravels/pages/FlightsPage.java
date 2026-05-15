package net.phptravels.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FlightsPage extends BasePage {

    @FindBy(css = "input[x-ref='fromInput']")
    private WebElement fromInput;

    @FindBy(css = "input[x-ref='toInput']")
    private WebElement toInput;

    @FindBy(name = "flights_departure_date")
    private WebElement departureDate;

    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;

    public FlightsPage(WebDriver driver) {
        super(driver);
    }

    public void setFrom(String city) {
        waitForLoader();
        scrollToElement(fromInput);
        
        executeJS("arguments[0].click(); arguments[0].value = '';", fromInput);
        fromInput.sendKeys(city);
        
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {} 
        
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
            executeJS("var items = document.querySelectorAll('div'); for(var i=0; i<items.length; i++) { if(items[i].getAttribute('@click') && items[i].getAttribute('@click').includes('selectFrom')) { items[i].click(); break; } }");
        }
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public void setTo(String city) {
        waitForLoader();
        scrollToElement(toInput);
        
        executeJS("arguments[0].click(); arguments[0].value = '';", toInput);
        toInput.sendKeys(city);
        
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {} 
        
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
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    public void setFlightDetails(String type, String flightClass, int adults, int children) {
        executeJS(
            "var spans = document.querySelectorAll('span');" +
            "for(var i=0; i<spans.length; i++) {" +
            "  if(spans[i].innerText.includes('One Way') || spans[i].innerText.includes('Round Trip')) {" +
            "    spans[i].parentElement.click();" +
            "    break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + type + "')) { items[i].click(); break; } }");
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        executeJS(
            "var spans = document.querySelectorAll('span');" +
            "for(var i=0; i<spans.length; i++) {" +
            "  if(spans[i].innerText === 'Economy' || spans[i].innerText.includes('Business') || spans[i].innerText.includes('First Class')) {" +
            "    spans[i].parentElement.click();" + 
            "    break;" +
            "  }" +
            "}"
        );
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        executeJS("var items = document.querySelectorAll('.input-dropdown-item'); for(var i=0; i<items.length; i++) { if(items[i].innerText.includes('" + flightClass + "')) { items[i].click(); break; } }");
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        for (int i = 1; i < adults; i++) {
            executeJS("var btns = document.querySelectorAll('button'); for(var i=0; i<btns.length; i++) { if(btns[i].getAttribute('@click') && btns[i].getAttribute('@click').includes(\"increment('adults')\")) { btns[i].click(); break; } }");
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
        for (int i = 0; i < children; i++) {
            executeJS("var btns = document.querySelectorAll('button'); for(var i=0; i<btns.length; i++) { if(btns[i].getAttribute('@click') && btns[i].getAttribute('@click').includes(\"increment('children')\")) { btns[i].click(); break; } }");
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
    }

    public void setDepartureDate(String date) {
        executeJS("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", departureDate, date);
        executeJS("var dp = document.querySelector('.datepicker'); if(dp) { dp.style.display = 'none'; dp.style.opacity = '0'; }");
        executeJS("document.body.click();");
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
    }

    public void setReturnDate(String date) {
        executeJS("var el = document.querySelector('.FlightsArrival'); if(el) { el.value = '" + date + "'; el.dispatchEvent(new Event('input', { bubbles: true })); el.dispatchEvent(new Event('change', { bubbles: true })); }");
        executeJS("var dp = document.querySelector('.datepicker'); if(dp) { dp.style.display = 'none'; dp.style.opacity = '0'; }");
        executeJS("document.body.click();");
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
    }

    public void clickSearch() {
        clickJS(searchButton);
        waitForLoader();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public void applyFilters(String stops, String timeSlot) {
        waitForLoader();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try {
            executeJS(
                "var labels = document.querySelectorAll('label');" +
                "for(var i=0; i<labels.length; i++) {" +
                "  if(labels[i].innerText.includes('" + stops + "')) {" +
                "    labels[i].click();" +
                "    break;" +
                "  }" +
                "}"
            );
            waitForLoader();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

            executeJS(
                "var labels = document.querySelectorAll('label');" +
                "for(var i=0; i<labels.length; i++) {" +
                "  if(labels[i].innerText.toLowerCase().includes('" + timeSlot.toLowerCase() + "')) {" +
                "    labels[i].click();" +
                "    break;" +
                "  }" +
                "}"
            );
            waitForLoader();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        } catch (Exception ignored) {}
    }

    public boolean selectFirstFlight() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        boolean noResults = (Boolean) executeJS(
            "return document.body.innerText.toLowerCase().includes('no flights found') || " +
            "document.querySelectorAll('.flight-card').length === 0;"
        );

        if (noResults) {
            return false;
        }

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
            selected = (Boolean) executeJS(
                "var links = document.querySelectorAll('a');" +
                "for(var i=0; i<links.length; i++) {" +
                "  if(links[i].href && links[i].href.includes('/checkout/')) {" +
                "    links[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;"
            );
        }
        
        if (selected) {
            waitForLoader();
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
        
        return selected;
    }
}

