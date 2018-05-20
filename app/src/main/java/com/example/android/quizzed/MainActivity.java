package com.example.android.quizzed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ResourceCursorAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.spec.InvalidParameterSpecException;

public class MainActivity extends AppCompatActivity {

    //Strings that save the activity data if the screen rotates
    private final static String CURRENT_SCREEN_KEY = "current_screen_key";
    private final static String QUIZ_SCORE_KEY = "quiz_score_key";
    private final static String USER_TEXT_KEY = "user_text_key";
    private final static String EDIT_TEXT_VIEW_KEY = "edit_text_view_key";
    private final static String QUESTION_1A_KEY = "question_1a_key";
    private final static String QUESTION_1B_KEY = "question_1b_key";
    private final static String QUESTION_1C_KEY = "question_1c_key";
    private final static String QUESTION_1D_KEY = "question_1d_key";
    private final static String QUESTION_2A_KEY = "question_2a_key";
    private final static String QUESTION_2B_KEY = "question_2b_key";
    private final static String QUESTION_2C_KEY = "question_2c_key";
    private final static String QUESTION_2D_KEY = "question_2d_key";
    private final static String QUESTION_3A_KEY = "question_3a_key";
    private final static String QUESTION_3B_KEY = "question_3b_key";
    private final static String QUIZ_SCORE_VIEW_KEY = "quiz_score_view_key";
    private final static String QUIZ_SCORE_COMMENTS_VIEW_KEY = "quiz_score_comments_view_key";
    private final static String BOY_IMAGE_ID_KEY = "boy_image_id_key";
    private final static String QUESTION_GRAPHIC_ID_KEY = "question_graphic_id_key";
    private final static String BACKGROUND_IMAGE_ID_KEY = "background_image_id_key";

    //Root Layout and other layout views which will be visible only when the related question is asked
    RelativeLayout relativeLayout;
    ScrollView question1Layout, question2Layout, question3Layout, question4Layout, quizScoreLayout;
    LinearLayout quizIntroLayout;

    //ImageViews from activity layout that will need to be changed programmatically
    ImageView boyImageView, teacherImageView, questionGraphicView;

    //TextViews from activity layout that will need to be changed programmatically
    TextView quizScoreView, quizResultsComments;

    //RadioButton views which keep track of answer recorded
    RadioButton radioButton1A, radioButton1B, radioButton1C, radioButton1D, radioButton3A, radioButton3B;

    //Checkbox views keep track of answer recorded
    CheckBox checkBox2A, checkBox2B, checkBox2C, checkBox2D;

    //boolean variables will keep track of radio button states
    boolean question1a, question1b, question1c, question1d, question2a, question2b, question2c, question2d, question3a, question3b;

    //int variable will keep track of the screen the user is viewing
    int currentScreen = 0;

    //int variable will keep track of the quiz score
    int quizScore = 0;

    //EditTextView in Question 4; String references fill in the blank response from user
    EditText q4EditTextView;
    String answerFourText = "";

    //This variable will keep track of activity layout orientation
    boolean isLandscapeMode;

    //The variable will store information pertaining to the drawables that are used in the ImageViews in Portrait Mode
    private int boyImageID = R.drawable.boysmiling;
    private int questionGraphicID = R.drawable.man_running;
    private int backgroundImageID = R.drawable.app_5bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find ScrollView, LinearLayout, and RelativeLayout using IDs assigned in the activity layout XML
        relativeLayout = findViewById(R.id.root_view);
        quizIntroLayout = findViewById(R.id.intro_page_layout);
        question1Layout = findViewById(R.id.question_1);
        question2Layout = findViewById(R.id.question_2);
        question3Layout = findViewById(R.id.question_3);
        question4Layout = findViewById(R.id.question_4);
        quizScoreLayout = findViewById(R.id.quiz_score);

        //Find RadioButton and CheckBox buttons using IDs assigned in the activity layout XML
        radioButton1A = findViewById(R.id.question_1_a);
        radioButton1B = findViewById(R.id.question_1_b);
        radioButton1C = findViewById(R.id.question_1_c);
        radioButton1D = findViewById(R.id.question_1_d);
        radioButton3A = findViewById(R.id.shape_A_radio);
        radioButton3B = findViewById(R.id.shape_B_radio);
        checkBox2A = findViewById(R.id.question_2_a);
        checkBox2B = findViewById(R.id.question_2_b);
        checkBox2C = findViewById(R.id.question_2_c);
        checkBox2D = findViewById(R.id.question_2_d);

