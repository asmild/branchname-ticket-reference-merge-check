package io.asmild.atlas.plugins.service;

import java.util.Arrays;
import java.util.List;
import com.atlassian.bitbucket.user.ApplicationUser;

public class UsersValidator
{
    private UsersValidator() {}
    public static boolean isUserExcluded(
            ApplicationUser currentUser,
            String usersExceptionGroups) {
        List<String> usersList = Arrays.asList(usersExceptionGroups.split(","));
        return usersList.contains(currentUser.getName());
    }
}
