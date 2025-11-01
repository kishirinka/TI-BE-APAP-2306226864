package apap.ti._5.Insurance_2306226864_be.enums;

public enum OrderedPlanStatusEnum {
    ORDERED("Ordered"),
    PAID("Paid"),
    WAITING_FOR_REVIEW("Waiting for Review"),
    CLAIMED("Claimed"),
    REJECTED("Rejected"),
    EXPIRED("Expired");

    private final String displayName;

    OrderedPlanStatusEnum(String displayName) {
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