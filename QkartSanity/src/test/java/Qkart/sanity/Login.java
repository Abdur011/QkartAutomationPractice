package Qkart.sanity;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class Login {

    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/login";

    // ✅ Constructor now accepts WebDriver
    public Login(WebDriver driver) {
        this.driver = driver;
    }

    // ✅ Navigate to login page if not already there
    public void navigateToLoginPage() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Perform login using username and password
     */
    public Boolean PerformLogin(String username, String password) {
        try {
            // Find and fill username
            WebElement usernameField = driver.findElement(By.id("username"));
            usernameField.clear();
            usernameField.sendKeys(username);

            // Find and fill password
            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.clear();
            passwordField.sendKeys(password);

            // Click Login button
            WebElement loginButton = driver.findElement(By.className("button"));
            loginButton.click();

            // ✅ Wait until username label appears (user successfully logged in)
            Wait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("username-text")));

            // Verify successful login
            return VerifyUserLoggedIn(username);
        } catch (Exception e) {
            System.out.println("Exception during login: " + e.getMessage());
            return false;
        }
    }

    /*
     * Verify username text in the header (top-right corner)
     */
    public Boolean VerifyUserLoggedIn(String username) {
        try {
            WebElement usernameLabel = driver.findElement(By.className("username-text"));
            return usernameLabel.getText().equals(username);
        } catch (Exception e) {
            System.out.println("Error verifying login status: " + e.getMessage());
            return false;
        }
    }
}
