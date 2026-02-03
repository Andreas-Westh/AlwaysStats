package com.westh.alwaysstats.screen;

import com.westh.alwaysstats.config.ScreenCorner;
import com.westh.alwaysstats.config.StatsConfig;
import com.westh.alwaysstats.render.StatsRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RepositionScreen extends Screen {
    private static final float MIN_SCALE = 0.5f;  // Same as SMALL
    private static final float MAX_SCALE = 1.5f;  // 1.5x LARGE
    private static final int HANDLE_SIZE = 8;

    private final Screen parent;
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean isDragging = false;
    private boolean isResizing = false;
    private int previewX;
    private int previewY;
    private int previewWidth;
    private int previewHeight;
    private float previewScale;

    // Stat reordering state
    private int draggingStatIndex = -1;
    private String draggingStatKey = null;  // Track by key for reliable highlighting
    private List<StatsRenderer.StatBounds> lastStatBounds = List.of();
    private List<String> originalStatOrder;  // To restore on cancel

    public RepositionScreen(Screen parent) {
        super(Component.literal("Reposition HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        StatsConfig config = StatsConfig.get();

        // Store original stat order to restore on cancel
        originalStatOrder = new ArrayList<>(config.statOrder);

        // Initialize preview scale
        if (config.corner == ScreenCorner.CUSTOM) {
            previewScale = config.customScale;
        } else {
            previewScale = config.fontSize.getScale();
        }

        // Initialize preview position from current config
        if (config.corner == ScreenCorner.CUSTOM) {
            previewX = config.customX;
            previewY = config.customY;
        } else {
            // Calculate initial position based on current corner setting
            int[] pos = StatsRenderer.calculatePosition(minecraft, config.corner);
            previewX = pos[0];
            previewY = pos[1];
        }

        // Add Done button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
            // Save custom position, scale, and stat order
            config.corner = ScreenCorner.CUSTOM;
            config.customX = previewX;
            config.customY = previewY;
            config.customScale = previewScale;
            StatsConfig.save();
            minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 100, this.height - 40, 95, 20).build());

        // Add Cancel button
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> {
            // Restore original stat order
            config.statOrder = new ArrayList<>(originalStatOrder);
            minecraft.setScreen(parent);
        }).bounds(this.width / 2 + 5, this.height - 40, 95, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render game world in background
        if (this.minecraft.level != null) {
            this.renderTransparentBackground(guiGraphics);
        } else {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }

        // Render the stats preview at the current position with current scale
        // Pass the dragged stat key to highlight it
        StatsRenderer.PreviewResult result = StatsRenderer.renderPreview(guiGraphics, minecraft, previewX, previewY, previewScale, draggingStatKey);
        previewWidth = result.width();
        previewHeight = result.height();
        lastStatBounds = result.statBounds();

        // Draw instruction text
        guiGraphics.drawCenteredString(this.font, "Drag box to move, corner to resize, stats to reorder", this.width / 2, 20, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("Scale: %.0f%%", previewScale * 100), this.width / 2, 35, 0xAAAAAA);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private int calculateInsertionIndex(double mouseY) {
        if (lastStatBounds.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < lastStatBounds.size(); i++) {
            StatsRenderer.StatBounds bounds = lastStatBounds.get(i);
            int midY = bounds.y() + bounds.height() / 2;
            if (mouseY < midY) {
                return i;
            }
        }
        return lastStatBounds.size();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        if (super.mouseClicked(event, handled)) {
            return true;
        }

        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == 0) {
            // Check if click is on the resize handle (bottom-right corner)
            if (isMouseOverResizeHandle(mouseX, mouseY)) {
                isResizing = true;
                return true;
            }

            // Check if click is on an individual stat (for reordering)
            int statIndex = getStatIndexAt(mouseX, mouseY);
            if (statIndex >= 0) {
                draggingStatIndex = statIndex;
                draggingStatKey = lastStatBounds.get(statIndex).key();
                return true;
            }

            // Check if click is within the stats preview box (for dragging whole box)
            if (isMouseOverPreview(mouseX, mouseY)) {
                isDragging = true;
                dragOffsetX = (int) mouseX - previewX;
                dragOffsetY = (int) mouseY - previewY;
                return true;
            }
        }

        return false;
    }

    private int getStatIndexAt(double mouseX, double mouseY) {
        if (lastStatBounds.isEmpty()) {
            return -1;
        }

        int boxX = previewX - StatsRenderer.PADDING;
        // Check if within horizontal bounds of the box
        if (mouseX < boxX || mouseX > boxX + previewWidth) {
            return -1;
        }

        for (int i = 0; i < lastStatBounds.size(); i++) {
            StatsRenderer.StatBounds bounds = lastStatBounds.get(i);
            if (mouseY >= bounds.y() && mouseY < bounds.y() + bounds.height()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            isDragging = false;
            isResizing = false;
            draggingStatIndex = -1;
            draggingStatKey = null;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();

            if (isResizing) {
                // Calculate new scale based on mouse distance from top-left corner
                int boxX = previewX - StatsRenderer.PADDING;

                // Use the diagonal distance to determine scale
                double currentWidth = mouseX - boxX;

                // Calculate scale based on width (more intuitive)
                if (previewWidth > 0) {
                    float baseWidth = previewWidth / previewScale;
                    float newScale = (float) (currentWidth / baseWidth);

                    // Clamp scale to valid range
                    previewScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScale));
                }

                return true;
            }

            if (draggingStatIndex >= 0) {
                // Calculate where the stat should move to
                int targetIndex = calculateInsertionIndex(mouseY);
                if (targetIndex >= 0 && targetIndex != draggingStatIndex && targetIndex != draggingStatIndex + 1) {
                    // Reorder the stats live
                    String draggedKey = lastStatBounds.get(draggingStatIndex).key();
                    StatsConfig config = StatsConfig.get();
                    List<String> order = new ArrayList<>(config.statOrder);

                    int currentPos = order.indexOf(draggedKey);
                    if (currentPos >= 0) {
                        order.remove(currentPos);

                        // Map visual index to full order position
                        int targetPos;
                        if (targetIndex >= lastStatBounds.size()) {
                            String lastKey = lastStatBounds.get(lastStatBounds.size() - 1).key();
                            targetPos = order.indexOf(lastKey) + 1;
                        } else if (targetIndex == 0) {
                            String firstKey = lastStatBounds.get(0).key();
                            targetPos = order.indexOf(firstKey);
                        } else {
                            // Account for the removed item shifting indices
                            int visualIdx = targetIndex > draggingStatIndex ? targetIndex - 1 : targetIndex;
                            if (visualIdx < lastStatBounds.size() && visualIdx != draggingStatIndex) {
                                String targetKey = lastStatBounds.get(visualIdx).key();
                                targetPos = order.indexOf(targetKey);
                                if (targetIndex > draggingStatIndex) {
                                    targetPos++;
                                }
                            } else {
                                targetPos = order.size();
                            }
                        }

                        if (targetPos < 0) targetPos = order.size();
                        order.add(targetPos, draggedKey);
                        config.statOrder = order;

                        // Update dragging index to follow the moved stat
                        draggingStatIndex = targetIndex > draggingStatIndex ? targetIndex - 1 : targetIndex;
                    }
                }
                return true;
            }

            if (isDragging) {
                previewX = (int) mouseX - dragOffsetX;
                previewY = (int) mouseY - dragOffsetY;

                // Clamp to screen bounds
                int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                int screenHeight = minecraft.getWindow().getGuiScaledHeight();

                previewX = Math.max(0, Math.min(previewX, screenWidth - previewWidth));
                previewY = Math.max(0, Math.min(previewY, screenHeight - previewHeight - 60));

                return true;
            }
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    private boolean isMouseOverPreview(double mouseX, double mouseY) {
        int boxX = previewX - StatsRenderer.PADDING;
        int boxY = previewY - StatsRenderer.PADDING;
        return mouseX >= boxX && mouseX <= boxX + previewWidth
            && mouseY >= boxY && mouseY <= boxY + previewHeight;
    }

    private boolean isMouseOverResizeHandle(double mouseX, double mouseY) {
        int boxX = previewX - StatsRenderer.PADDING;
        int boxY = previewY - StatsRenderer.PADDING;
        int handleX = boxX + previewWidth - HANDLE_SIZE;
        int handleY = boxY + previewHeight - HANDLE_SIZE;
        return mouseX >= handleX && mouseX <= boxX + previewWidth
            && mouseY >= handleY && mouseY <= boxY + previewHeight;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
