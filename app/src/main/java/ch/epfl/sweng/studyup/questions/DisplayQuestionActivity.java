package ch.epfl.sweng.studyup.questions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.epfl.sweng.studyup.R;
import ch.epfl.sweng.studyup.firebase.FileStorage;
import ch.epfl.sweng.studyup.items.Items;
import ch.epfl.sweng.studyup.player.Player;
import ch.epfl.sweng.studyup.player.QuestsActivityStudent;
import ch.epfl.sweng.studyup.utils.navigation.NavigationStudent;

public class DisplayQuestionActivity extends NavigationStudent {

    private final String TAG = "DisplayQuestionActivity";
    public static final String DISPLAY_QUESTION_TITLE = "display_question_title";
    public static final String DISPLAY_QUESTION_ID = "display_question_id";
    public static final String DISPLAY_QUESTION_TRUE_FALSE = "display_question_true_false";
    public static final String DISPLAY_QUESTION_ANSWER = "display_question_answer";
    public static final int XP_GAINED_WITH_QUESTION = 10;
    private Question displayQuestion;

    private RadioGroup answerGroupTOP;
    private RadioGroup answerGroupBOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);

        int answerNumber = 0;
        boolean trueFalse = false;
        String questionTitle = "";
        String questionID = "";

        Intent intent = getIntent();
        //Get the Uri from the intent
        if (!intent.hasExtra(DISPLAY_QUESTION_TITLE)) {
            quit();
            return;
        } else {
            questionTitle = intent.getStringExtra(DISPLAY_QUESTION_TITLE);
        }

        //Get the question ID
        if (!intent.hasExtra(DISPLAY_QUESTION_ID)) {
            quit();
            return;
        }else {
            questionID = intent.getStringExtra(DISPLAY_QUESTION_ID);
        }

        //Get the answer
        if (!intent.hasExtra(DISPLAY_QUESTION_ANSWER)) {
            quit();
            return;
        } else
            answerNumber = Integer.parseInt(intent.getStringExtra(DISPLAY_QUESTION_ANSWER));

        //Now the boolean isTrueFale
        if (!intent.hasExtra(DISPLAY_QUESTION_TRUE_FALSE)) {
            quit();
            return;
        } else
            trueFalse = Boolean.parseBoolean(intent.getStringExtra(DISPLAY_QUESTION_TRUE_FALSE));

        //Create the question
        displayQuestion = new Question(questionID, questionTitle, trueFalse, answerNumber);
        displayImage(questionID);
        setupLayout(displayQuestion);


        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        answerGroupTOP = findViewById(R.id.answer_radio_group_top);
        answerGroupBOT = findViewById(R.id.answer_radio_group_bot);
        answerGroupTOP.clearCheck();
        answerGroupBOT.clearCheck();
        answerGroupTOP.setOnCheckedChangeListener(listener1);
        answerGroupBOT.setOnCheckedChangeListener(listener2);

        List<RadioButton> radioButtons = new ArrayList<>(Arrays.asList(
                (RadioButton) findViewById(R.id.answer1),
                (RadioButton) findViewById(R.id.answer2),
                (RadioButton) findViewById(R.id.answer3),
                (RadioButton) findViewById(R.id.answer4)));

        for (RadioButton rdb : radioButtons) {
            rdb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isChecked()) {
                        buttonView.setBackgroundResource(R.drawable.button_quests_clicked_shape);
                    }
                    else buttonView.setBackgroundResource(R.drawable.button_quests_shape);
                }
            });
        }

        TextView questTitle = findViewById(R.id.quest_title);
        questTitle.setText(displayQuestion.getTitle());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    /**
     * Listeners that allows us to have two columns of radio buttons, without two buttons checkable
     * */
    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                answerGroupBOT.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                answerGroupBOT.clearCheck(); // clear the second RadioGroup!
                answerGroupBOT.setOnCheckedChangeListener(listener2); //reset the listener
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                answerGroupTOP.setOnCheckedChangeListener(null);
                answerGroupTOP.clearCheck();
                answerGroupTOP.setOnCheckedChangeListener(listener1);
            }
        }
    };


    private void displayImage(String questionID){
        StorageReference questionImage = FileStorage.getProblemImageRef(Uri.parse(questionID + ".png"));
        try {
            final File tempImage = File.createTempFile(questionID, "png");
            questionImage.getFile(tempImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    ProgressBar progressBar = findViewById(R.id.questionProgressBar);
                    progressBar.setVisibility(View.GONE);
                    Bitmap displayImage = BitmapFactory.decodeFile(tempImage.getAbsolutePath());
                    ImageView displayImageView = findViewById(R.id.question_display_view);
                    displayImageView.setImageBitmap(displayImage);
                }
            });
        }catch(IOException e){
            Toast.makeText(this, "An error occured when downloading the question", Toast.LENGTH_SHORT).show();
            quit();
        }
    }

    private void quit() {
        Toast.makeText(this, "Error while displaying the question", Toast.LENGTH_SHORT);
        Log.e(TAG, "Bad intent given in parameters");
        super.onBackPressed();
    }

    private void setupLayout(Question question){
        if (!question.isTrueFalse()){
            TextView answer1 = findViewById(R.id.answer1);
            answer1.setText(getString(R.string.text_answer_1));

            TextView answer2 = findViewById(R.id.answer2);
            answer2.setText(getString(R.string.text_answer_2));

            TextView answer3 = findViewById(R.id.answer3);
            answer3.setVisibility(View.VISIBLE);

            TextView answer4 = findViewById(R.id.answer4);
            answer4.setVisibility(View.VISIBLE);
        }
    }


    public void answerQuestion(View view) {
        int chkTOP = answerGroupTOP.getCheckedRadioButtonId();
        int chkBOT = answerGroupBOT.getCheckedRadioButtonId();
        if(chkBOT == -1 && chkTOP==-1) {
            Toast.makeText(this, "Make your choice !", Toast.LENGTH_SHORT).show();
        }
        else {
            int realCheck = (chkTOP == -1) ? chkBOT : chkTOP;
            RadioButton checkedAnswer = findViewById(realCheck);

            //subtract 1 to have answer between 0 and 3
            int answer = Integer.parseInt(checkedAnswer.getTag().toString()) - 1;

            //TODO : What to do next ?
            if(Player.get().getAnsweredQuestion().containsKey(displayQuestion.getQuestionId())) {
                Toast.makeText(this, "You can't answer a question twice !", Toast.LENGTH_SHORT).show();
            }

            else if (answer == displayQuestion.getAnswer()) {
                Player.get().addAnsweredQuestion(displayQuestion.getQuestionId(), true);
                Toast.makeText(this, "Correct answer ! Congrats", Toast.LENGTH_SHORT).show();
                Player.get().addExperience(XP_GAINED_WITH_QUESTION, this);

                //Randomly add one item to the player
                Random random = new Random();
                boolean rng = random.nextBoolean();
                if(rng){
                    Player.get().addItem(Items.XP_POTION);
                }else{
                    Player.get().addItem(Items.COIN_SACK);
                }
            } else {
                Player.get().addAnsweredQuestion(displayQuestion.getQuestionId(), false);
                Toast.makeText(this, "Wrong answer... Maybe next time ?", Toast.LENGTH_SHORT).show();
            }

            Intent goToQuests = new Intent(this, QuestsActivityStudent.class);
            startActivity(goToQuests);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    /**
     * @param c The context of the application that launch the intent (put this)
     * @param q The question that needs to be passed
     * @return The intent ready to be launched with "startActivity"
     */
    public static Intent getIntentForDisplayQuestion(Context c, Question q) {
        Intent goToQuestion = new Intent(c, DisplayQuestionActivity.class);
        goToQuestion.putExtra(DISPLAY_QUESTION_TITLE, q.getTitle());
        goToQuestion.putExtra(DISPLAY_QUESTION_ID, q.getQuestionId());
        goToQuestion.putExtra(DISPLAY_QUESTION_TRUE_FALSE, Boolean.toString(q.isTrueFalse()));
        goToQuestion.putExtra(DISPLAY_QUESTION_ANSWER, Integer.toString(q.getAnswer()));
        return goToQuestion;
    }

    //Display the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.top_navigation, menu);
        return true;
    }

    // Allows you to do an action with the toolbar (in a different way than with the navigation bar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        navigationTopToolbar(item);
        return super.onOptionsItemSelected(item);
    }
}
