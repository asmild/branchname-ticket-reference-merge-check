package io.asmild.solutions.atlassian.plugins.exceptions;

import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.Response;
public class ResourceNotFoundException extends ResponseException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
