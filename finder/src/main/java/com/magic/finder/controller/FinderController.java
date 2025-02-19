package com.magic.finder.controller;

import com.magic.finder.dto.WebCrawlData;
import com.magic.finder.processor.FinderProcessor;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.web.bind.annotation.*;
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
import java.util.*;

import static org.apache.commons.io.IOUtils.length;
import static org.apache.commons.io.IOUtils.resourceToByteArray;

@RestController
public class FinderController {

    private final FinderProcessor finderProcessor;

    public FinderController(FinderProcessor finderProcessor) {
        this.finderProcessor = finderProcessor;
    }
    
    @PostMapping("/search/clean")
    public Mono<Set<String>> getCardInformation(@RequestBody WebCrawlData webCrawlData){
        return finderProcessor.getSingleCard(webCrawlData.getName());
    }
}
