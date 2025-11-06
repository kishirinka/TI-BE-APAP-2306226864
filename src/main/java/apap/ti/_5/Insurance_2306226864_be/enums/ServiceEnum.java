package apap.ti._5.Insurance_2306226864_be.enums;

public enum ServiceEnum {
    ACCOMMODATION("Accommodation"),
    FLIGHT("Flight"),
    PACKAGE("Package"),
    RENTALS("Rentals");

    private final String displayName;

    ServiceEnum(String displayName) {
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