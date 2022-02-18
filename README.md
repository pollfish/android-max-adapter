# Pollfish Android Max Mediation Adapter

Max Mediation Adapter for Android apps looking to load and show Rewarded Surveys from Pollfish in the same waterfall with other Rewarded Ads.

> **Note:** A detailed step by step guide is provided on how to integrate can be found [here](https://www.pollfish.com/docs/android-max-adapter)

<br/>

## Step 1: Add Pollfish Max Adapter to your project

Import Pollfish Max adapter **.aar** file as it can be found in the **pollfish-max-aar** folder, to your project libraries

If you are using Android Studio, right click on your project and select New Module. Then select Import .JAR or .AAR Package option and from the file browser locate Pollfish Max Adapter aar file. Right click again on your project and in the Module Dependencies tab choose to add Pollfish module that you recently added, as a dependency.

**OR**

#### **Retrieve Pollfish Max Adapter through mavenCentral()**

Retrieve Pollfish through **mavenCentral()** with gradle by adding the following line in your project **build.gradle** (not the top level one, the one under 'app') in dependencies section:

```groovy
dependencies {
    implementation 'com.pollfish.mediation:pollfish-max:6.2.2.0'
}
```

<br/>

## Step 2: Request for a RewardedAd

<br/>

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
    ...

    AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX

    AppLovinSdk.getInstance(this).initializeSdk {
        createRewardedAd()
    }
}
```

<span style="text-decoration:underline">Java</span>

```java
protected void onCreate(Bundle savedInstanceState) {
    ...

    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);

    AppLovinSdk.getInstance(this).initializeSdk(config -> {
        createRewardedAd();
    });
}
```

<br/>

Implement `MaxRewardedAdListener` so that you are notified when your ad is ready and of other ad-related events.

<br/>

Request a RewardedAd from AppLovin by calling `loadAd()` in the `RewardedAd` object instance you've created.

<br/>

<span style="text-decoration:underline">Kotlin</span>

```kotlin
class MainActivity : AppCompatActivity(), MaxRewardedAdListener {

    private lateinit var rewardedAd: RewardedAd

    ...

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

    ...

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

## Step 3: Use and control Pollfish Max Adapter in your Rewarded Ad Unit

Pollfish Max Adapter provides different options that you can use to control the behaviour of Pollfish SDK. This configuration, if applied, will override any configuration done in AppLovin's dashboard.

<br/>

```kotlin
rewardedAd = MaxRewardedAd.getInstance("25aa38dc0445505f", this)
rewardedAd.setLocalExtraParameter("release_mode", false)
rewardedAd.setLocalExtraParameter("offerwall_mode", true)
rewardedAd.setLocalExtraParameter("api_key", "YOUR_POLLFISH_API_KEY")
rewardedAd.setLocalExtraParameter("request_uuid", "REQUEST_UUID")
```

| No  | Description |
| --- | ----------------------------------- |
| 3.1 | **`api_key`** <br/> Sets Pollfish SDK API key as provided by Pollfish                                                                            |
| 3.2 | **`request_uuid`** <br/> Sets a unique identifier to identify a user and be passed through to [s2s callbacks](https://www.pollfish.com/docs/s2s) |
| 3.3 | **`release_mode`** <br/> Toggles Pollfish SDK Developer or Release mode                                                                          |
| 3.4 | **`offerwallMode`** <br/> Sets Pollfish SDK to Offerwall Mode                                                                                    |

<br/>

### 3.1 `api_key`

Pollfish API Key as provided by Pollfish on [Pollfish Dashboard](https://www.pollfish.com/publisher/) after you sign in and create an app. If you have already specified Pollfish API Key on AppLovin's UI, this param will override the one defined on Web UI.

<br/>

### 3.2 `request_uuid`

Sets a unique id to identify a user and be passed through s2s callbacks upon survey completion.

In order to register for such callbacks you can set up your server URL on your app's page on Pollfish Developer Dashboard. On each survey completion you will receive a callback to your server including the `request_uuid` param passed.

If you would like to read more on Pollfish s2s callbacks you can read the documentation [here](https://www.pollfish.com/docs/s2s)

<br/>

### 3.3 `release_mode`

Sets Pollfish SDK to Developer or Release mode.

- **Developer mode** is used to show to the developer how Pollfish surveys will be shown through an app (useful during development and testing).
- **Release mode** is the mode to be used for a released app in any app store (start receiving paid surveys).

Pollfish Max Adapter runs Pollfish SDK in release mode by default. If you would like to test with Test survey, you should set release mode to fasle.

<br/>

### 3.4 `offerwall_mode`

Enables offerwall mode. If not set, one single survey is shown each time.

<br/>

## Step 4: Publish

If you everything worked fine during the previous steps, you should turn Pollfish to release mode and publish your app.

> **Note:** After you take your app live, you should request your account to get verified through Pollfish Dashboard in the App Settings area.

> **Note:** There is an option to show **Standalone Demographic Questions** needed for Pollfish to target users with surveys even when no actually surveys are available. Those surveys do not deliver any revenue to the publisher (but they can increase fill rate) and therefore if you do not want to show such surveys in the Waterfall you should visit your **App Settings** are and disable that option.
