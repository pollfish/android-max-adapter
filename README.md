# Prodege Android Max Mediation Adapter

Max Mediation Adapter for Android apps looking to load and show Rewarded Ads from Prodege in the same waterfall with other Rewarded Ads.

> **Note:** A detailed step by step guide is provided on how to integrate can be found [here](https://www.pollfish.com/docs/android-max-adapter)

<br/>

## Add Prodege Max Adapter to your project

Retrieve Prodege Max Adapter through **maven()** with gradle by adding the following line in your app's module **build.gradle** file:

```groovy
dependencies {
    implementation 'com.prodege.mediation:prodege-max:7.0.0-beta01.0'
}
```

<br/>

## Request for a RewardedAd

Import the following packages

<span style="text-decoration:underline">Kotlin</span>

```kotlin
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
```

<span style="text-decoration:underline">Java</span>

```java
import com.applovin.mediation.*;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
```

<br/>

Initialize AppLovin SDK by calling the `initializeSdk()` method, passing that method an `Activity` context. Do this as soon as possible after your app starts, for example in the `onCreate()` method of your launch Activity.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // ...

    AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX

    AppLovinSdk.getInstance(this).initializeSdk {
        createRewardedAd()
    }
}
```

<span style="text-decoration:underline">Java</span>

```java
protected void onCreate(Bundle savedInstanceState) {
    // ...

    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);

    AppLovinSdk.getInstance(this).initializeSdk(config -> {
        createRewardedAd();
    });
}
```

<br/>

Implement `MaxRewardedAdListener` so that you are notified when your ad is ready and of other ad-related events.

Request a RewardedAd from AppLovin by calling `loadAd()` in the `RewardedAd` object instance you've created. By default Prodege Max Adapter will use the configuration as provided on AppLovin's dashboard (section 2). If no configuration is provided or if you want to override any of those params please see section 6.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
class MainActivity : AppCompatActivity(), MaxRewardedAdListener {

    private lateinit var rewardedAd: RewardedAd

    // ...

    private fun createRewardedAd() {
        rewardedAd = MaxRewardedAd.getInstance("AD_UNIT_ID", this);
        rewardedAd.setListener(this);
        loadRewardedAd();
    }

    private fun loadRewardedAd() {
        rewardedAd.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {}

    override fun onAdDisplayed(ad: MaxAd?) {}

    override fun onAdHidden(ad: MaxAd?) {}

    override fun onAdClicked(ad: MaxAd?) {}

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {}

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}

    override fun onRewardedVideoStarted(ad: MaxAd?) {}

    override fun onRewardedVideoCompleted(ad: MaxAd?) {}

    override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {}

}
```

<span style="text-decoration:underline">Java</span>

```java
class MainActivity extends AppCompatActivity implements MaxRewardedAdListener {

    private RewardedAd rewardedAd;

    // ...

    private void createRewardedAd() {
        rewardedAd = MaxRewardedAd.getInstance("AD_UNIT_ID", this);
        rewardedAd.setListener(this);
        loadRewardedAd();
    }

    private void loadRewardedAd() {
        rewardedAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {}

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {}

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {}

    @Override
    public void onAdDisplayed(MaxAd ad) {}

    @Override
    public void onAdHidden(MaxAd ad) {}

    @Override
    public void onAdClicked(MaxAd ad) {}

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {}

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {}
}
```

When the Rewarded Ad is ready, present the ad by invoking `rewardedAd.showAd()`. Just to be sure, you can combine show with a check to see if the Ad you are about to show is actualy ready.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
if (rewardedAd.isReady) {
    rewardedAd.showAd()
}
```

<span style="text-decoration:underline">Java</span>

```java
if (rewardedAd.isReady()) {
    rewardedAd.showAd();
}
```

<br/>

## Configure the Prodege SDK programmatically

Prodege Max Adapter provides different options that you can use to control the behaviour of Prodege SDK. This configuration, if applied, will override any configuration done in AppLovin's dashboard.

<br/>

### **6.1. Initialization configuration**

Below you can see all the availbale options for configuring Prodege SDK prior to the AppLovin SDK initialization.

<br/>

### 6.1.1. `userIdentifier`

An optional id used to identify a user.

Setting the AppLovin's `userIdentifier` will override the default behaviour and use that instead of the Advertising Id in order to identify a user.

> **Note:** <span style="color: red">You can pass the id of a user as identified on your system. Prodege will use this id to identify the user across sessions instead of an ad id/idfa as advised by the stores. You are solely responsible for aligning with store regulations by providing this id and getting relevant consent by the user when necessary. Prodege takes no responsibility for the usage of this id. In any request from your users on resetting/deleting this id and/or profile created, you should be solely liable for those requests.</span>

<span style="text-decoration:underline">Kotlin</span>

```kotlin
AppLovinSdk.getInstance(this).userIdentifier = "MY_USER_ID"
```

<span style="text-decoration:underline">Java</span>

```java
AppLovinSdk.getInstance(this).setUserIdentifier("MY_USER_ID");
```

<br/>

### 6.1.2. `testMode`

Toggles the Prodege SDK Test mode.

- **`true`** is used to show to the developer how Prodege ads will be shown through an app (useful during development and testing).
- **`false`** is the mode to be used for a released app in any app store (start receiving paid surveys).

If you have already specified the preferred mute state on AppLovin's UI, this will override the one defined on Web UI.

Prodege Max Adapter respects the AppLovin's SDK test mode state by default. If you would like to test Prodege ads, while AppLovin SDK is in live mode.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
AppLovinSdk.getInstance(this).settings.setExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_TEST_MODE,
    "true"
)
```

