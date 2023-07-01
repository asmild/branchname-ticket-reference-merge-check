package io.asmild.solutions.atlassian.plugins.exceptions;

public class ApplicationConnectionErrorException extends Exception {
    public ApplicationConnectionErrorException(String message) {
        super(message);
    }
}
