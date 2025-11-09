package Qkart.sanity;

import java.sql.Timestamp;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Register {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/register";
    public String lastGeneratedUsername = "";

    // ✅ Constructor now uses WebDriver
    public Register(WebDriver driver) {
        this.driver = driver;
    }

    // ✅ Navigate to registration page if not already there
    public void navigateToRegisterPage() {
        if (!driver.getCurrentUrl().equals(this.url)) {
            driver.get(this.url);
        }
    }

    /*
     * Registers a new user and returns true if registration succeeds.
     * makeUsernameDynamic → adds timestamp suffix for unique username.
     */
    public Boolean registerUser(String username, String password, Boolean makeUsernameDynamic) {
        try {
            // Locate username textbox
            WebElement usernameBox = driver.findElement(By.id("username"));

            // Generate dynamic username if needed
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String finalUsername = makeUsernameDynamic
                    ? username + "_" + timestamp.getTime()
                    : username;

            // Type username
            usernameBox.clear();
            usernameBox.sendKeys(finalUsername);

            // Fill password and confirm password
            WebElement passwordBox = driver.findElement(By.id("password"));
            WebElement confirmPasswordBox = driver.findElement(By.id("confirmPassword"));
            passwordBox.sendKeys(password);
            confirmPasswordBox.sendKeys(password);

            // Click on "Register Now" button
            WebElement registerButton = driver.findElement(By.className("button"));
            registerButton.click();

            // ✅ Wait for navigation to /login or successful message
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/login"));

            // Save the last generated username for later use
            this.lastGeneratedUsername = finalUsername;

            // Return true if redirected to login page
            return driver.getCurrentUrl().endsWith("/login");
        } catch (Exception e) {
            System.out.println("Exception during registration: " + e.getMessage());
            return false;
        }
    }
}
