package com.dudoji.spring.models.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetailedMapSection extends MapSection {
    private byte[] bitmap;
    public DetailedMapSection(Builder builder){
        super(builder);
        explored = false;
        bitmap = builder.bitmap;
    }
}
