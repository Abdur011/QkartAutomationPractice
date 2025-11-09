package Qkart.sanity;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    // ✅ Constructor now uses WebDriver instead of RemoteWebDriver
    public Checkout(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Add new address and return true if successful
     */
    public Boolean addNewAddress(String addressString) {
        try {
            WebElement addNewAddress = driver.findElement(By.xpath("//button[text()='Add new address']"));
            addNewAddress.click();

            WebElement addressText = driver.findElement(By.xpath("//textarea[@placeholder='Enter your complete address']"));
            addressText.sendKeys(addressString);

            WebElement addButton = driver.findElement(By.xpath("//button[text()='Add']"));
            addButton.click();

            // ✅ Small wait to ensure address gets added
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'" + addressString + "')]")));

            return true; // ✅ Return true when address successfully added
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;
        }
    }

    /*
     * Select an address from the available list and return true if successful
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            List<WebElement> addresses = driver.findElements(
                    By.xpath("//div[contains(@class,'address-item')]/div[1]//p"));

            for (WebElement addr : addresses) {
                if (addr.getText().trim().equals(addressToSelect.trim())) {
                    addr.click();
                    return true;
                }
            }

            System.out.println("Unable to find the given address: " + addressToSelect);
            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred while selecting the given address: " + e.getMessage());
            return false;
        }
    }

    /*
     * Place order and return true if click successful
     */
    public Boolean placeOrder() {
        try {
            WebElement placeBtn = driver.findElement(By.xpath("//button[text()='PLACE ORDER']"));
            placeBtn.click();

            // ✅ Wait for URL or confirmation element (optional)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.urlContains("/thanks"));

            return true; // ✅ return true after clicking and navigation success
        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Verify insufficient balance message and return true if visible
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement balanceMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'enough balance')]")));

            String alertText = balanceMessage.getText();
            return alertText.contains("enough balance");
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
