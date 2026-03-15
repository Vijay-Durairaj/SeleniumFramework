package helper;

public enum Platforms {

    ANDROID("android"),
    IOS("ios"),
    WEB("web"),
    UNKNOWN("unknown");

    final String name;

    Platforms(String name) {
        this.name = name;
    }

    public static Platforms getPlatform(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Platforms.UNKNOWN;
        }

        String normalized = name.trim().toLowerCase();
        for (Platforms platform : Platforms.values()) {
            if (platform.name.equals(normalized)) {
                return platform;
            }
        }

        return Platforms.UNKNOWN;
    }
}
