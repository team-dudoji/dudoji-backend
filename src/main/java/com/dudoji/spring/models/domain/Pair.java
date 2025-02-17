package com.dudoji.spring.models.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@EqualsAndHashCode
public class Pair<A, B> {
    @Getter
    private A X;
    @Getter
    private B Y;

    public Pair(A X, B Y) {
        this.X = X;
        this.Y = Y;
    }

    public void setX(A X) {
        this.X = X;
    }

    public void setY(B Y) {
        this.Y = Y;
    }
}