package io.asmild.solutions.atlassian.plugins.service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.atlassian.bitbucket.user.ApplicationUser;

public class UsersValidatorTest {
    @Test
    public void testIsUserExcluded_UserExcluded() {
        ApplicationUser mockUser = Mockito.mock(ApplicationUser.class);
        Mockito.when(mockUser.getName()).thenReturn("excludedUser");

        boolean result = UsersValidator.isUserExcluded(mockUser, "excludedUser,someUser");

        Assert.assertTrue(result);
    }

    @Test
    public void testIsUserExcluded_UserNotInList() {
        ApplicationUser mockUser = Mockito.mock(ApplicationUser.class);
        Mockito.when(mockUser.getName()).thenReturn("otherUser");

        boolean result = UsersValidator.isUserExcluded(mockUser, "excludedUser,includedUser");

        Assert.assertFalse(result);
    }
}
