package org.dhwpcs.webchat.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.network.protocol.v1r0.ProtocolV1R0;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Validator {

    public static final Validator DEFAULT = Validator.create();
    private Map<String, Supplier<Protocol>> supported = new HashMap<>();

    public boolean register(String version, Supplier<Protocol> lazy) {
        return supported.putIfAbsent(version, lazy) == null;
    }

    public Protocol validate(JsonElement je) {
        if(je instanceof JsonObject $) {
            if($.has("protocol")) {
                String version = $.getAsJsonPrimitive("protocol").getAsString();
                Supplier<Protocol> sup = supported.get(version);
                if(sup == null) {
                    return null;
                } else {
                    return sup.get();
                }
            }
        }
        throw new IllegalArgumentException();
    }

    public static Validator create() {
        Validator v = new Validator();
        v.register("1.0", ProtocolV1R0::new);
        return v;
    }
}
