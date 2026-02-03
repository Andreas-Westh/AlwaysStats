package com.westh.alwaysstats.config;

public enum ScreenCorner {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CUSTOM;

    // Used for the config UI - excludes CUSTOM since that's set via reposition screen
    public static ScreenCorner[] presetValues() {
        return new ScreenCorner[] { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT };
    }
}
