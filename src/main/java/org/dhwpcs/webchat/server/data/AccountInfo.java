package org.dhwpcs.webchat.server.data;

import java.util.UUID;

public record AccountInfo(UUID uid, String name, String passwd) {}
