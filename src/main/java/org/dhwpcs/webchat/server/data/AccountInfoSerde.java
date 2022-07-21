package org.dhwpcs.webchat.server.data;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class AccountInfoSerde implements JsonSerializer<AccountInfo>, JsonDeserializer<AccountInfo> {
    @Override
    public AccountInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(typeOfT.equals(AccountInfo.class)) {
            if(json instanceof JsonObject $) {
                UUID uid = context.deserialize($.get("uuid"), UUID.class);
                String name = $.getAsJsonPrimitive("name").getAsString();
                String passwd = $.getAsJsonPrimitive("pwd").getAsString();
                return new AccountInfo(uid, name, passwd);
            }
            throw new JsonParseException("Expected JsonObject, got "+json.getClass());
        }
        throw new JsonParseException("typeOfT is not AccountInfo!");
    }

    @Override
    public JsonElement serialize(AccountInfo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject $ = new JsonObject();
        $.add("uuid", context.serialize(src.uid()));
        $.addProperty("name", src.name());
        $.addProperty("pwd", src.passwd());
        return $;
    }
}
