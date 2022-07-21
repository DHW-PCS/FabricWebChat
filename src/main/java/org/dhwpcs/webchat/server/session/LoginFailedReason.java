package org.dhwpcs.webchat.server.session;

public enum LoginFailedReason {
    WRONG_ACCOUNT_OR_PASSWORD,
    ALREADY_LOGGING_IN,
    NOT_LOGGING_IN,
    ALREADY_LOGGED_IN,
    ACCOUNT_NOT_SPECIFIED,
    UNKNOWN
}
