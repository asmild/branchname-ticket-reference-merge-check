package io.asmild.solutions.atlassian.plugins.client;

import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationLinkNotConfiguredException;

public interface JiraAppLinkClient {
    String getTicketDetails(String s) throws Exception;
}