        //Find TextView views using IDs assigned in the activity layout XML
        q4EditTextView = findViewById(R.id.ques_4_edit_text_view);
        quizScoreView = findViewById(R.id.score_text_view);
        quizResultsComments = findViewById(R.id.quiz_results_comment);

        //Find ImageView views using IDs assigned in the activity layout XML
        boyImageView = findViewById(R.id.boy_results_screen);
        teacherImageView = findViewById(R.id.teacher_left_side);

        //Check if activity is running in landscape mode, if so, do not run the next line(s) of code
        Resources res = getResources();
        isLandscapeMode = res.getBoolean(R.bool.is_landscape);
        if (!isLandscapeMode) {
            questionGraphicView = findViewById(R.id.question_graphic_pic);
        }

        //Calls method to hide all views
        hideAllViews();

        //Starting a new session, select the correct view to display
        selectViewLayout(0);

        // Shows a "New Session" message on the screen if no information was saved
        Toast.makeText(MainActivity.this, getText(R.string.toast_msg_new_game), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        //Saves the current screen information so that quiz is resumed on the correct question
        savedInstanceState.putInt(CURRENT_SCREEN_KEY, currentScreen);

        //Saves quiz score
        savedInstanceState.putInt(QUIZ_SCORE_KEY, quizScore);

        //Saves user's typed in response for Question 4
        savedInstanceState.putString(USER_TEXT_KEY, answerFourText);
        savedInstanceState.putString(EDIT_TEXT_VIEW_KEY, q4EditTextView.getText().toString());

        //Saves Quiz Score and Results Comments, Results Image, Retake quiz info text
        savedInstanceState.putString(QUIZ_SCORE_VIEW_KEY, quizScoreView.getText().toString());
        savedInstanceState.putString(QUIZ_SCORE_COMMENTS_VIEW_KEY, quizResultsComments.getText().toString());
        savedInstanceState.putInt(BOY_IMAGE_ID_KEY, boyImageID);

        //Saves current background image, and any graphics related to the questions
        savedInstanceState.putInt(BACKGROUND_IMAGE_ID_KEY, backgroundImageID);
        savedInstanceState.putInt(QUESTION_GRAPHIC_ID_KEY, questionGraphicID);

        //Saves responses to quiz (booleans will be used to restore CheckBox view and RadioButton states)
        savedInstanceState.putBoolean(QUESTION_1A_KEY, question1a);
        savedInstanceState.putBoolean(QUESTION_1B_KEY, question1b);
        savedInstanceState.putBoolean(QUESTION_1C_KEY, question1c);
        savedInstanceState.putBoolean(QUESTION_1D_KEY, question1d);
        savedInstanceState.putBoolean(QUESTION_2A_KEY, question2a);
        savedInstanceState.putBoolean(QUESTION_2B_KEY, question2b);
        savedInstanceState.putBoolean(QUESTION_2C_KEY, question2c);
        savedInstanceState.putBoolean(QUESTION_2D_KEY, question2d);
        savedInstanceState.putBoolean(QUESTION_3A_KEY, question3a);
        savedInstanceState.putBoolean(QUESTION_3B_KEY, question3b);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Toast Message Stating that the Session has been retrieved
        Toast.makeText(MainActivity.this, getText(R.string.toast_msg_saved_game), Toast.LENGTH_SHORT).show();

        //Restore background images
        backgroundImageID = savedInstanceState.getInt(BACKGROUND_IMAGE_ID_KEY);
        relativeLayout.setBackgroundResource(backgroundImageID);

        //Restore Views, answers, button states, etc
        currentScreen = savedInstanceState.getInt(CURRENT_SCREEN_KEY);

        if (currentScreen != 0 && currentScreen != 5) {

            //Restore user entered text
            //check if EditText view was empty, if it is, set answer to empty string
            // (default state of answerFourText)
            String userText = savedInstanceState.getString(EDIT_TEXT_VIEW_KEY);
            answerFourText = savedInstanceState.getString(USER_TEXT_KEY);

            if (TextUtils.isEmpty(userText)) {

                q4EditTextView.setText(answerFourText);
            } else {
                q4EditTextView.setText(userText);
                answerFourText = userText;
            }

            //Run the next 2 lines only if in Portrait mode
            if (!isLandscapeMode) {
                questionGraphicID = savedInstanceState.getInt(QUESTION_GRAPHIC_ID_KEY);
                questionGraphicView.setImageResource(questionGraphicID);
            }

            //Retrieve RadioButton and CheckBox states
            question1a = savedInstanceState.getBoolean(QUESTION_1A_KEY);
            question1b = savedInstanceState.getBoolean(QUESTION_1B_KEY);
            question1c = savedInstanceState.getBoolean(QUESTION_1C_KEY);
            question1d = savedInstanceState.getBoolean(QUESTION_1D_KEY);
            question2a = savedInstanceState.getBoolean(QUESTION_2A_KEY);
            question2b = savedInstanceState.getBoolean(QUESTION_2B_KEY);
            question2c = savedInstanceState.getBoolean(QUESTION_2C_KEY);
            question2d = savedInstanceState.getBoolean(QUESTION_2D_KEY);
            question3a = savedInstanceState.getBoolean(QUESTION_3A_KEY);
            question3b = savedInstanceState.getBoolean(QUESTION_3B_KEY);

            //After retrieving button states, invoke method to restore the states
            restoreButtonStates();

        } else if (currentScreen == 5) {
            quizScore = savedInstanceState.getInt(QUIZ_SCORE_KEY);

            String savedQuizScoreTextViewText = savedInstanceState.getString(QUIZ_SCORE_VIEW_KEY);
            quizScoreView.setText(savedQuizScoreTextViewText);

            String savedQuizScoreCommentsViewText = savedInstanceState.getString(QUIZ_SCORE_COMMENTS_VIEW_KEY);
            quizResultsComments.setText(savedQuizScoreCommentsViewText);

            //Restore image resources
            boyImageID = savedInstanceState.getInt(BOY_IMAGE_ID_KEY);
            boyImageView.setImageResource(boyImageID);

        } else {
            resetAnswers();
        }

        //Reveal the saved layout
        selectViewLayout(currentScreen);
    }

