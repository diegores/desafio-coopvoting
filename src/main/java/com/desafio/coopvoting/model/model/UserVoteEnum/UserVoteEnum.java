package com.desafio.coopvoting.model.model.UserVoteEnum;

public enum UserVoteEnum {
    VALID("ABLE_TO_VOTE"),
    INVALID("(UNABLE_TO_VOTE)");

    private final String description;

    UserVoteEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserVoteEnum fromDescription(String description) {
        for (UserVoteEnum value : values()) {
            if (value.getDescription().equalsIgnoreCase(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown UserVoteEnum description: " + description);
    }
}