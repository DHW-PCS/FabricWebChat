package org.dhwpcs.webchat.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Validator {
    private Map<String, Supplier<Protocol>> supported = new HashMap<>();
    private Protocol protocol;

    public boolean register(String version, Supplier<Protocol> lazy) {
        return supported.putIfAbsent(version, lazy) == null;
    }

    public void validate(JsonElement je) {
        if(je instanceof JsonObject $) {
            if($.has("protocol")) {
                String version = $.getAsJsonPrimitive("protocol").getAsString();
                protocol = supported.get(version).get();
            }
        }
        throw new IllegalArgumentException();
    }
}
