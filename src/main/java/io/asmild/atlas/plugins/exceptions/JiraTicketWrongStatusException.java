package io.asmild.atlas.plugins.exceptions;

public class JiraTicketWrongStatusException extends Exception {
    public JiraTicketWrongStatusException(String message) {
        super(message);
    }
}
