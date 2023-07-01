package io.asmild.solutions.atlassian.plugins.megrecheck;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.net.ResponseException;
import io.asmild.solutions.atlassian.plugins.client.JiraAppLinkClient;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.Ref;
import com.atlassian.bitbucket.user.ApplicationUser;

import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationConnectionErrorException;
import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationLinkNotConfiguredException;
import io.asmild.solutions.atlassian.plugins.exceptions.JiraTicketNotFoundException;
import io.asmild.solutions.atlassian.plugins.exceptions.ResourceNotFoundException;
import io.asmild.solutions.atlassian.plugins.models.JiraIssue;
import org.springframework.stereotype.Component;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("BranchNameTicketReferenceMergeCheck")
public class BranchNameTicketReferenceMergeCheckImpl implements BranchNameTicketReferenceMergeCheck {
    @ComponentImport
    private final AuthenticationContext authenticationContext;
    private final JiraAppLinkClient jiraClient;

    public BranchNameTicketReferenceMergeCheckImpl(
            AuthenticationContext authenticationContext,
            JiraAppLinkClient jiraClient
    ) {
        this.authenticationContext = authenticationContext;
        this.jiraClient = jiraClient;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {

        boolean debug = true;
        String ticketRegex = context.getSettings().getString("issueKeyRegex", "");
        String escapeCharacter = context.getSettings().getString("issueKeyEscapeCharacter", "");
        boolean ticketsValidationEnabled = context.getSettings().getBoolean("ticketsValidationEnabled", false);
        boolean sourceBranchExceptionEnabled = context.getSettings().getBoolean("sourceBranchExceptionEnabled", false);
        boolean targetBranchExceptionEnabled = context.getSettings().getBoolean("targetBranchExceptionEnabled", false);
        boolean usersExceptionEnabled = context.getSettings().getBoolean("usersExceptionEnabled", false);
        boolean multipleKeysAllowed = context.getSettings().getBoolean("multipleKeys", false);
        String sourceBranchExceptionRegex = context.getSettings().getString("sourceBranchExceptionRegex", "");
        String targetBranchExceptionRegex = context.getSettings().getString("targetBranchExceptionRegex", "");
        String usersExceptionGroups = context.getSettings().getString("usersExceptionGroups", "");
        boolean excludedBySourceBranch = false;
        boolean excludedByTragetBranch = false;
        boolean usersException = false;
        List<String> issuesIds = new ArrayList<>();

        if (ticketRegex == "") {
            ticketRegex = "([A-Z][A-Z\\d_]*)-(\\d+)";
        }

//        Get rid of escape character field - leave default - !
        String escapedRegex = escapeCharacter + ticketRegex;



//      Getting branch name to merge from
        PullRequest pullRequest = request.getPullRequest();
        Ref fromRef = pullRequest.getFromRef();
        String branchName = fromRef.getDisplayId();

//      Discovering keyId
        Pattern jiraTicketPattern = Pattern.compile(ticketRegex);
        Matcher jiraTicketMatcher = jiraTicketPattern.matcher(branchName);
        Pattern escappedJiraTicketPattern = Pattern.compile(escapedRegex);
        Matcher escapedJiraTicketMatcher = escappedJiraTicketPattern.matcher(branchName);

        while (jiraTicketMatcher.find()) {
            issuesIds.add(jiraTicketMatcher.group());
        }

        while (escapedJiraTicketMatcher.find()) {
            issuesIds.removeIf(item -> escapedJiraTicketMatcher.group().contains(item));
        }

        if (sourceBranchExceptionEnabled) {
            Pattern sourceBranchExceptionPattern = Pattern.compile(sourceBranchExceptionRegex);
            Matcher sourceBranchxceptionMatcher = sourceBranchExceptionPattern.matcher(branchName);
            excludedBySourceBranch = sourceBranchxceptionMatcher.find();
        }

        if (targetBranchExceptionEnabled) {
            Pattern targetBranchExceptionPattern = Pattern.compile(targetBranchExceptionRegex);
            Matcher targetBranchxceptionMatcher = targetBranchExceptionPattern.matcher(branchName);
            excludedByTragetBranch = targetBranchxceptionMatcher.find();
        }

        if (usersExceptionEnabled) {
            ApplicationUser currentUser = authenticationContext.getCurrentUser();
            List<String> usersList = Arrays.asList(usersExceptionGroups.split(","));
            usersException = usersList.contains(currentUser.getName());
        }

        if (excludedBySourceBranch || excludedByTragetBranch || usersException ) {
            return RepositoryHookResult.accepted();
        }

        if (issuesIds.size() > 0){
            if (issuesIds.size() > 1  && !multipleKeysAllowed) {
                return RepositoryHookResult.rejected("Multiple Jira keys discovered", "Only one Jira key is allowed, but " + issuesIds.size() + " discovered");
            }

            if (ticketsValidationEnabled) {
                List<String> rejects = new ArrayList<>();

                for (String issueId : issuesIds) {
                    try {
                        JiraIssue jiraIssue = new JiraIssue(jiraClient.getTicketDetails(issueId));
                    } catch (Exception e) {
                        if (e instanceof ApplicationConnectionErrorException) {
                            rejects.add(e.getMessage());
                            break;
                        } else if (e instanceof JiraTicketNotFoundException) {
                            rejects.add("ticket id '" + issueId + "' is not valid");
                        } else if (e instanceof ApplicationLinkNotConfiguredException) {
                            rejects.add(e.getMessage());
                            break;
                        }else {
                            rejects.add("Error occurred: " + e.getMessage());
                        }
                    }
                }

                if (!rejects.isEmpty()) {
                    return RepositoryHookResult.rejected("Unable to validate Jira ticket", String.join(", ", rejects));
                }
            }
            return RepositoryHookResult.accepted();
        } else {
            return RepositoryHookResult.rejected("No Jira ticket IDs found.", "The branch name must include a valid Jira ticket ID.");
        }
    }
}