<span style="text-decoration:underline">Java</span>

```java
AppLovinSdk.getInstance(this).getSettings().setExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_TEST_MODE, 
    "true"
);
```

<br/>

### 6.1.3. `muted`

You can set globally the video mute state for both AppLovin and Prodege.

If you have already specified the preferred mute state on AppLovin's UI, this will override the one defined on Web UI.

Prodege Max Adapter respects the AppLovin's SDK mute state by default.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
AppLovinSdk.getInstance(this).settings.isMuted = true
```

<span style="text-decoration:underline">Java</span>

```java
AppLovinSdk.getInstance(this).getSettings().setMuted(true);
```

<br/>

After configuring the Prodege Max Adapter you can proceed with the AppLovin SDK initialization.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
AppLovinSdk.getInstance(this).initializeSdk {
    // ...
}
```

<span style="text-decoration:underline">Java</span>

```java
AppLovinSdk.getInstance(this).initializeSdk(config -> {
    // ...
});
```

<br/>

### **6.2. Rewarded Ad configuration**

Below you can see all the supported options for configuring your `MaxRewardedAd` intance.

Start by gettings a `MaxRewardedAd` by calling:.

<span style="text-decoration:underline">Kotlin</span>

``` kotlin
val rewardedAd = MaxRewardedAd.getInstance(...);
```

<span style="text-decoration:underline">Java</span>

```java
MaxRewardedAd rewardedAd = MaxRewardedAd.getInstance(...);
```

<br/>

### 6.2.1. `placementId`

Your ad unit's placement id as provided by [Publisher Dashboard](https://www.pollfish.com/publisher/).

If you have already specified a placement id on AppLovin's UI, this param will override the one defined on Web UI.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_PLACEMENT_ID,
    "PLACEMENT_ID"
)
```

<span style="text-decoration:underline">Java</span>

```java
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_PLACEMENT_ID, 
    "PLACEMENT_ID"
);
```

<br/>

### 6.2.2. `requestUuid`

Sets a pass-through param to be received via the server-to-server callbacks

In order to register for such callbacks you can set up your server URL on your app's page on the [Publisher Dashboard](https://www.pollfish.com/publisher/). On each survey completion you will receive a callback to your server including the `request_uuid` param passed.

If you would like to read more on Prodege s2s callbacks you can read the documentation [here](https://www.pollfish.com/docs/s2s)

If you have already specified a placement id on AppLovin's UI, this will override the one defined on Web UI.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID,
    "REQUEST_UUID"
)
```

<span style="text-decoration:underline">Java</span>

```java
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID,
    "REQUEST_UUID"
)
```

<br/>

### 6.2.3 `muted`

Sets Prodege video ads mute state.

If you have already specified the preferred mute state on AppLovin's UI or by setting the global AppLovin SDK mute state, this will override any of the previous configurations.

Prodege Max Adapter respects the AppLovin's SDK mute state by default. If you would like to toggle the mute state only for Prodege ads:

<span style="text-decoration:underline">Kotlin</span>

```kotlin
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_MUTED, 
    false
)
```

<span style="text-decoration:underline">Java</span>

```java
rewardedAd.setLocalExtraParameter(
    ProdegeMediationAdapter.LOCAL_EXTRA_REQUEST_UUID,
    "REQUEST_UUID"
)
```

<br/>

Finally, after configuring your rewarded ad.

<span style="text-decoration:underline">Kotlin</span>

```kotlin
rewardedAd.loadAd()
```

<span style="text-decoration:underline">Java</span>

```java
rewardedAd.loadAd();
```

<br/>

## Proguard

If you use proguard with your app, please insert the following lines in your proguard configuration file:

```java
-dontwarn com.prodege.**
-keep class com.prodege.** { *; }
```

<br/>

## Publish

If everything worked fine during the previous steps, you are ready to proceed with publishing your app.

> **Note:** After you take your app live, you should request your account to get verified through the [Publisher Dashboard](https://www.pollfish.com/publisher/) in the App Settings area.

> **Note:** There is an option to show **Standalone Demographic Questions** needed for Prodege to target users with surveys even when no actually surveys are available. Those surveys do not deliver any revenue to the publisher (but they can increase fill rate) and therefore if you do not want to show such surveys in the Waterfall you should visit your **App Settings** are and disable that option. You can read more [here](https://www.pollfish.com/docs/demographic-surveys).

<br/>

# More info

You can read more info on how the Prodege SDKs work or how to get started with AppLovin's Max at the following links:

[Prodege Android SDK](https://pollfish.com/docs/android)

[AppLovin Max Android SDK](https://dash.applovin.com/documentation/mediation/android/getting-started/integration)
