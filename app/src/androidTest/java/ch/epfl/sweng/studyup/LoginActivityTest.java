package ch.epfl.sweng.studyup;
import android.provider.MediaStore;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RadioGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import ch.epfl.sweng.studyup.player.Player;
import ch.epfl.sweng.studyup.questions.AddQuestionActivity;
import android.content.Intent;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    // Test handle failed login message
    @Test
    public void testFailedLoginReport() {
        Intent badLoginIntent = new Intent();
        badLoginIntent.putExtra("login_message", "Failed Login");
        rule.launchActivity(badLoginIntent);
    }
    // Test handle no login message
    @Test
    public void testNoReport() {
        rule.launchActivity(new Intent());
    }
    // Test button click redirect
    @Test
    public void testLoginButtonRedirect() {
        rule.launchActivity(new Intent());
        onView(withId(R.id.loginButton)).perform(click());
    }
}