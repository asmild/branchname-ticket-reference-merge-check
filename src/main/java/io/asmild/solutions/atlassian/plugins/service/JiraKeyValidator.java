package io.asmild.solutions.atlassian.plugins.service;

import java.util.ArrayList;
import java.util.List;
import io.asmild.solutions.atlassian.plugins.client.JiraAppLinkClient;
import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationConnectionErrorException;
import io.asmild.solutions.atlassian.plugins.exceptions.JiraTicketNotFoundException;
import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationLinkNotConfiguredException;
//import RepositoryHookResult

public class JiraKeyValidator {
    public static List<String> areJiraTicketsValid(List<String> issueIds, JiraAppLinkClient jiraClient) {
        List<String> rejects = new ArrayList<>();

        for (String issueId : issueIds) {
            try {
                jiraClient.getTicketDetails(issueId);
            } catch (Exception e) {
                if (e instanceof ApplicationConnectionErrorException) {
                    rejects.add(e.getMessage());
                    break;
                } else if (e instanceof JiraTicketNotFoundException) {
                    rejects.add("ticket id '" + issueId + "' is not valid");
                } else if (e instanceof ApplicationLinkNotConfiguredException) {
                    rejects.add(e.getMessage());
                    break;
                } else {
                    rejects.add("Error occurred: " + e.getMessage());
                }
            }
        }

        return rejects;
    }
}
//        if (!issuesIds.isEmpty()){
//            if (issuesIds.size() > 1  && !multipleKeysAllowed) {
//                return RepositoryHookResult.rejected("Multiple Jira keys discovered", "Only one Jira key is allowed, but " + issuesIds.size() + " discovered");
//            }
//
//            if (ticketsValidationEnabled) {
//                List<String> rejects = new ArrayList<>();
//
//                for (String issueId : issuesIds) {
//                    try {
//                        // TODO: check issue status
////                        JiraIssue jiraIssue = new JiraIssue(jiraClient.getTicketDetails(issueId));
//                        jiraClient.getTicketDetails(issueId);
//                    } catch (Exception e) {
//                        if (e instanceof ApplicationConnectionErrorException) {
//                            rejects.add(e.getMessage());
//                            break;
//                        } else if (e instanceof JiraTicketNotFoundException) {
//                            rejects.add("ticket id '" + issueId + "' is not valid");
//                        } else if (e instanceof ApplicationLinkNotConfiguredException) {
//                            rejects.add(e.getMessage());
//                            break;
//                        }else {
//                            rejects.add("Error occurred: " + e.getMessage());
//                        }
//                    }
//                }
//
//                if (!rejects.isEmpty()) {
//                    return RepositoryHookResult.rejected("Unable to validate Jira ticket", String.join(", ", rejects));
//                }
//            }
//            return RepositoryHookResult.accepted();
//        } else {
//            return RepositoryHookResult.rejected("No Jira ticket IDs found.", "The branch name must include a valid Jira ticket ID.");
//        }