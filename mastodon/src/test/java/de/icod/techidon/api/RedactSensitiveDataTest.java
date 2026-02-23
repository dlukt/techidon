package de.icod.techidon.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedactSensitiveDataTest {

    private final MastodonAPIController controller = new MastodonAPIController(null);

    @Test
    public void testRedactSensitiveKeys() {
        String[] sensitiveKeys = {
            "access_token",
            "refresh_token",
            "client_secret",
            "password",
            "email",
            "code",
            "bearer_token",
            "authorization_code",
            "otp_attempt",
            "otp_token",
            "encrypted_message",
            "private_key",
            "auth_key",
            "unified_push_token",
            "instance",
            "endpoint",
            "auth"
        };

        for (String key : sensitiveKeys) {
            JsonObject json = new JsonObject();
            json.addProperty(key, "super_secret_value");
            json.addProperty("other_key", "public_value");

            JsonElement redacted = controller.redactSensitiveData(json);

            assertTrue(redacted.isJsonObject());
            JsonObject redactedObj = redacted.getAsJsonObject();
            assertEquals("[REDACTED]", redactedObj.get(key).getAsString());
            assertEquals("public_value", redactedObj.get("other_key").getAsString());
        }
    }

    @Test
    public void testRedactNestedObjects() {
        JsonObject nested = new JsonObject();
        nested.addProperty("password", "secret");

        JsonObject root = new JsonObject();
        root.add("user", nested);

        JsonElement redacted = controller.redactSensitiveData(root);

        assertTrue(redacted.isJsonObject());
        JsonObject redactedRoot = redacted.getAsJsonObject();
        JsonObject redactedNested = redactedRoot.getAsJsonObject("user");

        assertEquals("[REDACTED]", redactedNested.get("password").getAsString());
    }

    @Test
    public void testRedactArray() {
        JsonObject sensitiveObj = new JsonObject();
        sensitiveObj.addProperty("email", "test@example.com");

        JsonArray array = new JsonArray();
        array.add(sensitiveObj);

        JsonElement redacted = controller.redactSensitiveData(array);

        assertTrue(redacted.isJsonArray());
        JsonArray redactedArray = redacted.getAsJsonArray();
        JsonObject redactedObj = redactedArray.get(0).getAsJsonObject();

        assertEquals("[REDACTED]", redactedObj.get("email").getAsString());
    }

    @Test
    public void testCaseInsensitivity() {
        JsonObject json = new JsonObject();
        json.addProperty("PaSsWoRd", "secret");

        JsonElement redacted = controller.redactSensitiveData(json);

        assertTrue(redacted.isJsonObject());
        JsonObject redactedObj = redacted.getAsJsonObject();
        assertEquals("[REDACTED]", redactedObj.get("PaSsWoRd").getAsString());
    }

    @Test
    public void testPreserveNonSensitive() {
        JsonObject json = new JsonObject();
        json.addProperty("username", "testuser");
        json.addProperty("id", 12345);

        JsonElement redacted = controller.redactSensitiveData(json);

        assertTrue(redacted.isJsonObject());
        JsonObject redactedObj = redacted.getAsJsonObject();
        assertEquals("testuser", redactedObj.get("username").getAsString());
        assertEquals(12345, redactedObj.get("id").getAsInt());
    }
}