    /**
     * This method selects which layout view to show based on the currentScreen variable
     *
     * @param page is based on the currentScreen variable, which determines which layout view "page" to show
     *             Example: Page 0 is the intro Page, Page 1 is Question 1, Page 2 is Question 1, Page 3 is Question 3,
     *             Page 4 is Question 4, Page 5 is the Quiz Score page
     */
    public void selectViewLayout(int page) {
        //Hide all view before selecting the new view
        hideAllViews();

        //Select which view to show based on parameter
        if (page == 0) {
            //Set Root View Background Image, set visibility of views
            quizIntroLayout.setVisibility(View.VISIBLE);
            backgroundImageID = R.drawable.app_5bg;
            relativeLayout.setBackgroundResource(backgroundImageID);

        } else if (page == 1) {
            //Set Root View Background Image, set visibility of views
            backgroundImageID = R.drawable.app_5bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
            questionGraphicID = R.drawable.man_running;

            //Run the next line(s) only if in Portrait mode
            //Set the image resource for an ImageView
            if (!isLandscapeMode) {
                questionGraphicView.setImageResource(questionGraphicID);
                questionGraphicView.setVisibility(View.VISIBLE);
            }

            //If the user is on this page, the quizScore needs to be reset to 0
            quizScore = 0;

            question1Layout.setVisibility(View.VISIBLE);

        } else if (page == 2) {
            //Set Root View Background Image, set visibility of views
            backgroundImageID = R.drawable.app_1bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
            question2Layout.setVisibility(View.VISIBLE);
            questionGraphicID = R.drawable.mountains;

            //Run the next line(s) only if in Portrait mode
            //Set the image resource for an ImageView
            if (!isLandscapeMode) {
                questionGraphicView.setImageResource(questionGraphicID);
                questionGraphicView.setVisibility(View.VISIBLE);
            }

        } else if (page == 3) {
            //Set Root View Background Image, set visibility of views
            backgroundImageID = R.drawable.app_6bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
            question3Layout.setVisibility(View.VISIBLE);
            teacherImageView.setImageResource(R.drawable.teacher3);
            teacherImageView.setVisibility(View.VISIBLE);

        } else if (page == 4) {
            //Set Root View Background Image, set visibility of views
            backgroundImageID = R.drawable.app_5bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
            questionGraphicID = R.drawable.politician;

            //Run the next line(s) only if in Portrait mode
            //Set the image resource for an ImageView
            if (!isLandscapeMode) {
                questionGraphicView.setImageResource(questionGraphicID);
                questionGraphicView.setVisibility(View.VISIBLE);
            }
            question4Layout.setVisibility(View.VISIBLE);

        } else if (page == 5) {
            //Set Root View Background Image, set visibility of views
            backgroundImageID = R.drawable.app_5bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
            quizScoreLayout.setVisibility(View.VISIBLE);
            boyImageView.setVisibility(View.VISIBLE);
        } else {
            backgroundImageID = R.drawable.app_4bg;
            relativeLayout.setBackgroundResource(backgroundImageID);
        }
    }

