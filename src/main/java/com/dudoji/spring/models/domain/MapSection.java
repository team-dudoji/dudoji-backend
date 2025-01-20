package com.dudoji.spring.models.domain;

public class MapSection {
    MapSection(Builder builder){
        uid = builder.uid;
        x = builder.x;
        y = builder.y;
        explored = true;
    }

    protected long uid;
    protected int x;
    protected int y;
    protected boolean explored;

    public static class Builder {
        protected long uid;
        protected int x;
        protected int y;
        protected byte[] bitmap = null;

        public Builder(){ }

        public MapSection build(){
            if (bitmap == null){
                return new MapSection(this);
            } else {
                return new DetailedMapSection(this);
            }
        }

        public MapSection.Builder setUid(long uid){
            this.uid = uid;
            return this;
        }
        public MapSection.Builder setX(int x){
            this.x = x;
            return this;
        }
        public MapSection.Builder setY(int y){
            this.y = y;
            return this;
        }
        public MapSection.Builder setBitmap(byte[] bitmap){
            this.bitmap = bitmap;
            return this;
        }
    }
}
