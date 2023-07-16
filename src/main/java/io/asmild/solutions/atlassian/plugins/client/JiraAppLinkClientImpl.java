package io.asmild.solutions.atlassian.plugins.client;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationLinkNotConfiguredException;
import io.asmild.solutions.atlassian.plugins.exceptions.ApplicationConnectionErrorException;
import io.asmild.solutions.atlassian.plugins.exceptions.ResourceNotFoundException;
import io.asmild.solutions.atlassian.plugins.exceptions.JiraTicketNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JiraAppLinkClientImpl implements JiraAppLinkClient {
    private static final Logger logger = LoggerFactory.getLogger(JiraAppLinkClient.class);
    private final ApplicationLinkService applicationLinkService;
    @Autowired
    public JiraAppLinkClientImpl(
            @ComponentImport ApplicationLinkService applicationLinkService
    ) {
        this.applicationLinkService = applicationLinkService;
    }

    public String getTicketDetails(String ticketId) throws Exception {
         return doRequest(Request.MethodType.GET, "issue/" + ticketId);
    }

    private String doRequest(Request.MethodType method, String endpoint) throws Exception {
        ApplicationLink jiraLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
        if (jiraLink == null) {
            throw new ApplicationLinkNotConfiguredException("Jira application link not found. Please, configure the application link or disable ticket validation.");
        }
        String response;
        ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();

        try {
            response = requestFactory
                    .createRequest(method, "/rest/api/latest/" + endpoint)
                    .execute(new ApplicationLinkResponseHandler<String>() {
                        @Override
                        public String credentialsRequired(Response response) throws ResponseException {
                            // Handle response when credentials are required
                            throw new ResponseException("Credentials are required to access Jira");
                        }

                        @Override
                        public String handle(Response response) throws ResponseException {
                            if (response.getStatusCode() == 404) {
                                throw new ResourceNotFoundException("Resource not found");
                            }

                            if (response.isSuccessful()) {
                                return response.getResponseBodyAsString();
                            } else {
                                throw new ResponseException("Request failed with status code: " + response.getStatusCode());
                            }
                        }
                    });
            return response;

        } catch (ResponseException | CredentialsRequiredException e) {
            if (e instanceof ResourceNotFoundException) {
                throw new JiraTicketNotFoundException("Ticket not found");
            } else if (e instanceof ResponseException || e instanceof CredentialsRequiredException) {
                logger.error(e.getMessage());
                throw new ApplicationConnectionErrorException("Jira connection error occurred");
            } else {
                throw e;
            }
        }
    }
}
