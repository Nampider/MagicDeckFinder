package com.magic.finder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class WebCrawlData {
    @JsonProperty("url")
    @Nullable
    private String url;

    @JsonProperty("item")
    private String name;
    public String getUrl(){
        return this.url;
    }

    public String getName() {return this.name;}
}
