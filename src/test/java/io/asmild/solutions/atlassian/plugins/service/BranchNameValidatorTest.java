package io.asmild.solutions.atlassian.plugins.service;

import org.junit.Test;
import org.junit.Assert;
public class BranchNameValidatorTest {
    @Test
    public void testIsExceptionalBranch_Matches() {
        String branchName = "exception_branch";
        String exceptionRegex = "exception_.*";

        boolean result = BranchNameValidator.isExceptionalBranch(branchName, exceptionRegex);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsExceptionalBranch_NoMatch() {
        String branchName = "normal_branch";
        String exceptionRegex = "exception_.*";

        boolean result = BranchNameValidator.isExceptionalBranch(branchName, exceptionRegex);

        Assert.assertFalse(result);
    }

}
