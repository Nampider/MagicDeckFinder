package com.magic.finder.processor;

import com.magic.finder.service.ContentScrapeService;
import com.magic.finder.service.PageFinderService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
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
        return pageFinderService.crawlWebsite(itemName)
                .flatMapMany(Flux::fromIterable) // Convert Set<String> to Flux<String>
                .flatMap(contentScrapeService::crawlWebsite) // Now correctly returns Flux<Set<String>>
                .flatMapIterable(set -> set) // Flatten Set<String> to Flux<String>
                .collect(Collectors.toSet()); // Collect into a single Mono<Set<String>>
    }

}
