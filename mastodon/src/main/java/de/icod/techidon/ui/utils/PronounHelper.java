package de.icod.techidon.ui.utils;

import androidx.annotation.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.icod.techidon.model.Account;
import de.icod.techidon.model.AccountField;
import de.icod.techidon.ui.text.HtmlParser;

public class PronounHelper {

    private static final String[] pronounsUrls = new String[]{
            "pronouns.within.lgbt/",
            "pronouns.cc/pronouns/",
            "pronouns.page/"
    };

    private static final String PRONOUN_CHARS = "\\w*¿¡!?";
    private static final Pattern trimPronouns =
            Pattern.compile("[^" + PRONOUN_CHARS + "]*([" + PRONOUN_CHARS + "].*[" + PRONOUN_CHARS + "]|[" + PRONOUN_CHARS + "])\\W*");

    public static Optional<String> extractPronouns(String localizedPronouns, @Nullable Account account) {
        if (account == null || account.fields == null) return Optional.empty();

        // higher = worse. the lowest number wins. also i'm sorry for writing this
        String bestPronouns = null;
        int bestScore = Integer.MAX_VALUE;

        for (AccountField f : account.fields) {
            String t = f.name.toLowerCase();
            int localizedIndex = t.indexOf(localizedPronouns);
            int englishIndex = t.indexOf("pronouns");

            if (localizedIndex == -1 && englishIndex == -1) continue;

            // neutralizing an english fallback failure if the localized pronoun already succeeded
            // -t.length() + t.length() = 0 -> so the low localized score doesn't get obscured
            if (englishIndex < 0) englishIndex = localizedIndex > -1 ? -t.length() : t.length();
            if (localizedIndex < 0) localizedIndex = t.length();
            int score = (localizedIndex + t.length()) + (englishIndex + t.length()) * 100;

            if (score < bestScore) {
                String extracted = extractPronounsFromField(localizedPronouns, f, t);
                if (extracted != null) {
                    bestScore = score;
                    bestPronouns = extracted;
                }
            }
        }

        return Optional.ofNullable(bestPronouns);
    }

    private static String extractPronounsFromField(String localizedPronouns, AccountField field, String lowerCasedName) {
        if (!lowerCasedName.contains(localizedPronouns) &&
                !lowerCasedName.contains("pronouns")) return null;
        String text = HtmlParser.text(field.value);
        if (text.toLowerCase().contains("https://")) {
            for (String pronounUrl : pronounsUrls) {
                int index = text.indexOf(pronounUrl);
                int beginPronouns = index + pronounUrl.length();
                // we only want to display the info from the urls if they're not usernames
                if (index > -1 && beginPronouns < text.length() && text.charAt(beginPronouns) != '@') {
                    return text.substring(beginPronouns);
                }
            }
            // maybe it's like "they and them (https://pronouns.page/...)"
            String[] parts = text.substring(0, text.toLowerCase().indexOf("https://"))
                    .split(" ");
            if (parts.length == 0) return null;
            text = String.join(" ", parts);
        }

        Matcher matcher = trimPronouns.matcher(text);
        if (!matcher.find()) return null;
        String pronouns = matcher.group(1);

        // crude fix to allow for pronouns like "it(/she)" or "(de) sie/ihr"
        int missingParens = 0, missingBrackets = 0;
        for (char c : pronouns.toCharArray()) {
            if (c == '(') missingParens++;
            else if (c == '[') missingBrackets++;
            else if (c == ')') missingParens--;
            else if (c == ']') missingBrackets--;
        }
        if (missingParens > 0) pronouns += ")".repeat(missingParens);
        else if (missingParens < 0) pronouns = "(".repeat(missingParens * -1) + pronouns;
        if (missingBrackets > 0) pronouns += "]".repeat(missingBrackets);
        else if (missingBrackets < 0) pronouns = "[".repeat(missingBrackets * -1) + pronouns;

        // if ends with an un-closed custom emoji
        if (pronouns.matches("^.*\\s+:[a-zA-Z_]+$")) pronouns += ':';
        return pronouns;
    }
}
