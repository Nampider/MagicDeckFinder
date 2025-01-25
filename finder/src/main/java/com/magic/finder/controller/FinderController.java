package com.magic.finder.controller;

import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
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
    public Flux<String> getCardLink(@PathVariable String cardName){
        // Set up ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless mode
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        try {
            // Set timeout for page load and implicit wait
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30)); // Wait for page load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Implicit wait for finding elements
            System.out.println(cardName);
            String encoded = URLEncoder.encode(cardName, "UTF-8");
            // Navigate to the URL
            String url = "https://www.tcgplayer.com/search/all/product?q="+encoded;
            driver.get(url);

            // Wait for the dynamic content to load (without explicitly waiting for elements)
            Thread.sleep(5000); // Just give a little buffer for dynamic content to load

            // Now get the entire page source
            String pageSource = driver.getPageSource();
            System.out.println(pageSource);
            // Find all <a> tags on the page
            List<WebElement> anchors = driver.findElements(By.tagName("a"));
            List<String> linkList = new ArrayList<>();
// Iterate through the list and print out the href attributes
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                System.out.println("Found href: " + href);
                if (href!=null && href.contains("/product/")){
                    linkList.add("\n"+href);
                }
            }
            return Flux.fromIterable(linkList);
        } catch (TimeoutException e) {
            System.out.println("Page took too long to load.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the browser
            driver.quit();
        }
        return Flux.fromIterable(new ArrayList<>());
    }
}
