package com.mstest;

/**
 * Created by ricardo on 12/11/2015.
 */

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BagContentsSteps {
    String itemId;
    Actions actions;

    protected WebDriver driver;

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "resources/driver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().setPosition(new Point(0, 0));

        int width;
        int height;
        try {
            width = Integer.parseInt(System.getProperty("width"));
            height = Integer.parseInt(System.getProperty("height"));
        } catch (Exception e) {
            // Default browser windows size.
            width = 1080;
            height = 720;
        }
        driver.manage().window().setSize(new Dimension(width, height));

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        actions = new Actions(driver);
        driver.get("http://www.marksandspencer.com/");
    }

    @Given("^I have added a shirt to my bag$")
    public void i_have_added_a_shirt_to_my_bag() throws Throwable {
        // Search for "shirt".
        WebElement searchField = driver.findElement(By.cssSelector("input#global-search"));
        Assert.assertNotNull(searchField);
        scrollToElement(searchField);
        searchField.sendKeys("shirt");
        WebElement searchButton = driver.findElement(By.cssSelector("input#goButton.submit.enabled"));
        scrollToElement(searchButton);
        searchButton.click();

        // Click first result.
        List<WebElement> searchResultsView = driver.findElements(By.cssSelector("div.product-listing-container li"));
        Assert.assertNotNull(searchResultsView);
        Assert.assertTrue(searchResultsView.size() > 0);
        scrollToElement(searchResultsView.get(0));
        searchResultsView.get(0).click();

        // Select first available colour and size.
        List<WebElement> coloursList = driver.findElements(By.cssSelector("div.caro.typeB.swatchContainerHeight ul li"));
        Assert.assertNotNull(coloursList);
        Assert.assertTrue(coloursList.size() > 0);

        boolean colourIsSelected = false;
        boolean sizeIsSelected = false;

        for (WebElement colour : coloursList) {
            scrollToElement(colour);
            colour.click();
            colourIsSelected = true;

            List<WebElement> sizesList = driver.findElements(By.cssSelector("table.sizes.displayCell.grid-1d td"));

            if(sizesList != null) {
                if (sizesList.size() > 0) {
                    for (WebElement size : sizesList) {
                        if (!size.getAttribute("class").equals("out-of-stock")) {
                            scrollToElement(size);
                            size.click();
                            sizeIsSelected = true;
                            break;
                        }
                    }
                }
            }

            if(colourIsSelected && sizeIsSelected) {
                break;
            } else {
                colourIsSelected = false;
            }
        }

        Assert.assertTrue(colourIsSelected);
        Assert.assertTrue(sizeIsSelected);

        // Click add to bag.
        WebElement addToBag = driver.findElement(By.cssSelector("li.linear-journey.req.addbag input[type=submit][value=\"add to bag\"]"));
        Assert.assertNotNull(addToBag);
        scrollToElement(addToBag);
        addToBag.click();
        itemId = addToBag.getAttribute("id").replace("basket_","").toLowerCase();

    }

    @When("^I view the contents of my bag$")
    public void i_view_the_contents_of_my_bagy() throws Throwable {
        // Click your bag.
        WebElement topBagButton = driver.findElement(By.cssSelector("ul.site-tools a.header-link"));
        Assert.assertNotNull(topBagButton);
        scrollToElement(topBagButton);
        topBagButton.click();
    }

    @Then("^I can see the contents of the bag include a shirt$")
    public void i_can_see_the_contents_of_the_bag_include_a_shirt() throws Throwable {
        //bag contains shirt
        List<WebElement> bagItems = driver.findElements(By.cssSelector("section.product-item div.product-info-wrap h3 a"));
        Assert.assertNotNull(bagItems);
        Assert.assertTrue(bagItems.size() > 0);

        if(itemId != null) {
            boolean itemIsPresent = false;
            for (WebElement item : bagItems) {
                if (item.getAttribute("href").contains(itemId)) {
                    itemIsPresent = true;
                    break;
                }
            }

            Assert.assertTrue("Item is not present in the Bag.", itemIsPresent);
        } else {
            Assert.fail("Item id is not valid.");
        }
    }

    @After
    public void closeBrowser() {
        driver.quit();
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
