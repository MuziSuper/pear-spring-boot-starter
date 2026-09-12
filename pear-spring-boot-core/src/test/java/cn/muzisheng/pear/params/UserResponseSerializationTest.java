package cn.muzisheng.pear.params;

import cn.muzisheng.pear.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserResponseSerializationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void loginResponseIncludesPublicFieldsAndTokenButNotPassword() throws Exception {
        LoginResponse response = LoginResponse.from(user(), "jwt-token");

        JsonNode json = OBJECT_MAPPER.valueToTree(response);

        assertPublicUserFields(json);
        assertEquals("jwt-token", json.get("token").asText());
        assertAbsent(json, "password");
    }

    @Test
    void currentUserResponseIncludesOnlyPublicFields() throws Exception {
        CurrentUserResponse response = CurrentUserResponse.from(user());

        JsonNode json = OBJECT_MAPPER.valueToTree(response);

        assertPublicUserFields(json);
        assertAbsent(json, "password", "token", "lastLoginIp", "profile", "source");
    }

    @Test
    void userEntitySerializationHidesSensitiveFields() throws Exception {
        JsonNode json = OBJECT_MAPPER.valueToTree(user());

        assertPublicUserFields(json);
        assertAbsent(json, "password", "token", "lastLoginIp", "profile", "source");
    }

    private static void assertPublicUserFields(JsonNode json) {
        assertEquals(7L, json.get("id").asLong());
        assertEquals("alice@example.com", json.get("email").asText());
        assertEquals("Alice", json.get("displayName").asText());
        assertEquals("A", json.get("firstName").asText());
        assertEquals("Liddell", json.get("lastName").asText());
        assertEquals("/avatar.png", json.get("avatar").asText());
        assertEquals("en_US", json.get("locale").asText());
        assertEquals("UTC", json.get("timezone").asText());
        assertEquals("+1-555-0100", json.get("phone").asText());
    }

    private static void assertAbsent(JsonNode json, String... fieldNames) {
        for (String fieldName : fieldNames) {
            assertFalse(json.has(fieldName), fieldName + " should not be serialized");
        }
    }

    private static User user() {
        User user = new User();
        user.setId(7L);
        user.setEmail("alice@example.com");
        user.setDisplayName("Alice");
        user.setFirstName("A");
        user.setLastName("Liddell");
        user.setAvatar("/avatar.png");
        user.setLocale("en_US");
        user.setTimezone("UTC");
        user.setPhone("+1-555-0100");
        user.setPassword("hashed-password");
        user.setToken("entity-token");
        user.setLastLoginIp("127.0.0.1");
        user.setProfile("{\"secret\":true}");
        user.setSource("internal");
        return user;
    }
}
