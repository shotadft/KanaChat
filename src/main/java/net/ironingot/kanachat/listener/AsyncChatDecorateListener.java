package net.ironingot.kanachat.listener;

import biscotte.kana.Kana;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import net.ironingot.kanachat.KanaChat;
import net.ironingot.translator.KanaKanjiTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class AsyncChatDecorateListener implements Listener {
    private static final String excludeMatchString = "\u00a7|u00a74u00a75u00a73u00a74v|^http|^\\./";
    private static final Pattern excludePattern = Pattern.compile(excludeMatchString);
    private static final String systemMatchString = "^(#GLOBAL#|>)( *)(.*)";
    private static final Pattern systemPattern = Pattern.compile(systemMatchString);
    private static final String wordMatchString = "([a-z0-9!-/:-@\\[-`\\{-~]*)";
    private static final Pattern wordPattern = Pattern.compile(wordMatchString);
    private static final String prefixMatchString = "^([0-9!-/:-@\\[-`\\{-~]+)(.*?)";
    private static final Pattern prefixPattern = Pattern.compile(prefixMatchString);
    private static final String postfixMatchString = "(.*?)([0-9!-,.-/:-@\\[-`\\{-~]+)$";
    private static final Pattern postfixPattern = Pattern.compile(postfixMatchString);
    public final KanaChat plugin;

    public AsyncChatDecorateListener(KanaChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncChatDecorate(AsyncChatDecorateEvent event) {
        String system = "";
        String space = "";
        Component component = event.originalMessage();

        if (!(component instanceof TextComponent)) {
            return;
        }

        TextComponent textComponent = (TextComponent) component;

        String message = textComponent.content();

        if (message.startsWith("/")) {
            return;
        }

        if (message.startsWith(".")) {
            return;
        }

        Matcher systemMatcher = systemPattern.matcher(message);
        if (systemMatcher.find(0)) {
            system = systemMatcher.group(1);
            space = systemMatcher.group(2);
            message = systemMatcher.group(3);
        }

        Player player = event.player();
        if (player == null) {
            return;
        }

        boolean toKana = plugin.getConfiguration().isKanaEnabled(player.getName());
        boolean toKanji = plugin.getConfiguration().isKanjiEnabled(player.getName());

        if (!toKana) {
            return;
        }

        String dstMessage = translateJapanese(message, toKanji);

        if (dstMessage.equals(message)) {
            return;
        }

        event.result(formatMessage(system, dstMessage, message));
    }

    private Component formatMessage(String prefix, String dst, String src) {
        Component component = Component.empty();

        // [Prefix] <Converted Message> <Source Message>
        if (!prefix.isEmpty()) {
            component = component
                    .append(Component.text(prefix))
                    .append(Component.text(" "));
        }

        return component
                .append(Component.text(dst))
                .append(Component.text(" ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(src).color(NamedTextColor.DARK_GRAY));

    }

    public String translateJapanese(String message, Boolean toKanji) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isLastTranslated = true;
        Map<String, String> dictionary = plugin.getDictionary().getValues();

        for (String word : message.split(" ")) {
            Matcher excludeMatcher = excludePattern.matcher(word);
            if (excludeMatcher.find()) {
                stringBuilder.append(word);
                continue;
            }

            Matcher wordMatcher = wordPattern.matcher(word);
            if (!wordMatcher.matches()) {
                // with blank
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(word);
                isLastTranslated = false;
                continue;
            }

            // find prefix signs
            Matcher prefixMatcher = prefixPattern.matcher(word);
            String prefix = "";
            if (prefixMatcher.matches()) {
                prefix = prefixMatcher.group(1);
                word = prefixMatcher.group(2);
            }

            // find postfix signs
            Matcher postfixMatcher = postfixPattern.matcher(word);
            String postfix = "";
            if (postfixMatcher.matches()) {
                word = postfixMatcher.group(1);
                postfix = postfixMatcher.group(2);
            }

            String dictionaryValue = dictionary.get(word.toLowerCase());
            if (dictionaryValue != null) {
                if (!isLastTranslated) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(prefix).append(dictionaryValue).append(postfix);
                isLastTranslated = true;
                continue;
            }

            // Roma-Ji -> Hiragana translation
            Kana kana = new Kana();
            kana.setLine(word);
            kana.convert();
            String translatedWord = kana.getLine();

            // Hiragana -> Kanji translation
            if (toKanji) {
                int wordLength = word.length();
                int headLength = Math.min(wordLength, 2);
                int footLength = Math.min(wordLength, 2);

                if (translatedWord.startsWith(word.substring(0, headLength)) ||
                        translatedWord.endsWith(word.substring(wordLength - footLength, wordLength))) {
                    // it's not roma-ji may be.
                    translatedWord = word;

                    // with blank
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(" ");
                    }
                    isLastTranslated = false;
                } else {
                    translatedWord = KanaKanjiTranslator.translate(translatedWord);
                    // without blank in Japanese string

                    if (!isLastTranslated) {
                        stringBuilder.append(" ");
                    }
                    isLastTranslated = true;
                }
            }

            stringBuilder.append(prefix).append(translatedWord).append(postfix);
        }

        return stringBuilder.toString();
    }

}
