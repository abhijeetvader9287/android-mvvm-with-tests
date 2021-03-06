package apidez.com.android_mvvm_sample.view.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import apidez.com.android_mvvm_sample.ComponentBuilder;
import apidez.com.android_mvvm_sample.R;
import apidez.com.android_mvvm_sample.model.api.IPurchaseApi;
import apidez.com.android_mvvm_sample.dependency.component.AppComponent;
import apidez.com.android_mvvm_sample.dependency.component.PurchaseComponent;
import apidez.com.android_mvvm_sample.dependency.module.PurchaseModule;
import apidez.com.android_mvvm_sample.stub.StubPurchaseViewModel;
import apidez.com.android_mvvm_sample.utils.ApplicationUtils;
import apidez.com.android_mvvm_sample.utils.UiUtils;
import apidez.com.android_mvvm_sample.viewmodel.IPurchaseViewModel;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static apidez.com.android_mvvm_sample.utils.MatcherEx.hasListener;
import static apidez.com.android_mvvm_sample.utils.MatcherEx.hasResId;
import static apidez.com.android_mvvm_sample.utils.MatcherEx.isVisible;
import static apidez.com.android_mvvm_sample.utils.MatcherEx.waitText;
import static org.hamcrest.Matchers.not;

/**
 * Created by nongdenchet on 10/2/15.
 */

/**
 * Test the UI implementation
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class PurchaseActivityTest {

    @Rule
    public ActivityTestRule<PurchaseActivity> activityTestRule =
            new ActivityTestRule<>(PurchaseActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        PurchaseModule stubModule = new PurchaseModule() {
            @Override
            public IPurchaseViewModel providePurchaseViewModel(IPurchaseApi purchaseApi) {
                return new StubPurchaseViewModel();
            }
        };

        // Setup test component
        AppComponent component = ApplicationUtils.application().component();
        ApplicationUtils.application().setComponentBuilder(new ComponentBuilder(component) {
            @Override
            public PurchaseComponent purchaseComponent() {
                return component.plus(stubModule);
            }
        });

        // Run the activity
        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void noErrorAtTheBeginning() throws Exception {
        onView(withText(R.string.error_credit_card)).check(doesNotExist());
        onView(withText(R.string.error_email)).check(doesNotExist());
    }

    @Test
    public void hasNoErrorCreditCard() throws Exception {
        onView((withId(R.id.creditCard))).perform(typeText("I am"));
        Thread.sleep(1000);
        onView(withText(R.string.error_credit_card)).check(matches(not(isVisible())));
    }

    @Test
    public void hasErrorCreditCard() throws Exception {
        onView(withId(R.id.creditCard)).perform(typeText("age"));
        onView(withText(R.string.error_credit_card)).check(matches(isDisplayed()));
        onView(withText(R.string.error_credit_card)).check(matches(isVisible()));
    }

    @Test
    public void hasNoErrorEmail() throws Exception {
        onView(withId(R.id.email)).perform(typeText("I am"));
        Thread.sleep(1000);
        onView(withText(R.string.error_email)).check(matches(not(isVisible())));
    }

    @Test
    public void hasErrorEmail() throws Exception {
        onView(withId(R.id.email)).perform(typeText("age"));
        onView(withText(R.string.error_email)).check(matches(isDisplayed()));
        onView(withText(R.string.error_email)).check(matches(isVisible()));
    }

    @Test
    public void cannotSubmit() throws Exception {
        onView(withId(R.id.creditCard)).perform(typeText("123"));
        onView(withId(R.id.btnSubmit)).check(matches(not(hasListener())));
        onView(withId(R.id.btnSubmit)).check(matches(hasResId(R.drawable.bg_inactive_submit)));
    }

    @Test
    public void canSubmit() throws Exception {
        onView(withId(R.id.creditCard)).perform(typeText("abcd"));
        onView(withId(R.id.btnSubmit)).check(matches(hasListener()));
        onView(withId(R.id.btnSubmit)).check(matches(hasResId(R.drawable.bg_submit)));
    }

    @Test
    public void submitSuccess() throws Exception {
        onView(withId(R.id.email)).perform(typeText("abcd"));
        onView(withId(R.id.creditCard)).perform(typeText("abcd"));
        UiUtils.closeKeyboard(activityTestRule.getActivity());
        onView(withId(R.id.btnSubmit)).perform(click());
        waitText("Success", 3000);
    }

    @Test
    public void submitFail() throws Exception {
        onView(withId(R.id.creditCard)).perform(typeText("abcd"));
        UiUtils.closeKeyboard(activityTestRule.getActivity());
        onView(withId(R.id.btnSubmit)).perform(click());
        waitText("Error", 3000);
    }
}