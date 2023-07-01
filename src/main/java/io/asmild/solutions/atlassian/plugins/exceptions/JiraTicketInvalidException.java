package io.asmild.solutions.atlassian.plugins.exceptions;

public class JiraTicketInvalidException extends Exception {
    public JiraTicketInvalidException(String message) {
        super(message);
    }
}
