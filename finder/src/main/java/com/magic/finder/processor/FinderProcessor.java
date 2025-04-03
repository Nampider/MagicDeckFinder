package com.magic.finder.processor;

import com.magic.finder.dto.CardListRequest;
import com.magic.finder.dto.CardListResponse;
import com.magic.finder.service.FinderService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FinderProcessor {

    private final FinderService finderService;

    public FinderProcessor(FinderService finderService) {
        this.finderService = finderService;
    }

    public Mono<CardListResponse> getCardListResponse(CardListRequest cardListRequest){
        return finderService.getCardListResponse(cardListRequest);
    }
}
