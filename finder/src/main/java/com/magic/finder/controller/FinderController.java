package com.magic.finder.controller;

import com.magic.finder.dto.CardListRequest;
import com.magic.finder.dto.CardListResponse;
import com.magic.finder.processor.FinderProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FinderController {

    private final FinderProcessor finderProcessor;

    public FinderController(FinderProcessor finderProcessor) {
        this.finderProcessor = finderProcessor;
    }

    @PostMapping("/search/clean")
    public Mono<CardListResponse> getCardInformation(@RequestBody CardListRequest cardListRequest){
        return finderProcessor.getCardListResponse(cardListRequest);
    }
}
