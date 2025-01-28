package com.magic.finder.service;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FinderService {
    public Flux<String> getSingleCardService(String cardName){
        // Set up ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless mode
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        try {
            // Set timeout for page load and implicit wait
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10000)); // Wait for page load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10000)); // Implicit wait for finding elements
            System.out.println(cardName);
            String encoded = URLEncoder.encode(cardName, "UTF-8");
            // Navigate to the URL
            String url = "https://www.tcgplayer.com/search/all/product?q="+encoded;
            driver.get(url);

            // Wait for the dynamic content to load (without explicitly waiting for elements)
            Thread.sleep(10000); // Just give a little buffer for dynamic content to load
            List<String> pageCount = new ArrayList<>();
            // Now get the entire page source
            String pageSource = driver.getPageSource();
            System.out.println("this is the url");
            System.out.println(url);
//            System.out.println(pageSource);
            String[] cardSplit = cardName.split(" ");
            // Find all <a> tags on the page
            List<WebElement> page = driver.findElements(By.className("tcg-button"));
            for (WebElement button : page){
                if (button.getAttribute("href")!=null && button.getAttribute("href").contains("page=")){
                    pageCount.add(button.getAttribute("href"));
                }
            }
            Set<String> pageCountSet = new HashSet<>(pageCount);
            System.out.println("This is overall list");
            System.out.println(pageCount);
            System.out.println("This is the overall Set");
            System.out.println(pageCountSet);
            System.out.println(pageCountSet.size());
            List<WebElement> anchors = driver.findElements(By.tagName("a"));
            List<String> linkList = new ArrayList<>();
// Iterate through the list and print out the href attributes
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                System.out.println("Found href: " + href);
                if (href!=null && href.contains("/product/") && (href.contains(cardSplit[0].toLowerCase()) || href.contains(cardSplit[0]) )){
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
