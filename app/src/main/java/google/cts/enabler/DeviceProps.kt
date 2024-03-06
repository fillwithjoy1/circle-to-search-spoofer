package google.cts.enabler

/**
 * Build values taken from:
 * https://www.pling.com/p/2129780/ (aka PixelProps)
 *
 * Features taken from:
 * https://t.me/PixelProps
 * https://github.com/DotOS/android_vendor_dot/blob/dot12/prebuilt/common/etc/pixel_2016_exclusive.xml
 * https://github.com/ayush5harma/PixelFeatureDrops/tree/master/system/etc/sysconfig
 */
object DeviceProps {

    /**
     * Class to store different feature flags for different pixels.
     * @param displayName String to show to user to customize flag selection. Example "Pixel 2020"
     * Also note that these display names are what is actually stored in shared preferences.
     * The actual feature flags are then derived from the display names.
     * @param featureFlags List of actual features spoofed to Google Photos for that particular [displayName].
     * Example, for [displayName] = "Pixel 2020", [featureFlags] = listOf("com.google.android.feature.PIXEL_2020_EXPERIENCE")
     */
    class Features(
        val displayName: String,
        val featureFlags: List<String>,
    ){
        constructor(displayName: String, vararg featureFlags: String) : this(
            displayName,
            featureFlags.toList()
        )

        constructor(displayName: String, singleFeature: String) : this(
            displayName,
            listOf(singleFeature)
        )
    }

    /**
     * List of all possible feature flags.
     * CHRONOLOGY IS IMPORTANT. Elements are arranged in accordance of device release date.
     */
    val allFeatures = listOf(
        Features("Pixel 2023", // Pixel 8 (Pro)
            "com.google.android.feature.PIXEL_2023_EXPERIENCE",
            "com.google.android.apps.photos.PIXEL_2023_PRELOAD",
        ),
    )

    /**
     * Example if [featureLevel] = "Pixel 2020", return will have
     * list of all elements from [allFeatures] from "Pixel 2016" i.e. index = 0,
     * to "Pixel 2020" i.e index = 6, both inclusive.
     */
    private fun getFeaturesUpTo(featureLevel: String): List<Features> {
        val allFeatureDisplayNames = allFeatures.map { it.displayName }
        val levelIndex = allFeatureDisplayNames.indexOf(featureLevel)
        return if (levelIndex == -1) listOf()
        else {
            allFeatures.withIndex().filter { it.index <= levelIndex }.map { it.value }
        }
    }

    /**
     * Class storing android version information to be faked.
     *
     * @param label Just a string to show the user. Not spoofed.
     * @param release Corresponds to `ro.build.version.release`. Example values: "12", "11", "8.1.0" etc.
     * @param sdk Corresponds to `ro.build.version.sdk`.
     */
    data class AndroidVersion(
        val label: String,
        val release: String,
        val sdk: Int,
    ){
        fun getAsMap() = hashMapOf(
            Pair("RELEASE", release),
            Pair("SDK_INT", sdk),
            Pair("SDK", sdk.toString()),
        )
    }

    /**
     * List of all major android versions.
     * Pixel 1 series launched with nougat, so that is the lowest version.
     */
    val allAndroidVersions = listOf(
        AndroidVersion("Nougat 7.1.2", "7.1.2", 25),
        AndroidVersion("Oreo 8.1.0", "8.1.0", 27),
        AndroidVersion("Pie 9.0", "9", 28),
        AndroidVersion("Q 10.0", "10", 29),
        AndroidVersion("R 11.0", "11", 30),
        AndroidVersion("S 12.0", "12", 31),
        // Haha Android 12L
        AndroidVersion("S 12.1", "12.1", 32),
        AndroidVersion("T 13.0", "13", 33),
        AndroidVersion("U 14.0", "14", 34),
        // Predict Vanilla Ice Cream Props 
        AndroidVersion("V 15.0", "15", 35),
    )

    /**
     * Get instance of [AndroidVersion] from specified [label].
     * Send null if no such label.
     */
    fun getAndroidVersionFromLabel(label: String) = allAndroidVersions.find { it.label == label }

    /**
     * Class to contain device names and their respective build properties.
     * @param deviceName Actual device names, example "Pixel 4a".
     * @param props Contains the device properties to spoof.
     * @param featureLevelName Points to the features expected to be spoofed from [allFeatures],
     * from "Pixel 2016" up to this level.
     */
    data class DeviceEntries(
        val deviceName: String,
        val props: HashMap<String, String>,
        val featureLevelName: String,
        val androidVersion: AndroidVersion?,
    )

    /**
     * List of all devices and their build props and their required feature levels.
     */
    val allDevices = listOf(

        DeviceEntries("None", hashMapOf(), "None", null),
        
        DeviceEntries(
            "Pixel 8 Pro", hashMapOf(
                Pair("BRAND", "google"),
                Pair("MANUFACTURER", "Google"),
                Pair("DEVICE", "husky"),
                Pair("PRODUCT", "husky"),
                Pair("MODEL", "Pixel 8 Pro"),
                Pair("FINGERPRINT", "google/husky/husky:14/UQ1A.240205.004/11269751:user/release-keys"),
            ),
            "Pixel 8 Pro",
            getAndroidVersionFromLabel("U 14.0"),
        ),
    )

    /**
     * Get instance of [DeviceEntries] from a supplied [deviceName].
     */
    fun getDeviceProps(deviceName: String?) = allDevices.find { it.deviceName == deviceName }

    /**
     * Call [getFeaturesUpTo] using a device name rather than feature level.
     * Used in spinner in main activity.
     */
    fun getFeaturesUpToFromDeviceName(deviceName: String?): Set<String>{
        return getDeviceProps(deviceName)?.let {
            getFeaturesUpTo(it.featureLevelName).map { it.displayName }.toSet()
        }?: setOf()
    }

    /**
     * Default name of device to spoof.
     */
    val defaultDeviceName = "Pixel 8 Pro"

    /**
     * Default feature level to spoof up to. Corresponds to what is expected for the device in [defaultDeviceName].
     */
    val defaultFeatures = getFeaturesUpTo("Pixel 2023")

}
