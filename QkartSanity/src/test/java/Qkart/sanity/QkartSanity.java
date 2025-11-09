package Qkart.sanity;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class QkartSanity {

    public static String lastGeneratedUserName;

    //  Local Chrome setup
    public static WebDriver createDriver() {
        WebDriverManager.chromedriver().setup(); // Auto-setup correct ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start Chrome maximized
        return new ChromeDriver(options);
    }

    public static void logStatus(String type, String message, String status) {
        System.out.println(String.format("%s | %s | %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    /*
     * Testcase01: Verify the functionality of Login button on the Home page
     */
    public static Boolean TestCase01(WebDriver driver) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);

        if (!status) {
            logStatus("TestCase 1", "User Registration Failed", "FAIL");
            return false;
        } else {
            logStatus("TestCase 1", "User Registration Passed", "PASS");
        }

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");

        if (!status) return false;

        Home home = new Home(driver);
        status = home.PerformLogout();
        logStatus("End TestCase", "Test Case 1: Verify user Registration", status ? "PASS" : "FAIL");

        return status;
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    public static Boolean TestCase02(WebDriver driver) throws InterruptedException {
        Boolean status;
        logStatus("Start Testcase", "Test Case 2: Verify duplicate registration", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) return false;

        lastGeneratedUserName = registration.lastGeneratedUsername;

        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        logStatus("End TestCase", "Duplicate registration validation", status ? "FAIL" : "PASS");
        return !status;
    }

    /*
     * Verify the functionality of the search box
     */
    public static Boolean TestCase03(WebDriver driver) throws InterruptedException {
        logStatus("Start TestCase", "Test Case 3: Verify search box", "DONE");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        Thread.sleep(3000);

        boolean status = homePage.searchForProduct("yonex");
        if (!status) return false;

        List<WebElement> searchResults = homePage.getSearchResults();
        if (searchResults.isEmpty()) return false;

        for (WebElement webElement : searchResults) {
            SearchResult resultElement = new SearchResult(webElement);
            if (!resultElement.getTitleofResult().toUpperCase().contains("YONEX")) return false;
        }

        logStatus("Step Success", "Validated search results", "PASS");
        return true;
    }

    /*
     * Verify the presence of size chart
     */
    public static Boolean TestCase04(WebDriver driver) throws InterruptedException {
        logStatus("Start TestCase", "Test Case 4: Verify size chart", "DONE");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        Thread.sleep(3000);

        boolean status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        List<String> expectedHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");

        for (WebElement element : searchResults) {
            SearchResult result = new SearchResult(element);
            if (result.verifySizeChartExists()) {
                result.openSizechart();
                result.validateSizeChartContents(expectedHeaders, null, driver);
                result.closeSizeChart(driver);
            } else return false;
        }
        return true;
    }

    /*
     * Verify checkout flow
     */
    public static Boolean TestCase05(WebDriver driver) throws InterruptedException {
        logStatus("Start TestCase", "Test Case 5: Verify checkout flow", "DONE");
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        Boolean status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        login.PerformLogin(lastGeneratedUserName, "abc@123");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        homePage.searchForProduct("Yonex");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(2000);

        return driver.getCurrentUrl().endsWith("/thanks");
    }

    /*
     * Verify quantity updates in cart
     */
    public static Boolean TestCase06(WebDriver driver) throws InterruptedException {
        logStatus("Start TestCase", "Test Case 6: Verify cart updates", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        Boolean status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        login.PerformLogin(lastGeneratedUserName, "abc@123");

        Home homePage = new Home(driver);
        homePage.searchForProduct("Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);
        homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);
        return true;
    }

    /*
     * Verify insufficient balance error
     */
    public static Boolean TestCase07(WebDriver driver) throws InterruptedException {
        logStatus("Start TestCase", "Test Case 7: Verify insufficient balance", "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        Boolean status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        login.PerformLogin(lastGeneratedUserName, "abc@123");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        homePage.searchForProduct("Stylecon");
        homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set");
        homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set", 10);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(2000);

        return checkoutPage.verifyInsufficientBalanceMessage();
    }

    //  Main method
    public static void main(String[] args) throws InterruptedException {
        int totalTests = 0;
        int passedTests = 0;
        Boolean status;

        WebDriver driver = createDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        try {
            totalTests++;
            if (TestCase01(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase02(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase03(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase04(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase05(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase06(driver)) 
            	passedTests++;

            totalTests++;
            if (TestCase07(driver)) 
            	passedTests++;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println(String.format("%d out of %d test cases Passed", passedTests, totalTests));
        }
    }
}
