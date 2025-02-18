package com.magic.finder.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ContentScrapeService {
    private WebDriver driver;
    private Set<String> visitedUrls = new HashSet<>();

    public ContentScrapeService() {
        // Setup WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configure headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Enable headless mode
        options.addArguments("--disable-gpu");  // Improves stability in some cases
        options.addArguments("--window-size=1920,1080");  // Optional for consistent rendering

        this.driver = new ChromeDriver(options);
    }

    public Mono<Set<String>> crawlWebsite(String startUrl) {
        // Ensure a new WebDriver instance is created every time
        if (driver != null) {
            driver.quit();
        }

        // Reinitialize the WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        this.driver = new ChromeDriver(options); // NEW INSTANCE

        visitedUrls.clear();
        crawl(startUrl);
        driver.quit();
        return Mono.just(visitedUrls);
    }

    public void crawl(String url) {
        // Open the page
        driver.get(url);

        // Create WebDriverWait object (you can set the time to wait)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Wait until all <a> elements (links) are present on the page
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("a")));

            // Find all links on the page after waiting for them to load
            List<WebElement> links = driver.findElements(By.tagName("a"));
            System.out.println("this is the url we are using: " + url);
            // Process the links
            for (WebElement link : links) {
                String href = link.getAttribute("href");
                if (href != null && href.contains("page=")){
                    String pageNumber = href.split("=")[1];
                }
                if (href != null && href.contains("/products")) {
                    visitedUrls.add(href);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
