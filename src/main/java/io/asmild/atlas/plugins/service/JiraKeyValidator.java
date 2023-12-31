package io.asmild.atlas.plugins.service;

import java.util.ArrayList;
import java.util.List;
import io.asmild.atlas.plugins.client.JiraAppLinkClient;
import io.asmild.atlas.plugins.exceptions.ApplicationConnectionErrorException;
import io.asmild.atlas.plugins.exceptions.JiraTicketNotFoundException;
import io.asmild.atlas.plugins.exceptions.ApplicationLinkNotConfiguredException;

public class JiraKeyValidator {

    private JiraKeyValidator() {}
    public static List<String> areJiraTicketsValid(List<String> issueIds, JiraAppLinkClient jiraClient) {
        List<String> rejects = new ArrayList<>();
        boolean breakLoop = false;
        for (String issueId : issueIds) {
            try {
                jiraClient.getTicketDetails(issueId);
            } catch (Exception e) {
                if (e instanceof ApplicationConnectionErrorException) {
                    breakLoop = true;
                } else if (e instanceof JiraTicketNotFoundException) {
                    rejects.add("ticket id '" + issueId + "' is not valid");
                } else if (e instanceof ApplicationLinkNotConfiguredException) {
                    breakLoop = true;
                } else {
                    rejects.add("Error occurred: " + e.getMessage());
                }
                if (breakLoop) {
                    rejects.add(e.getMessage());
                    break;
                }
            }
        }

        return rejects;
    }
}