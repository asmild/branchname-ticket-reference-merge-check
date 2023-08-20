package io.asmild.solutions.atlassian.plugins.megrecheck;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import io.asmild.solutions.atlassian.plugins.client.JiraAppLinkClient;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.Ref;

import io.asmild.solutions.atlassian.plugins.service.BranchNameValidator;
import io.asmild.solutions.atlassian.plugins.service.JiraKeyValidator;
import io.asmild.solutions.atlassian.plugins.service.TicketReference;

import io.asmild.solutions.atlassian.plugins.service.UsersValidator;
import org.springframework.stereotype.Component;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("BranchNameTicketReferenceMergeCheck")
public class BranchNameTicketReferenceMergeCheck implements RepositoryMergeCheck{
    @ComponentImport
    private final AuthenticationContext authenticationContext;
    private final JiraAppLinkClient jiraClient;

    public BranchNameTicketReferenceMergeCheck(
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

        if (ticketRegex.equals("")) {
            ticketRegex = "([A-Z][A-Z\\d_]*)-(\\d+)";
        }

//      Get rid of escape character field - leave default - !
        String escapedRegex = escapeCharacter + ticketRegex;

//      Getting branch name to merge from
        PullRequest pullRequest = request.getPullRequest();
        Ref fromRef = pullRequest.getFromRef();
        String branchName = fromRef.getDisplayId();

//      Discovering keyId
        issuesIds = TicketReference.getIds(branchName,ticketRegex);

        Pattern escappedJiraTicketPattern = Pattern.compile(escapedRegex);
        Matcher escapedJiraTicketMatcher = escappedJiraTicketPattern.matcher(branchName);

        while (escapedJiraTicketMatcher.find()) {
            issuesIds.removeIf(item -> escapedJiraTicketMatcher.group().contains(item));
        }

        if (sourceBranchExceptionEnabled) {
            excludedBySourceBranch = BranchNameValidator.isExceptionalBranch(branchName,sourceBranchExceptionRegex);
        }

        if (targetBranchExceptionEnabled) {
            excludedByTragetBranch = BranchNameValidator.isExceptionalBranch(branchName,targetBranchExceptionRegex);
        }

        if (usersExceptionEnabled) {
            usersException = UsersValidator.isUserExcluded(authenticationContext.getCurrentUser(),usersExceptionGroups);
        }

        if (excludedBySourceBranch || excludedByTragetBranch || usersException ) {
            return RepositoryHookResult.accepted();
        }

        if (!issuesIds.isEmpty()){
            if (issuesIds.size() > 1  && !multipleKeysAllowed) {
                return RepositoryHookResult.rejected("Multiple Jira keys discovered", "Only one Jira key is allowed, but " + issuesIds.size() + " discovered");
            }

            if (ticketsValidationEnabled) {
                List<String> rejects = new ArrayList<>();

                rejects = JiraKeyValidator.areJiraTicketsValid(issuesIds,jiraClient);

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
