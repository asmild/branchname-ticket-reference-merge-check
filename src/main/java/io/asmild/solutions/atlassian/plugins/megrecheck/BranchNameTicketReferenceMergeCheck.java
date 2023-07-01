package io.asmild.solutions.atlassian.plugins.megrecheck;

import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import javax.annotation.Nonnull;

public interface BranchNameTicketReferenceMergeCheck extends RepositoryMergeCheck{
    @Nonnull
    RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                   @Nonnull PullRequestMergeHookRequest request);
}
