package io.asmild.solutions.atlassian.plugins.models;

import com.google.gson.Gson;

public class JiraIssue {
    private String id;
    private String key;
    private Fields fields;

    // Getters and setters

    public JiraIssue(String json) {
        Gson gson = new Gson();
        JiraIssue parsedIssue = gson.fromJson(json, JiraIssue.class);
        this.id = parsedIssue.id;
        this.key = parsedIssue.key;
        this.fields = parsedIssue.fields;
    }
    public String getStatusName() {
        if (fields != null && fields.getStatus() != null) {
            return fields.getStatus().getName();
        }
        return null;
    }

    public static class Fields {
        private Status status;

        public static class Status {
            private String name;
            public String getName() {
                return name;
            }
        }

        public Status getStatus() {
            return status;
        }
    }
}
