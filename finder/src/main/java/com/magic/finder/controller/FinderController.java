package com.magic.finder.controller;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import reactor.core.publisher.Mono;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.io.IOUtils.length;

@RestController
public class FinderController {

    @GetMapping("/test/{id}")
    public Mono<String> sayHello(@PathVariable Integer id) {
        // Set up ChromeOptions for headless mode (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Headless mode
        options.addArguments("--disable-gpu"); // Disable GPU (optional)

        WebDriver driver = new ChromeDriver(options);

        try {
            // Navigate to the website
            driver.get("https://www.tcgplayer.com/");

            // Wait for the #app div to be visible and populated with content
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("app")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("horizontal-layout")));

            // Find the #app div (or any other parent div you're interested in)
            WebElement appDiv = driver.findElement(By.id("app"));

            // Get the outer HTML of the #app div (including all nested elements)
            String appContent = appDiv.getAttribute("outerHTML");
            System.out.println("Full HTML inside #app:\n" + appContent);

            // Find all <a> tags on the page
            List<WebElement> anchors = driver.findElements(By.tagName("a"));

// Iterate through the list and print out the href attributes
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                System.out.println("Found href: " + href);
            }

            // If you only need the inner HTML (without the parent <div> tag itself), use:
            // String innerHtml = appDiv.getAttribute("innerHTML");
            // System.out.println("Inner HTML inside #app:\n" + innerHtml);

        } finally {
            // Close the browser
            driver.quit();
        }
        return Mono.just("Hello");
    }

    @GetMapping("/search/{cardName}")
    public Mono<String> getCardLink(@PathVariable String cardName){
        //https://www.tcgplayer.com/search/all/product?q=urza%27s+mine&view=grid
        // Set up ChromeOptions for headless mode (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Headless mode
        options.addArguments("--disable-gpu"); // Disable GPU (optional)

        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
//            String regex = "[\\\\'\\\\+]";
//            String regex = "[\\\\!\\\\@]";
//            System.out.println("This is the cardName");
//            System.out.println(cardName);
//            String[] arr = cardName.split(regex);
//            System.out.println(Arrays.toString(arr));
//            System.out.println(length(arr));
            //make sure to convert to UTF-8
            System.out.println(cardName);
            String encoded = URLEncoder.encode(cardName, "UTF-8");
            System.out.println("https://www.tcgplayer.com/search/all/product?q="+encoded);
            // Navigate to the website
            driver.get("https://www.tcgplayer.com/search/all/product?q=\"+encoded");

            // Wait for the #app div to be visible and populated with content
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("app")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("horizontal-filters-bar")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-layout-hfb")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-results")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-result")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-result__content")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-result__product")));
//href="/product/11288/magic-8th-edition-urzas-mine?page=1"
            // Find the #app div (or any other parent div you're interested in)
            WebElement appDiv = driver.findElement(By.id("app"));

            // Get the outer HTML of the #app div (including all nested elements)
            String appContent = appDiv.getAttribute("outerHTML");
            System.out.println("Full HTML inside #app:\n" + appContent);

            // Find all <a> tags on the page
            List<WebElement> anchors = driver.findElements(By.tagName("a"));

// Iterate through the list and print out the href attributes
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                System.out.println("Found href: " + href);
            }
            return Mono.just(appContent);
            // If you only need the inner HTML (without the parent <div> tag itself), use:
            // String innerHtml = appDiv.getAttribute("innerHTML");
            // System.out.println("Inner HTML inside #app:\n" + innerHtml);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
