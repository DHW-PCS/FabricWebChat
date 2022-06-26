package org.dhwpcs.webchat.structure;

import java.util.UUID;

public class ServerGroup implements Group {

    private final UUID uid;

    public ServerGroup(UUID uid) {
        this.uid = uid;
    }

    @Override
    public UUID groupUid() {
        return uid;
    }
}
