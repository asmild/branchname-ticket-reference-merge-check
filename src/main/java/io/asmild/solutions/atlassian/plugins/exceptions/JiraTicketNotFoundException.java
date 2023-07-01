package io.asmild.solutions.atlassian.plugins.exceptions;

public class JiraTicketNotFoundException extends Exception {
    public JiraTicketNotFoundException(String message) {
        super(message);
    }
}

