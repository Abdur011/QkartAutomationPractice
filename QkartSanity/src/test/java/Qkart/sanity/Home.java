package Qkart.sanity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    // ✅ Constructor now accepts WebDriver
    public Home(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean PerformLogout() throws InterruptedException {
        try {
            WebElement logoutButton = driver.findElement(By.className("MuiButton-text"));
            logoutButton.click();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            return false;
        }
    }

    /*
     * Search for a product by name
     */
    public Boolean searchForProduct(String product) {
        try {
            WebElement searchBox = driver.findElement(By.xpath("//input[@placeholder='Search for items/categories']"));
            searchBox.clear();
            searchBox.sendKeys(product);
            searchBox.sendKeys(Keys.ENTER);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//div[contains(@class,'MuiCardContent-root')]")),
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(),'No products found')]"))));

            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return the list of search result elements
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<>();
        try {
            searchResults = driver.findElements(By.xpath("//div[contains(@class,'MuiCardContent-root')]"));
        } catch (Exception e) {
            System.out.println("Error fetching search results: " + e.getMessage());
        }
        return searchResults;
    }

    /*
     * Check if "No products found" message is displayed
     */
    public Boolean isNoResultFound() {
        try {
            WebElement noResults = driver.findElement(By.xpath("//*[contains(text(),'No products found')]"));
            return noResults.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Add a product to the cart by name
     */
    public Boolean addProductToCart(String productName) {
        try {
            List<WebElement> titleElements = driver.findElements(
                    By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-yg30e6']"));
            List<WebElement> addToCartButtons = driver.findElements(By.xpath("//button[text()='Add to cart']"));

            for (int i = 0; i < titleElements.size(); i++) {
                String title = titleElements.get(i).getText();
                if (title.equals(productName)) {
                    addToCartButtons.get(i).click();
                    Thread.sleep(1500);
                    return true;
                }
            }

            System.out.println("Unable to find the product: " + productName);
            return false;
        } catch (Exception e) {
            System.out.println("Exception while adding product to cart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Click on the Checkout button
     */
    public Boolean clickCheckout() {
        try {
            WebElement checkoutButton = driver.findElement(By.xpath("//button[text()='Checkout']"));
            checkoutButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.urlContains("/checkout"));

            return true;
        } catch (Exception e) {
            System.out.println("Exception while clicking Checkout: " + e.getMessage());
            return false;
        }
    }

    /*
     * Change quantity of a product in cart
     */
    public Boolean changeProductQuantityinCart(String productName, int quantity) {
        try {
            List<WebElement> parentElements = driver.findElements(By.xpath("//div[@class='MuiBox-root css-1gjj37g']"));

            for (WebElement parentElement : parentElements) {
                String title = parentElement.findElement(By.xpath("./div[1]")).getText();

                if (title.equals(productName)) {
                    while (true) {
                        WebElement qtyElement = parentElement.findElement(By.xpath(".//div[@data-testid='item-qty']"));
                        int actualQty = Integer.parseInt(qtyElement.getText());

                        if (quantity > actualQty) {
                            parentElement.findElement(By.xpath(".//*[@data-testid='AddOutlinedIcon']")).click();
                            Thread.sleep(500);
                        } else if (quantity < actualQty) {
                            parentElement.findElement(By.xpath(".//*[@data-testid='RemoveOutlinedIcon']")).click();
                            Thread.sleep(500);
                        } else {
                            break;
                        }
                    }
                    return true;
                }
            }

            System.out.println("Product not found in cart: " + productName);
            return false;
        } catch (Exception e) {
            System.out.println("Exception when updating cart quantity: " + e.getMessage());
            return quantity == 0; // consider success if item removed completely
        }
    }

    /*
     * Verify expected items exist in the cart
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartItems = cartParent.findElements(By.className("css-zgtx0t"));

            List<String> actualCartContents = new ArrayList<>();
            for (WebElement item : cartItems) {
                String itemName = item.findElement(By.className("css-1gjj37g")).getText().split("\n")[0];
                actualCartContents.add(itemName);
            }

            for (String expected : expectedCartContents) {
                if (!actualCartContents.contains(expected)) {
                    System.out.println("Missing product in cart: " + expected);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Exception verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