    /*This method restores the RadioButton and CheckBox button states*/
    public void restoreButtonStates() {

        //Restore RadioButton states (Question 1 and 3)
        if (question1a) {
            radioButton1A.setChecked(true);
            radioButton1A.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question1b) {
            radioButton1B.setChecked(true);
            radioButton1B.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question1c) {
            radioButton1C.setChecked(true);
            radioButton1C.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question1d) {
            radioButton1D.setChecked(true);
            radioButton1D.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question3a) {
            radioButton3A.setChecked(true);
            radioButton3A.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question3b) {
            radioButton3B.setChecked(true);
            radioButton3B.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        //Restore CheckBox States (Question 2)
        if (question2a) {
            checkBox2A.setChecked(true);
            checkBox2A.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question2b) {
            checkBox2B.setChecked(true);
            checkBox2B.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question2c) {
            checkBox2C.setChecked(true);
            checkBox2C.setBackgroundColor(Color.parseColor("#c158dc"));
        }

        if (question2d) {
            checkBox2D.setChecked(true);
            checkBox2D.setBackgroundColor(Color.parseColor("#c158dc"));
        }
    }

    /*This method hides all the views (containing questions, results, or Intro page) so that a new view can be shown*/
    public void hideAllViews() {
        quizIntroLayout.setVisibility(View.GONE);
        boyImageView.setVisibility(View.GONE);
        teacherImageView.setVisibility(View.GONE);
        question1Layout.setVisibility(View.GONE);
        question2Layout.setVisibility(View.GONE);
        question3Layout.setVisibility(View.GONE);
        question4Layout.setVisibility(View.GONE);
        quizScoreLayout.setVisibility(View.GONE);

        //Run the next line(s) only if in Portrait mode
        if (!isLandscapeMode) {
            questionGraphicView.setVisibility(View.GONE);
        }
    }

    /**
     * This method is called when the user presses the "Start" button to start the quiz
     *
     * @param view is the button view that was clicked
     **/
    public void startQuiz(View view) {
        //Change the View Page from 0 to 1
        currentScreen = 1;
        selectViewLayout(currentScreen);
    }

    /**
     * This method is called when the user selects answer in Question 1
     *
     * @param view is the RadioButton view that was clicked
     **/
    public void onClickQuestionOne(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.question_1_a:
                if (checked) {
                    //Highlight the picked radio button, remove highlight from other options
                    radioButton1A.setBackgroundColor(Color.parseColor("#c158dc"));
                    radioButton1B.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1C.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1D.setBackgroundColor(Color.TRANSPARENT);

                    // Record Answer to global variable
                    question1a = true;
                    question1b = false;
                    question1c = false;
                    question1d = false;
                }
                break;
            case R.id.question_1_b:
                if (checked) {
                    //Highlight the picked radio button, remove highlight from other options
                    radioButton1A.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1B.setBackgroundColor(Color.parseColor("#c158dc"));
                    radioButton1C.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1D.setBackgroundColor(Color.TRANSPARENT);

                    // Record Answer to global variable
                    question1a = false;
                    question1b = true;
                    question1c = false;
                    question1d = false;
                }
                break;
            case R.id.question_1_c:
                if (checked) {
                    //Highlight the picked radio button, remove highlight from other options
                    radioButton1A.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1B.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1C.setBackgroundColor(Color.parseColor("#c158dc"));
                    radioButton1D.setBackgroundColor(Color.TRANSPARENT);

                    // Record Answer to global variable
                    question1a = false;
                    question1b = false;
                    question1c = true;
                    question1d = false;
                }
                break;
            case R.id.question_1_d:
                if (checked) {
                    //Highlight the picked radio button, remove highlight from other options
                    radioButton1A.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1B.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1C.setBackgroundColor(Color.TRANSPARENT);
                    radioButton1D.setBackgroundColor(Color.parseColor("#c158dc"));

                    // Record Answer to global variable
                    question1a = false;
                    question1b = false;
                    question1c = false;
                    question1d = true;
                }
                break;
        }

        //Change the View Page from 1 to 2 to move on to the next question
        currentScreen = 2;
        selectViewLayout(currentScreen);
    }

