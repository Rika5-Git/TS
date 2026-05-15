# PHPTravels Selenium Testing Project

Tato semestrální práce se zaměřuje na automatizované E2E testování platformy `https://phptravels.net`.

## Přehled projektu
*   **Technologie:** Java 17, Selenium WebDriver 4, JUnit 5, Maven.
*   **Architektura:** Page Object Pattern (POP) pro jasné oddělení logiky stránek od testovacích scénářů.
*   **Klíčové funkce:**
    *   Automatizované testy pro vyhledávání letenek, vízové procesy a přihlašování uživatelů.
    *   Data-driven testing (DDT) s využitím CSV souborů pro parametrizované testy (3 scénáře).
    *   Celkem 10 robustních Selenium testů, splňujících požadavky semestrální práce.
    *   Pokrytí úspěšných průchodů, negativních scénářů a kontrola chybových hlášení.

## Struktura projektu
*   `src/main/java/net/phptravels/pages/`: Obsahuje třídy Page Object reprezentující jednotlivé stránky.
    *   `BasePage.java`: Společné utility pro interakci s elementy (čekání, klikání, psaní).
    *   `MainPage.java`, `FlightsPage.java`, `VisaPage.java`, `LoginPage.java`: Specifická logika pro formuláře.
*   `src/test/java/net/phptravels/tests/`: Obsahuje testovací sady.
    *   `BaseTest.java`: Nastavení a ukončení WebDriveru (Chrome) + automatické snímky obrazovky (screenshots) při selhání.
    *   `FlightsTest.java`, `VisaTest.java`, `LoginTest.java`: Funkční a procesní testy.
*   `src/test/resources/`: Obsahuje testovací data ve formátu CSV.
*   `Lekce/`: Obsahuje původní zadání a požadavky.

## Sestavení a spuštění
Projekt využívá Maven. Ujistěte se, že máte nainstalovaný Google Chrome.

*   **Spuštění všech testů:**
    ```bash
    mvn test
    ```
*   **Spuštění konkrétní třídy:**
    ```bash
    mvn test -Dtest=VisaTest
    ```

## Metodika testování
*   **Page Object Pattern:** Všechny lokátory a interakce jsou uloženy v třídách stránek.
*   **Robustnost:** Využití explicitního čekání (WebDriverWait) a automatické ukládání snímků obrazovky při chybě do `target/screenshots`.
*   **Analýza dat:** Využití ekvivalentních tříd (EC) a hraničních hodnot (BVA) pro vstupy ve formulářích Flights a Visa.