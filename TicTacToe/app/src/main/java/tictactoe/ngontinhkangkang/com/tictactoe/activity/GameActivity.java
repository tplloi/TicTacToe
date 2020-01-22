package tictactoe.ngontinhkangkang.com.tictactoe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.core.base.BaseFontActivity;
import com.core.utilities.LConnectivityUtil;
import com.core.utilities.LSocialUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jetbrains.annotations.Nullable;

import tictactoe.ngontinhkangkang.com.tictactoe.GameEngine;
import tictactoe.ngontinhkangkang.com.tictactoe.R;

public class GameActivity extends BaseFontActivity {
    private Button[] button;
    private int[] enableButtons;
    private TextView tvScoreComputer, tvScoreMe;
    private GameEngine game;
    private final static int numberOfButton = 9;//The number of buttons on screen
    private final static int addAPoint = 1;
    private Handler handler;
    private boolean checkRunnableAdded;
    private SharedPreferences saveGameScore;
    private static final String storeAndroidScore = "storeAndroidScore";
    private static final String storeMeScore = "storeMeScore";
    private static final String checkFirsTime = "checkFirstTimes";
    private int scoreComputer, scoreMe;
    private AdView adView;

    @Override
    protected boolean setFullScreen() {
        return false;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected String setTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setShowAdWhenExit(true);
        super.onCreate(savedInstanceState);
        adView = this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("33F2CB83BAADAD6C").addTestDevice("8FA8E91902B43DCB235ED2F6BBA9CAE0")
                .addTestDevice("7DA8A5B216E868636B382A7B9756A4E6").addTestDevice("179198315EB7B069037C5BE8DEF8319A")
                .addTestDevice("A1EC01C33BD69CD589C2AF605778C2E6").build());
        referenceToButton();
        tvScoreComputer = findViewById(R.id.displayScoreAndroid);
        tvScoreMe = findViewById(R.id.displayScoreMe);
        game = new GameEngine(numberOfButton);
        handler = new Handler();
        checkRunnableAdded = false;
        saveGameScore = getSharedPreferences(storeAndroidScore, 0);
        scoreComputer = saveGameScore.getInt(storeAndroidScore, 0);
        scoreMe = saveGameScore.getInt(storeMeScore, 0);
        displayScore();
        if (firstTimeShowInstruction()) {
            showHowToPlay();
        }
        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate_app:
                LSocialUtil.INSTANCE.rateApp(activity, getPackageName());
                break;
            case R.id.more_app:
                LSocialUtil.INSTANCE.moreApp(activity);
                break;
            case R.id.action_info:
                showHowToPlay();
                break;
            case R.id.action_reset:
                scoreComputer = 0;
                scoreMe = 0;
                displayScore();
                storeWinnerScore(storeAndroidScore, scoreComputer);
                storeWinnerScore(storeMeScore, scoreMe);
                startNewGame();
                break;
            default:
                break;
            //return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void referenceToButton() {
        button = new Button[numberOfButton];
        //First Row
        button[0] = findViewById(R.id.entry1);
        button[1] = findViewById(R.id.entry2);
        button[2] = findViewById(R.id.entry3);
        //Second Row
        button[3] = findViewById(R.id.entry4);
        button[4] = findViewById(R.id.entry5);
        button[5] = findViewById(R.id.entry6);
        //Third Row
        button[6] = findViewById(R.id.entry7);
        button[7] = findViewById(R.id.entry8);
        button[8] = findViewById(R.id.entry9);
    }

    private void displayScore() {
        tvScoreComputer.setText(String.valueOf(scoreComputer));
        tvScoreMe.setText(String.valueOf(scoreMe));
    }

    //Start a new Game
    private void startNewGame() {
        int k;
        game.clearBoard();
        for (k = 0; k < numberOfButton; k++) {
            button[k].setText(" ");
            button[k].setEnabled(true);
            //Applied click listener to each button
            button[k].setOnClickListener(new EntryButtonListener(k));
        }
    }

    private class EntryButtonListener implements View.OnClickListener {
        int mePosition;

        EntryButtonListener(int mePosition) {
            this.mePosition = mePosition;
        }

        @Override
        public void onClick(View v) {
            button[mePosition].setText("x");
            button[mePosition].setTextColor(getResources().getColor(R.color.Black));
            game.storePlayerMove(mePosition, game.mePlayer());
            button[mePosition].setEnabled(false);
            disableButtons();
            checkRunnableAdded = handler.postDelayed(displayAndroidMove, 1000);
        }
    }

    private Runnable displayAndroidMove = new Runnable() {
        @Override
        public void run() {
            int checkWinner;
            checkWinner = game.checkWinner();
            if (checkWinner == game.noWinnerYet()) {
                int androidPosition = game.findComputerMove();
                button[androidPosition].setText("o");
                button[androidPosition].setTextColor(getResources().getColor(R.color.Black));
                checkWinner = game.checkWinner();
            }

            if (checkWinner == game.mePlayerWin()) {
                scoreMe = scoreMe + addAPoint;
                tvScoreMe.setText(String.valueOf(scoreMe));
                storeWinnerScore(storeMeScore, scoreMe);
                showGameOverDialog(R.string.meWinner);
            } else if (checkWinner == game.computerPlayerWin()) {
                scoreComputer = scoreComputer + addAPoint;
                tvScoreComputer.setText(String.valueOf(scoreComputer));
                storeWinnerScore(storeAndroidScore, scoreComputer);
                showGameOverDialog(R.string.androidWinner);
            } else if (checkWinner == game.thereIsTie()) {
                showGameOverDialog(R.string.tiedGame);//Show Dialog
            } else {
                enableButtons = game.getAllEntry();
                for (int k = 0; k < numberOfButton; k++) {
                    if (enableButtons[k] == game.emptyBoard()) {
                        button[k].setEnabled(true);
                    }
                }
            }
        }
    };

    private void disableButtons() {
        for (int k = 0; k < numberOfButton; k++) {
            if (button[k].isEnabled()) {
                //Disable button
                button[k].setEnabled(false);
            }
        }
    }

    private void storeWinnerScore(String scoreKey, int scoreValue) {
        SharedPreferences.Editor preferenceEditior = saveGameScore.edit();
        preferenceEditior.putInt(scoreKey, scoreValue);
        preferenceEditior.apply();
    }

    private void showGameOverDialog(final int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(getResources().getString(R.string.gameTittle));
        builder.setMessage(getResources().getString(messageId));
        builder.setPositiveButton(R.string.OK, (dialog, which) -> startNewGame());
        AlertDialog winnerNotify = builder.create();
        winnerNotify.show();
    }

    private void showHowToPlay() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.pop_window_instruction);
        dialog.setCanceledOnTouchOutside(true);
        View masterView = dialog.findViewById(R.id.popWindow);
        masterView.setOnClickListener(view -> dialog.dismiss());
        dialog.show();

    }

    private boolean firstTimeShowInstruction() {
        boolean ranBeforeLaunch = saveGameScore.getBoolean(checkFirsTime, false);
        if (!ranBeforeLaunch) {
            SharedPreferences.Editor editor = saveGameScore.edit();
            editor.putBoolean(checkFirsTime, true);
            editor.apply();
        }
        return !ranBeforeLaunch;
    }

    public void newGame(View v) {
        if (checkRunnableAdded) {
            handler.removeCallbacks(displayAndroidMove);//Stop postDelay
            checkRunnableAdded = false;
        }
        startNewGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout lnAd = findViewById(R.id.ln_ad);
        if (LConnectivityUtil.INSTANCE.isConnected(activity)) {
            lnAd.setVisibility(View.VISIBLE);
        } else {
            lnAd.setVisibility(View.GONE);
        }
        adView.resume();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }
}
