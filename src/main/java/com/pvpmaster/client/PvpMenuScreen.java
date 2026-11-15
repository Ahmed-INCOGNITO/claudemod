package com.pvpmaster.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PvpMenuScreen extends Screen {
    private static final String[] MODES = {"sword", "axe"};
    private static final String[] LEVELS = {"easy", "normal", "hard", "expert"};
    private static int mode = 0, level = 1, count = 1;

    private ButtonWidget modeBtn, levelBtn, countBtn;

    public PvpMenuScreen() { super(Text.literal("PvP Master")); }

    @Override
    protected void init() {
        int cx = this.width / 2, y = this.height / 2 - 55;
        modeBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> { mode = (mode + 1) % MODES.length; refresh(); })
                .dimensions(cx - 90, y, 180, 20).build());
        levelBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> { level = (level + 1) % LEVELS.length; refresh(); })
                .dimensions(cx - 90, y + 25, 180, 20).build());
        countBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> { count = count % 3 + 1; refresh(); })
                .dimensions(cx - 90, y + 50, 180, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Start Fight"), b -> {
            if (this.client != null && this.client.player != null) {
                this.client.player.networkHandler.sendChatCommand(
                        "pvp spawn " + MODES[mode] + " " + LEVELS[level] + " " + count);
            }
            this.close();
        }).dimensions(cx - 90, y + 80, 180, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear Bots"), b -> {
            if (this.client != null && this.client.player != null) this.client.player.networkHandler.sendChatCommand("pvp clear");
        }).dimensions(cx - 90, y + 105, 88, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> this.close())
                .dimensions(cx + 2, y + 105, 88, 20).build());
        refresh();
    }

    private void refresh() {
        modeBtn.setMessage(Text.literal("Mode: " + cap(MODES[mode])));
        levelBtn.setMessage(Text.literal("Difficulty: " + cap(LEVELS[level])));
        countBtn.setMessage(Text.literal("Bots: " + count));
    }

    private static String cap(String s) { return Character.toUpperCase(s.charAt(0)) + s.substring(1); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
    }
}
