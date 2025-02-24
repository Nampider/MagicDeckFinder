package com.magic.finder.repository;

import com.magic.finder.model.MagicEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MagicRepository extends R2dbcRepository<MagicEntity, Integer> {

    @Query("SELECT * FROM public.magic WHERE id = :id")
    Mono<MagicEntity> findByIdCustom(@Param("id") Integer id);

    @Query("INSERT INTO magic (\"itemName\", \"itemPrice\", \"itemUrl\") VALUES (:itemName, :itemPrice, :itemUrl) RETURNING *")
    Mono<MagicEntity> customSave(String itemName, Double itemPrice, String itemUrl);
//    //find the itemName
//    @Query("SELECT * FROM public.magic WHERE itemName = :itemName")
//    Mono<MagicEntity> findByItemName(@Param("itemName") String itemName);
}
