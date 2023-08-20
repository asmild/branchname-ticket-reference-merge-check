package io.asmild.solutions.atlassian.plugins.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TicketReference {

    private TicketReference() {}
    public static List<String> getIds(String branchName, String regex) {
        List<String> issuesIds = new ArrayList<>();

        Pattern jiraTicketPattern = Pattern.compile(regex);
        Matcher jiraTicketMatcher = jiraTicketPattern.matcher(branchName);

        while (jiraTicketMatcher.find()) {
            issuesIds.add(jiraTicketMatcher.group());
        }

        return issuesIds;
    }
}
