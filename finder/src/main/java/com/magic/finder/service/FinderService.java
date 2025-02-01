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
import java.util.stream.Collectors;

@Service
public class FinderService {
    public List<String> getPageWebScrapeService(String url, String cardName){
        // Set up ChromeOptions for headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless mode
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        try {
            // Set timeout for page load and implicit wait
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10000)); // Wait for page load
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10000)); // Implicit wait for finding elements
            driver.get(url);
            Thread.sleep(10000);

            List<WebElement> anchors = driver.findElements(By.tagName("a"));
            List<String> linkList = new ArrayList<>();
// Iterate through the list and print out the href attributes
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                if (href!=null && href.contains("/product/") && (href.contains(cardName.toLowerCase()) || href.contains(cardName) )){
                    linkList.add("\n"+href);
                    System.out.println("Found href: " + href);
                }
            }
            return linkList;

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

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
            String encoded = URLEncoder.encode(cardName, "UTF-8");
            // Navigate to the URL
            String url = "https://www.natural20gaming.com/products/search?q=Sol+ring&c=1"+encoded;
            driver.get(url);

            // Wait for the dynamic content to load (without explicitly waiting for elements)
            Thread.sleep(10000); // Just give a little buffer for dynamic content to load
            List<String> pageCount = new ArrayList<>();
            // Now get the entire page source
            String pageSource = driver.getPageSource();
            String[] cardSplit = cardName.split(" ");
            // Find all <a> tags on the page
            List<WebElement> page = driver.findElements(By.className("image"));

            for (WebElement image : page){
                System.out.println("GOT HERE");
                System.out.println(image.findElement(By.xpath("/html/body/div[1]/div/section/div/div[3]/section/div/div[4]/ul/li[1]/div/div[1]/div[1]/a")).getAttribute("href"));
                if (image.getAttribute("href")!=null && image.getAttribute("href").contains("magic_singles")){
                    System.out.println(image.getAttribute("href"));
                }
            }

            List<WebElement> pagenum = driver.findElements(By.xpath("//div[@class='pagination']//a"));

            for (WebElement pagination : pagenum){
                System.out.println(pagination.getAttribute("href"));
            }
            Set<String> pageCountSet = new HashSet<>(pageCount);
            List<List<String>> allLinksList = new ArrayList<>();
            for (String i : pageCountSet){
                allLinksList.add(getPageWebScrapeService(i, cardSplit[0]));
            }
            List<String> outputList = new ArrayList<>();
            outputList = allLinksList.stream().flatMap(List::stream).toList();
//            List<WebElement> anchors = driver.findElements(By.tagName("a"));
//            List<String> linkList = new ArrayList<>();
//            for (WebElement anchor : anchors) {
//                String href = anchor.getAttribute("href");
//                if (href!=null && href.contains("/product/") && (href.contains(cardSplit[0].toLowerCase()) || href.contains(cardSplit[0]) )){
//                    linkList.add("\n"+href);
//                }
//            }
            return Flux.fromIterable(outputList);
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
