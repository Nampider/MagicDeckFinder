package com.magic.finder.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.bonigarcia.wdm.WebDriverManager;
import reactor.core.publisher.Mono;

@Service
public class PageFinderService {
    private WebDriver driver;
    private Set<String> visitedUrls = new HashSet<>();
    private Set<Integer> pageCounts = new HashSet<>();

    public PageFinderService() {
        // Setup WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configure headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Enable headless mode
        options.addArguments("--disable-gpu");  // Improves stability in some cases
        options.addArguments("--window-size=1920,1080");  // Optional for consistent rendering

        this.driver = new ChromeDriver(options);
    }

    public Mono<Set<String>> crawlWebsite(String itemName) {
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
        crawl(itemName);
        driver.quit();
        return Mono.just(visitedUrls);
    }


    public void crawl(String item) {
        String url = "https://darksidegames.com/search?page=1&q=%2A" + urlBuilder(item) + "%2A";
        // Open the page
        driver.get(url);

        // Create WebDriverWait object (you can set the time to wait)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        System.out.println("This is the URL: " + url);
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
                    pageCounts.add(Integer.parseInt(pageNumber.substring(0, (pageNumber.length()-2))));
                }
//                if (href != null) {
//                    visitedUrls.add(href);
//                }
            }
            int max = 0;
            for (Integer s : pageCounts){
                max = Math.max(s, max);
            }
            getActualItemData(max, item);
            //Now create a new function that serves to actually find all the links.
            System.out.println("This is the max: " + max);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getActualItemData(int count, String item){
        String urlPArt = urlBuilder(item);
        while(count > 0) {
            String url = "https://darksidegames.com/search?page=" + count + "&q=%2A" + urlPArt + "%2A";
            count--;
            System.out.println(url);
            visitedUrls.add(url);
        }
    }

    public static String urlBuilder(String item){
        String builtUrl = "";
        builtUrl = item.replace("'", "%27").replace(" ", "%20");
        return builtUrl;
    }
}
