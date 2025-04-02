package com.magic.finder.service;

import com.magic.finder.dto.CardListRequest;
import com.magic.finder.dto.CardListResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FinderService {
    public Mono<CardListResponse> getCardListResponse(CardListRequest cardListRequest){
        return Mono.just(new CardListResponse());
    }
}
