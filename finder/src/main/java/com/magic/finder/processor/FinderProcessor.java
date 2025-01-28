package com.magic.finder.processor;

import com.magic.finder.service.FinderService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FinderProcessor {

    private final FinderService finderService;

    public FinderProcessor(FinderService finderService) {
        this.finderService = finderService;
    }

    public Flux<String> getSingleCard(String cardName){
        return finderService.getSingleCardService(cardName);
    }
}
