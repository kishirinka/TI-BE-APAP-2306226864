package apap.ti._5.Insurance_2306226864_be.enums;

public enum ClaimStatusEnum {
    WAITING_FOR_REVIEW("Waiting for Review"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String displayName;

    ClaimStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}