    /**
     * This method is called when the user checks any of
     * the CheckBox button field on Question 2
     *
     * @param view is the CheckBox button view that was clicked
     **/
    public void onClickBoxQuestion2(View view) {
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.question_2_a:
                if (checked) {
                    //Highlight selection
                    checkBox2A.setBackgroundColor(Color.parseColor("#c158dc"));
                    // Record Answer to global variable
                    question2a = true;
                } else {
                    question2a = false;
                    checkBox2A.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case R.id.question_2_b:
                if (checked) {
                    //Highlight selection
                    checkBox2B.setBackgroundColor(Color.parseColor("#c158dc"));
                    // Record Answer to global variable
                    question2b = true;
                } else {
                    question2b = false;
                    checkBox2B.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case R.id.question_2_c:
                if (checked) {
                    //Highlight selection
                    checkBox2C.setBackgroundColor(Color.parseColor("#c158dc"));
                    // Record Answer to global variable
                    question2c = true;
                } else {
                    question2c = false;
                    checkBox2C.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case R.id.question_2_d:
                if (checked) {
                    //Highlight selection
                    checkBox2D.setBackgroundColor(Color.parseColor("#c158dc"));
                    // Record Answer to global variable
                    question2d = true;
                } else {
                    question2d = false;
                    checkBox2D.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
        }
    }

    /**
     * This method is called when the user presses the "Next" button on Question 2
     *
     * @param view is the button view that was clicked
     **/
    public void onSubmitQuestionTwo(View view) {

        //Change the View Page from 2 to 3 to move to next question
        currentScreen = 3;
        selectViewLayout(currentScreen);
    }

    /**
     * This method is called when the user selects a shape on Question 3
     *
     * @param view is the RadioButton button view that was clicked
     **/
    public void onClickShapeRadioButton(View view) {
        // Is the button now checked?

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.shape_A_radio:
                if (checked) {
                    //Highlight selection, remove highlight from the other selection
                    radioButton3A.setBackgroundColor(Color.parseColor("#c158dc"));
                    radioButton3B.setBackgroundColor(Color.TRANSPARENT);
                    // Record Answer to global variable
                    question3a = true;
                    question3b = false;
                }
                break;
            case R.id.shape_B_radio:
                if (checked) {
                    //Highlight selection, remove highlight from the other selection
                    radioButton3A.setBackgroundColor(Color.TRANSPARENT);
                    radioButton3B.setBackgroundColor(Color.parseColor("#c158dc"));
                    // Record Answer to global variable
                    question3a = false;
                    question3b = true;
                }
                break;
        }

        //Change the View Page from 3 to 4 to move to next page
        currentScreen = 4;
        selectViewLayout(currentScreen);
    }

    /**
     * This method is called when the user submits their final answer on question 4
     *
     * @param view is the button view that was clicked
     **/
    public void finalSubmitPress(View view) {
        //Record final answer that was entered into EditText view by the user
        answerFourText = q4EditTextView.getText().toString();

        //check if EditText view was empty, if it is, set response to default
        if (TextUtils.isEmpty(answerFourText)) {

            answerFourText = "";
        }

        //Remove window focus to hide keyboard
        //Code snippet to hide keyboard obtained from stackoverflow.com
        View focus = this.getCurrentFocus();
        if (focus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Call method to calculate score
        scoreQuiz();
    }

    /**
     * This method calculates the Quiz score. It is called from the
     * finalSubmitPress() method (which is called when the user submits
     * their final answer on question 4).
     **/
    public void scoreQuiz() {
        //Keep track of number of correct responses for informational purposes
        int numberOfCorrectAnswers = 0;

        //Calculate results using conditional statements
        if (question1c) {
            quizScore = quizScore + 25;
            numberOfCorrectAnswers = numberOfCorrectAnswers + 1;
        }
        //For Question 2 to be correct, only the 2nd and 4th fields can be selected
        if (!question2a && !question2c && question2b && question2d) {
            quizScore = quizScore + 25;
            numberOfCorrectAnswers = numberOfCorrectAnswers + 1;
        }
        if (question3a) {
            quizScore = quizScore + 25;
            numberOfCorrectAnswers = numberOfCorrectAnswers + 1;
        }
        if (answerFourText.equals("30")) {
            quizScore = quizScore + 25;
            numberOfCorrectAnswers = numberOfCorrectAnswers + 1;
        }

        //Convert quizScore integer to a String, set TextView to display it
        String quizScoreText = Integer.toString(quizScore);
        quizScoreText = quizScoreText + "%";
        quizScoreView.setText(quizScoreText);

        // Toast message shows Quiz Score while moving on to the explanation screen
        Toast.makeText(MainActivity.this, getString(R.string.toast_msg_score, numberOfCorrectAnswers, quizScore), Toast.LENGTH_LONG).show();

        //Call method to display quiz results explanation
        displayTextQuizResults();

        //Display Results. Screen 5 refers to the Quiz Results layout view
        currentScreen = 5;
        selectViewLayout(currentScreen);

        // Repeats Toast message, which shows Quiz Score while moving on to the explanation screen
        Toast.makeText(MainActivity.this, getString(R.string.toast_msg_score, numberOfCorrectAnswers, quizScore), Toast.LENGTH_LONG).show();
    }

    /**
     * This method is called on to generate content for the Quiz Results
     * Screen. The quizScore variable determines which text will be displayed,
     * and which picture will be displayed. The lower the score, the more negative
     * the boy's reaction
     **/
    public void displayTextQuizResults() {
        if (quizScore == 0) {
            //Set text for quiz results
            quizResultsComments.setText(getText(R.string.zero_score));
            boyImageID = R.drawable.boyfrustrated;
            boyImageView.setImageResource(boyImageID);

        } else if (quizScore == 25) {
            quizResultsComments.setText(getText(R.string.three_wrong));
            boyImageID = R.drawable.boyscared;
            boyImageView.setImageResource(boyImageID);

        } else if (quizScore == 50) {
            quizResultsComments.setText(getText(R.string.fifty_percent_score));
            boyImageID = R.drawable.boyshocked;
            boyImageView.setImageResource(boyImageID);

        } else if (quizScore == 75) {
            quizResultsComments.setText(getText(R.string.seventy_five_percent));
            boyImageID = R.drawable.boytongueout;
            boyImageView.setImageResource(boyImageID);

        } else {
            //Scored 100%
            quizResultsComments.setText(getText(R.string.perfect_score));
            boyImageID = R.drawable.boyhappy;
            boyImageView.setImageResource(boyImageID);
        }
    }

    /**
     * This method is called when the user presses the "Start Over" button to restart the quiz
     * Guidance for creating method and code snippet obtained from stackoverflow.com
     *
     * @param view is the button view that was clicked
     **/
    public void startOver(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

    /*This method clears previous answers when user Starts quiz over*/
    public void resetAnswers() {
        //Reset score
        quizScore = 0;

        //Reset button states to default
        if (question1a) {
            radioButton1A.setChecked(false);
            radioButton1A.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question1b) {
            radioButton1B.setChecked(false);
            radioButton1B.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question1c) {
            radioButton1C.setChecked(false);
            radioButton1C.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question1d) {
            radioButton1D.setChecked(false);
            radioButton1D.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question3a) {
            radioButton3A.setChecked(false);
            radioButton3A.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question3b) {
            radioButton3B.setChecked(false);
            radioButton3B.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question2a) {
            checkBox2A.setChecked(false);
            checkBox2A.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question2b) {
            checkBox2B.setChecked(false);
            checkBox2B.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question2c) {
            checkBox2C.setChecked(false);
            checkBox2C.setBackgroundColor(Color.TRANSPARENT);
        }

        if (question2d) {
            checkBox2D.setChecked(false);
            checkBox2D.setBackgroundColor(Color.TRANSPARENT);
        }

        //Reset EditText field
        q4EditTextView.setText("");
    }
}