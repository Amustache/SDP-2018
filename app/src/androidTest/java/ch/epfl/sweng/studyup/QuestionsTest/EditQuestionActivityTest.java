package ch.epfl.sweng.studyup.QuestionsTest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.RadioButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.sweng.studyup.R;
import ch.epfl.sweng.studyup.firebase.Firestore;
import ch.epfl.sweng.studyup.player.Player;
import ch.epfl.sweng.studyup.questions.AddOrEditQuestionActivity;
import ch.epfl.sweng.studyup.questions.Question;
import ch.epfl.sweng.studyup.questions.QuestionParser;
import ch.epfl.sweng.studyup.utils.Constants;
import ch.epfl.sweng.studyup.utils.Utils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.studyup.utils.Utils.waitAndTag;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static ch.epfl.sweng.studyup.utils.GlobalAccessVariables.MOCK_ENABLED;

@SuppressWarnings("HardCodedStringLiteral")
@RunWith(AndroidJUnit4.class)
public class EditQuestionActivityTest {
    private final String questionUUID = "Temporary fake uuid";
    private Question q;
    private ListView list;
    private String TAG = "EditQuestionActivityTest";

    @Rule
    public final ActivityTestRule<AddOrEditQuestionActivity> mActivityRule =
            new ActivityTestRule<>(AddOrEditQuestionActivity.class);


    @Before
    public void init() {
        MOCK_ENABLED = true;
    }

    @After
    public void disableMock() {
        Player.get().resetPlayer();
        MOCK_ENABLED = false;
    }

    private void editAndCheckQuestion(final int newAnswerId, final int newAnswerNumber, final boolean changeType) throws Throwable {
        mActivityRule.launchActivity(new Intent().putExtra(AddOrEditQuestionActivity.class.getSimpleName(), q));

        // Change Type
        final boolean isTrueFalseBeforeEdition = q.isTrueFalse();
        if(changeType && isTrueFalseBeforeEdition) {
            onView(withId(R.id.mcq_radio)).perform(scrollTo()).perform(click());
        } else if(changeType) {
            onView(withId(R.id.true_false_radio)).perform(scrollTo()).perform(click());
        }

        // Change answer
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                unsetAnswerRadioButtons();
                RadioButton newAnswerButton = mActivityRule.getActivity().findViewById(newAnswerId);
                newAnswerButton.setChecked(true);
            }
        });
        waitAndTag(150, "Wait for radio to be set.");

        // Edit and Check
        onView(withId(R.id.addOrEditQuestionButton)).perform(scrollTo()).perform(click());
        Firestore.get().loadQuestions(mActivityRule.getActivity());
        waitAndTag(1000, "Waiting for questions to load.");
        LiveData<List<Question>> parsedList = QuestionParser.parseQuestionsLiveData(mActivityRule.getActivity().getApplicationContext());
        assertNotNull(parsedList);
        parsedList.observe(mActivityRule.getActivity(), new Observer<List<Question>>() {
            @Override
            public void onChanged(@Nullable List<Question> questions) {
                if (!questions.isEmpty()) {
                    for (Question q : questions) {
                        if (q.getQuestionId().equals(questionUUID)) {
                            assertEquals(newAnswerNumber, q.getAnswer());

                            boolean shouldBeTrueFalse = getExpectedIsTrueFalse(changeType, isTrueFalseBeforeEdition);
                            assertEquals(shouldBeTrueFalse, q.isTrueFalse());
                        }
                    }
                }
            }
        });
    }

    private void unsetAnswerRadioButtons() {
        RadioButton r1 = mActivityRule.getActivity().findViewById(R.id.radio_answer1);
        RadioButton r2 = mActivityRule.getActivity().findViewById(R.id.radio_answer2);
        RadioButton r3 = mActivityRule.getActivity().findViewById(R.id.radio_answer3);
        RadioButton r4 = mActivityRule.getActivity().findViewById(R.id.radio_answer4);

        r1.setChecked(false);
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
    }

    private boolean getExpectedIsTrueFalse(boolean changeType, boolean wasTrueFalse) {
        boolean exp;
        if (changeType && wasTrueFalse) {
            exp = false;
        } else {
            exp = changeType || wasTrueFalse;
        }

        return exp;
    }

    @Test
    public void editTrueFalseQuestionAnswer0to1Test()  throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), true, 0, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer2, 1, false);
    }



    @Test
    public void editTrueFalseQuestionAnswer1to0Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), true, 1, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer1, 0, false);
    }


    @Test
    public void editTrueFalseToMCQAnswer0To3Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), true, 0, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer3, 2, true);
    }

    @Test
    public void editMCQQuestionAnswer0to1Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), false, 0, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer2, 1, false);
    }

    @Test
    public void editMCQQuestionAnswer1to2Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), false, 0, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer2, 1, false);
    }

    @Test
    public void editMCQQuestionAnswer3to2Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), false, 2, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer2, 1, false);
    }

    @Test
    public void editMCQQuestionAnswer4to1Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), false, 3, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer1, 0, false);
    }

    @Test
    public void editMCQToTrueFalseQuestionAnswer4to0Test() throws Throwable {
        q = new Question(questionUUID, this.getClass().getName(), false, 3, Constants.Course.SWENG.name(), "en");
        editAndCheckQuestion(R.id.radio_answer1, 0, true);
    }

    @Test
    public void editTrueFalseQuestionAnswerImagedToTextBasedTest() {
        q = new Question(questionUUID, this.getClass().getName(), true, 0, Constants.Course.SWENG.name(), "en");
        //addQuestionAndClick(q);

        onView(withId(R.id.text_radio_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.questionText))
                .perform(scrollTo())
                .perform(clearText())
                .perform(typeText("Q"))
                .perform(closeSoftKeyboard());

        waitAndTag(2000, this.getClass().getName());

        onView(withId(R.id.radio_answer2)).perform(scrollTo()).perform(click());
        onView(withId(R.id.addOrEditQuestionButton)).perform(scrollTo()).perform(click());
        waitAndTag(1000, this.getClass().getName());
        Firestore.get().loadQuestions(mActivityRule.getActivity());
        waitAndTag(1000, "Waiting for questions to load.");
        checkQuestionIsTrueFalse();
    }
/*
    private void addQuestionAndClick(Question q) {
        Firestore.get().addQuestion(q);
        Utils.waitAndTag(3000, this.getClass().getName());
        Firestore.get().loadQuestions(mActivityRule.getActivity());
        Utils.waitAndTag(3000, this.getClass().getName());
        clickOnListViewItem();
    }
*/
    private void checkQuestionIsTrueFalse() {
        LiveData<List<Question>> parsedList = QuestionParser.parseQuestionsLiveData(mActivityRule.getActivity().getApplicationContext());
        assertNotNull(parsedList);
        parsedList.observe(mActivityRule.getActivity(), new Observer<List<Question>>() {
            @Override
            public void onChanged(@Nullable List<Question> questions) {
                if (!questions.isEmpty()) {
                    for (Question q : questions) {
                        if(q.getQuestionId().equals(questionUUID)) {
                            assertEquals(true, q.isTrueFalse());
                            return;
                        }
                    }
                }
            }
        });
    }

    @Test
    public void backButtonTest() {
        q = new Question(questionUUID, this.getClass().getName(), true, 0, Constants.Course.SWENG.name(), "en");
        Intent iWithSimpleQ = new Intent();
        iWithSimpleQ.putExtra(AddOrEditQuestionActivity.class.getSimpleName(), q);
        mActivityRule.launchActivity(iWithSimpleQ);
        onView(withId(R.id.back_button)).perform(click());
    }
}
