package interfaces;

/**
 * Master contract — common methods shared by ALL platforms (Web, Android, iOS).
 * <p>
 * Step definitions use this type for the {@code platform} field.
 * Common methods are called directly: {@code platform.launchApplication()}
 * Mobile-specific methods are accessed via: {@code platform.asMobile().deepLogin()}
 */
public interface IPlatformInterface extends ILoginPage, IHomePage, ShoppingCart, CommonAction {

    /** Shared mobile methods: platform.asMobile().deepLogin() */
    default IMobilePlatform asMobile() {
        if (this instanceof IMobilePlatform mp) return mp;
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is not a mobile platform");
    }

    /** Android-specific methods: platform.asAndroid().someAndroidMethod() */
    default Android asAndroid() {
        if (this instanceof Android a) return a;
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is not an Android platform");
    }

    /** iOS-specific methods: platform.asIOS().someIOSMethod() */
    default IOS asIOS() {
        if (this instanceof IOS i) return i;
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is not an iOS platform");
    }

    /** Web-specific methods: platform.asWeb().someWebMethod() */
    default Web asWeb() {
        if (this instanceof Web w) return w;
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is not a web platform");
    }
}