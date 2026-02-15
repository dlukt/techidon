package de.icod.techidon.ui.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Optional;
import de.icod.techidon.model.Account;
import de.icod.techidon.model.AccountField;

public class PronounHelperTest {

    @Test
    public void testExtractPronouns_FoundInEnglish() {
        Account account = new Account();
        account.fields = new ArrayList<>();
        AccountField f = new AccountField();
        f.name = "Pronouns";
        f.value = "he/him";
        account.fields.add(f);

        Optional<String> result = PronounHelper.extractPronouns("pronouns", account);
        assertTrue(result.isPresent());
        assertEquals("he/him", result.get());
    }

    @Test
    public void testExtractPronouns_FoundInLocalized() {
        Account account = new Account();
        account.fields = new ArrayList<>();
        AccountField f = new AccountField();
        f.name = "Pronomi"; // Italian
        f.value = "lui";
        account.fields.add(f);

        Optional<String> result = PronounHelper.extractPronouns("pronomi", account);
        assertTrue(result.isPresent());
        assertEquals("lui", result.get());
    }

    @Test
    public void testExtractPronouns_NotFound() {
        Account account = new Account();
        account.fields = new ArrayList<>();
        AccountField f = new AccountField();
        f.name = "Website";
        f.value = "https://example.com";
        account.fields.add(f);

        Optional<String> result = PronounHelper.extractPronouns("pronouns", account);
        assertFalse(result.isPresent());
    }

    @Test
    public void testExtractPronouns_MultipleFields() {
        Account account = new Account();
        account.fields = new ArrayList<>();

        AccountField f1 = new AccountField();
        f1.name = "Website";
        f1.value = "https://example.com";
        account.fields.add(f1);

        AccountField f2 = new AccountField();
        f2.name = "Pronouns";
        f2.value = "she/her";
        account.fields.add(f2);

        Optional<String> result = PronounHelper.extractPronouns("pronouns", account);
        assertTrue(result.isPresent());
        assertEquals("she/her", result.get());
    }

    @Test
    public void testExtractPronouns_MixedCase() {
        Account account = new Account();
        account.fields = new ArrayList<>();
        AccountField f = new AccountField();
        f.name = "My Pronouns";
        f.value = "they/them";
        account.fields.add(f);

        Optional<String> result = PronounHelper.extractPronouns("pronouns", account);
        assertTrue(result.isPresent());
        assertEquals("they/them", result.get());
    }

    @Test
    public void testExtractPronouns_Url() {
        Account account = new Account();
        account.fields = new ArrayList<>();
        AccountField f = new AccountField();
        f.name = "Pronouns";
        f.value = "https://pronouns.page/he/him";
        account.fields.add(f);

        Optional<String> result = PronounHelper.extractPronouns("pronouns", account);
        assertTrue(result.isPresent());
        assertEquals("he/him", result.get());
    }
}
