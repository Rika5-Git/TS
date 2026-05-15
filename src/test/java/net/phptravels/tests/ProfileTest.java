package net.phptravels.tests;

import net.phptravels.pages.LoginPage;
import net.phptravels.pages.ProfileSettingsPage;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileTest extends BaseTest {

    /**
     * МЕГА-СКЛАДНИЙ ТЕСТ: Перевірка збереження даних профілю (CRUD + Session Persistence)
     * Сценарій: Login -> Update Profile -> Logout -> Login -> Verify Data.
     */
    @Test
    public void testProfileDataPersistence() {
        LoginPage loginPage = new LoginPage(driver);
        ProfileSettingsPage profilePage = new ProfileSettingsPage(driver);

        // 1. Авторизація
        driver.get("https://phptravels.net/login");
        loginPage.login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 2. Генерація унікальних даних на основі часу
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String newPhone = "123" + timestamp;
        String newState = "State_" + timestamp;
        String newAddress = "Test Address " + timestamp;

        // 3. Оновлення профілю
        driver.get("https://phptravels.net/profile");
        profilePage.updateProfileDetails(newPhone, newState, newAddress);

        // 4. Logout (Скидання сесії)
        profilePage.logout();
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 5. Повторний Login
        driver.get("https://phptravels.net/login");
        loginPage.login("user@phptravels.com", "demouser");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // 6. Верифікація даних після перезаходу
        driver.get("https://phptravels.net/profile");
        
        assertEquals(newPhone, profilePage.getPhoneValue(), "Phone number should persist after logout");
        assertEquals(newState, profilePage.getStateValue(), "State should persist after logout");
        assertEquals(newAddress, profilePage.getAddressValue(), "Address should persist after logout");
    }
}
