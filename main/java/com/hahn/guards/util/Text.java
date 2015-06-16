package com.hahn.guards.util;

import net.minecraft.client.gui.FontRenderer;

public class Text {
    public static final String Italic = "\u00A7o";

    public static final String Important = Italic + Color.RED;
    
    public enum Color {        
        BLACK       (0x000000, "\u00A70"),
        DARK_BLUE   (0x0000AA, "\u00A71"),
        DARK_GREEN  (0x00AA00, "\u00A72"),
        DARK_AQUA   (0x00AAAA, "\u00A73"),
        DARK_RED    (0xAA0000, "\u00A74"),
        DARK_PURPLE (0xAA00AA, "\u00A75"),
        GOLD        (0xFFAA00, "\u00A76"),
        GRAY        (0xAAAAAA, "\u00A77"),
        DARK_GRAY   (0x555555, "\u00A78"),
        BLUE        (0x5555FF, "\u00A79"),
        GREEN       (0x55FF55, "\u00A7a"),
        AQUA        (0x55FFFF, "\u00A7b"),
        RED         (0xFF0000, "\u00A7c"),
        LIGHT_PURPLE(0xFF55FF, "\u00A7d"),
        YELLOW      (0xFFFF55, "\u00A7e"),
        WHITE       (0xFFFFFF, "\u00A7f");
        
        public final int hex;
        public final String str;
        private Color(int i, String s) {
            hex = i;
            str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    public enum Align {
        CENTER, LEFT, RIGHT;
    }
    
    public static void drawString(FontRenderer fontRenderer, String str, int x, int y, int color) {
        drawString(fontRenderer, Align.LEFT, str, x, y, color);
    }
    
    public static void drawString(FontRenderer fontRenderer, Align align, String str, int x, int y, int color) {
        if (align == Align.CENTER) x -= fontRenderer.getStringWidth(str) / 2;
        else if (align == Align.RIGHT) x -= fontRenderer.getStringWidth(str);
        
        fontRenderer.drawString(str, x, y, color);
    }
}
