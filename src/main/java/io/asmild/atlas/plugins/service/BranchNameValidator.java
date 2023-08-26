package io.asmild.atlas.plugins.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BranchNameValidator {
    private BranchNameValidator(){}
    public static boolean isExceptionalBranch(String branchName, String exceptionRegex) {
        Pattern branchExceptionPattern = Pattern.compile(exceptionRegex);
        Matcher branchxceptionMatcher = branchExceptionPattern.matcher(branchName);
        return branchxceptionMatcher.find();
    }
}
