package com.magic.finder.processor;

import com.magic.finder.service.ContentScrapeService;
import com.magic.finder.service.PageFinderService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FinderProcessor {

    private final PageFinderService pageFinderService;
    private final ContentScrapeService contentScrapeService;

    public FinderProcessor(PageFinderService pageFinderService, ContentScrapeService contentScrapeService) {
        this.pageFinderService = pageFinderService;
        this.contentScrapeService = contentScrapeService;
    }

    //We collect all of the collected webscraped data in here.
    public Mono<Set<String>> getSingleCard(String itemName) {
        //Url list:
        String url1 = "https://darksidegames.com/search?page=5&q=%2ASauron%2A";
        //https://www.power9games.com/products/search?c=1&page=4&q=Sol+Ring
//        String url2 = "https://darksidegames.com/search?page=";
        //https://darksidegames.com/search?page=5&q=%2ASauron%2A
        List<String> listOfWebsites = new ArrayList<>();
        listOfWebsites.add(url1);
//        listOfWebsites.add(url2);

        Mono<Set<String>> test = Mono.just(listOfWebsites.stream().map(
                val -> {
                    return pageFinderService.crawlWebsite(val)
                            .flatMapMany(Flux::fromIterable) // Convert Set<String> to Flux<String>
                            .flatMap(contentScrapeService::crawlWebsite) // Now correctly returns Flux<Set<String>>
                            .flatMapIterable(set -> set) // Flatten Set<String> to Flux<String>
                            .collect(Collectors.toSet()); // Collect into a single Mono<Set<String>>
                }
        ).collect(Collectors.toSet())).flatMapMany(Flux::fromIterable).flatMap(monoSet->monoSet).collect(Collectors.toSet()).map(
                val -> {
                    return val.stream().flatMap(Set::stream).collect(Collectors.toSet());
                }
        );

        test.subscribe(
                val -> {
                    System.out.println(val);
                }
        );

        return test;

//        return pageFinderService.crawlWebsite(itemName)
//                .flatMapMany(Flux::fromIterable) // Convert Set<String> to Flux<String>
//                .flatMap(contentScrapeService::crawlWebsite) // Now correctly returns Flux<Set<String>>
//                .flatMapIterable(set -> set) // Flatten Set<String> to Flux<String>
//                .collect(Collectors.toSet()); // Collect into a single Mono<Set<String>>
    }

}
