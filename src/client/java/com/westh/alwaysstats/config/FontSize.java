package com.westh.alwaysstats.config;

public enum FontSize {
    SMALL(0.5f),
    MEDIUM(0.75f),
    LARGE(1.0f);

    private final float scale;

    FontSize(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }
}
