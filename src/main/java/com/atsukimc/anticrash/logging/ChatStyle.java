package com.atsukimc.anticrash.logging;

//? if (>=1.19 && <=1.21.11) {
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
//?} else if <=1.18.2 {
//import net.minecraft.text.LiteralText;
//import net.minecraft.text.MutableText;
//import net.minecraft.text.Text;
//import net.minecraft.util.Formatting;
//?} else if >=26.1 {
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.MutableComponent;
//?}

/** Renders an AntiCrash message as coloured Minecraft chat text; see {@link MessageStyle}. */
public final class ChatStyle {
    private ChatStyle() {
    }

    /**
     * @param base colour of the unstyled text, or null to inherit (white for feedback, red when
     *             Vanilla wraps the text as a command error)
     */
//? if (>=1.19 && <=1.21.11) {
    public static Text text(String message, Formatting base, boolean coloured) {
        if (message == null) return Text.literal("");
//?} else if <=1.18.2 {
//    public static Text text(String message, Formatting base, boolean coloured) {
//        if (message == null) return new LiteralText("");
//?} else if >=26.1 {
//    public static Component text(String message, ChatFormatting base, boolean coloured) {
//        if (message == null) return Component.literal("");
//?}
        if (!coloured) {
//? if (>=1.19 && <=1.21.11) {
            MutableText plain = Text.literal(message);
            return base == null ? plain : plain.formatted(base);
//?} else if <=1.18.2 {
//            MutableText plain = new LiteralText(message);
//            return base == null ? plain : plain.formatted(base);
//?} else if >=26.1 {
//            MutableComponent plain = Component.literal(message);
//            return base == null ? plain : plain.withStyle(base);
//?}
        }
//? if (>=1.19 && <=1.21.11) {
        MutableText result = Text.literal("");
        if (base != null) result.formatted(base);
//?} else if <=1.18.2 {
//        MutableText result = new LiteralText("");
//        if (base != null) result.formatted(base);
//?} else if >=26.1 {
//        MutableComponent result = Component.literal("");
//        if (base != null) result.withStyle(base);
//?}
        String[] lines = message.split("\n", -1);
        for (int index = 0; index < lines.length; index++) {
            if (index > 0) result.append("\n");
            for (MessageStyle.Span span : MessageStyle.spans(lines[index])) {
//? if (>=1.19 && <=1.21.11) {
                MutableText part = Text.literal(span.text());
//?} else if <=1.18.2 {
//                MutableText part = new LiteralText(span.text());
//?} else if >=26.1 {
//                MutableComponent part = Component.literal(span.text());
//?}
                switch (span.kind()) {
//? if <=1.21.11 {
                    case HEADER -> part.formatted(Formatting.BOLD);
//?} else if >=26.1 {
//                    case HEADER -> part.withStyle(ChatFormatting.BOLD);
//?}
                    case PREFIX, PLAIN -> { }
                }
                result.append(part);
            }
        }
        return result;
    }
}
