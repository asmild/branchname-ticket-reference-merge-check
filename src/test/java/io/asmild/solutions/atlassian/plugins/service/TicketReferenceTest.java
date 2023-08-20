package io.asmild.solutions.atlassian.plugins.service;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class TicketReferenceTest {
    @Test
    public void testGetIds_NoMatches() {
        String branchName = "no_ticket_references";
        String regex = "([A-Z][A-Z\\d_]*)-(\\d+)";

        List<String> result = TicketReference.getIds(branchName, regex);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetIds_SingleMatch() {
        String branchName = "JIRA-123_feature";
        String regex = "([A-Z][A-Z\\d_]*)-(\\d+)";

        List<String> result = TicketReference.getIds(branchName, regex);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("JIRA-123", result.get(0));
    }

    @Test
    public void testGetIds_MultipleMatches() {
        String branchName = "JIRA-123_feature_JIRA-456_fix";
        String regex = "([A-Z][A-Z\\d_]*)-(\\d+)";

        List<String> result = TicketReference.getIds(branchName, regex);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains("JIRA-123"));
        Assert.assertTrue(result.contains("JIRA-456"));
    }

}
