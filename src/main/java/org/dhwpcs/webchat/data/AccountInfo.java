package org.dhwpcs.webchat.data;

import java.util.UUID;

public record AccountInfo(UUID uid, String name, String passwd) {}
