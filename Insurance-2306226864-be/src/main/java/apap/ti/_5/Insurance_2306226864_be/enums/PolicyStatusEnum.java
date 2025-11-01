package apap.ti._5.Insurance_2306226864_be.enums;

public enum PolicyStatusEnum {
    CREATED("Created"),
    PAID("Paid"),
    PARTIALLY_CLAIMED("Partially Claimed"),
    FULLY_CLAIMED("Fully Claimed"),
    EXPIRED("Expired");

    private final String displayName;

    PolicyStatusEnum(String displayName) {
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