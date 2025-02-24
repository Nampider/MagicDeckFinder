package com.magic.finder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigInteger;

@Data
@Table("magic")
public class MagicEntity {

    @Id
    @GeneratedValue
    private Integer id;
    @Column("itemName")
    private String itemName;
    @Column("itemPrice")
    private Double itemPrice;
    @Column("itemUrl")
    private String itemUrl;

    public MagicEntity(String itemName, String itemUrl, Double itemPrice){
        this.itemName = itemName;
        this.itemUrl = itemUrl;
        this.itemPrice = itemPrice;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }
    public String getItemName(){
        return itemName;
    }

    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    public String getItemUrl(){
        return itemUrl;
    }

    public void setItemUrl(String itemUrl){
        this.itemUrl = itemUrl;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice){
        this.itemPrice = itemPrice;
    }

}
