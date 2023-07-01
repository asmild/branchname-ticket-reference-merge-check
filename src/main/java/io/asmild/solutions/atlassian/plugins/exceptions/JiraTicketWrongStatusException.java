package io.asmild.solutions.atlassian.plugins.exceptions;

public class JiraTicketWrongStatusException extends Exception {
    public JiraTicketWrongStatusException(String message) {
        super(message);
    }
}
