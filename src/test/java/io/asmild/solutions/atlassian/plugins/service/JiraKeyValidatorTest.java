package io.asmild.atlas.plugins.service;

import io.asmild.atlas.plugins.client.JiraAppLinkClient;
import io.asmild.atlas.plugins.exceptions.ApplicationConnectionErrorException;
import io.asmild.atlas.plugins.exceptions.JiraTicketNotFoundException;
import io.asmild.atlas.plugins.models.JiraIssue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class JiraKeyValidatorTest {

    @Test
    public void testAreJiraTicketsValid_AllValid() throws Exception {
        JiraAppLinkClient mockJiraClient = Mockito.mock(JiraAppLinkClient.class);
        Mockito.when(mockJiraClient.getTicketDetails(Mockito.anyString())).thenReturn(Mockito.anyString());

        List<String> issueIds = Arrays.asList("JIRA-123", "JIRA-456");
        List<String> result = JiraKeyValidator.areJiraTicketsValid(issueIds, mockJiraClient);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testAreJiraTicketsValid_SomeInvalid() throws Exception {
        JiraAppLinkClient mockJiraClient = Mockito.mock(JiraAppLinkClient.class);
        Mockito.when(mockJiraClient.getTicketDetails(Mockito.anyString())).thenThrow(new JiraTicketNotFoundException("Ticket not found"));

        List<String> issueIds = Arrays.asList("JIRA-123", "JIRA-456");
        List<String> result = JiraKeyValidator.areJiraTicketsValid(issueIds, mockJiraClient);

        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("ticket id 'JIRA-123' is not valid", result.get(0));
    }

    @Test
    public void testAreJiraTicketsValid_ConnectionError() throws Exception {
        JiraAppLinkClient mockJiraClient = Mockito.mock(JiraAppLinkClient.class);
        Mockito.when(mockJiraClient.getTicketDetails(Mockito.anyString())).thenThrow(new ApplicationConnectionErrorException("Connection error"));

        List<String> issueIds = Arrays.asList("JIRA-123", "JIRA-456");
        List<String> result = JiraKeyValidator.areJiraTicketsValid(issueIds, mockJiraClient);

        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("Connection error", result.get(0));
    }
}
