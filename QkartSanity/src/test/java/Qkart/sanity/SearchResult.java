package Qkart.sanity;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;

    public SearchResult(WebElement searchResultElement) {
        this.parentElement = searchResultElement;
    }

    /*
     * Return title of the search result card
     */
    public String getTitleofResult() {
        try {
            WebElement productName = parentElement.findElement(By.xpath(".//p[contains(@class,'MuiTypography-body1')]"));
            return productName.getText().trim();
        } catch (Exception e) {
            System.out.println("Error getting product title: " + e.getMessage());
            return "";
        }
    }

    /*
     * Open the size chart modal
     */
    public Boolean openSizechart() {
        try {
            WebElement sizeChartBtn = parentElement.findElement(By.xpath(".//button[text()='Size chart']"));
            sizeChartBtn.click();

            // Wait for modal to appear
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Close the size chart modal using ESC key
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ESCAPE).perform();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//table")));
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Verify if the size chart button exists for this product
     */
    public Boolean verifySizeChartExists() {
        try {
            WebElement sizeChart = parentElement.findElement(By.xpath(".//button[text()='Size chart']"));
            return sizeChart.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Validate that the table headers and body of the size chart match expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
                                             WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

            // ✅ Validate table headers
            for (int i = 0; i < expectedTableHeaders.size(); i++) {
                int col = i + 1;
                String expectedHeader = expectedTableHeaders.get(i).trim();
                WebElement actualHeaderElement = driver.findElement(By.xpath("//table/thead/tr/th[" + col + "]"));
                String actualHeader = actualHeaderElement.getText().trim();

                if (!actualHeader.equalsIgnoreCase(expectedHeader)) {
                    System.out.println("Header mismatch at column " + col + ": expected " + expectedHeader + " but got " + actualHeader);
                    return false;
                }
            }

            // ✅ Validate table body
            for (int i = 0; i < expectedTableBody.size(); i++) {
                List<String> expectedRow = expectedTableBody.get(i);
                for (int j = 0; j < expectedRow.size(); j++) {
                    int row = i + 1;
                    int col = j + 1;
                    String expectedCell = expectedRow.get(j).trim();

                    WebElement actualCellElement = driver.findElement(By.xpath("//table/tbody/tr[" + row + "]/td[" + col + "]"));
                    String actualCell = actualCellElement.getText().trim();

                    if (!actualCell.equalsIgnoreCase(expectedCell)) {
                        System.out.println("Table mismatch at row " + row + ", col " + col +
                                ": expected " + expectedCell + " but got " + actualCell);
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error while validating size chart contents: " + e.getMessage());
            return false;
        }
    }

    /*
     * Check if the size dropdown exists on the product card
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        try {
            WebElement sizeDropdown = driver.findElement(By.xpath("//select[@name='age']"));
            return sizeDropdown.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
