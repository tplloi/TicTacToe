package tictactoe.activity;

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

import androidx.appcompat.widget.Toolbar;

import com.annotation.IsFullScreen;
import com.annotation.IsShowAdWhenExit;
import com.annotation.LogTag;
import com.core.base.BaseFontActivity;
import com.core.utilities.LConnectivityUtil;
import com.core.utilities.LDialogUtil;
import com.core.utilities.LSocialUtil;
import com.core.utilities.LUIUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import kotlin.Unit;
import tictactoe.R;
import tictactoe.engine.GameEngine;

@LogTag("GameActivity")
@IsFullScreen(false)
@IsShowAdWhenExit(true)
public class GameActivity extends BaseFontActivity {
    private Button[] button;
    private TextView tvDisplayScoreAndroid, tvDisplayScoreMe;
    private GameEngine gameEngine;
    private final static int numberOfButton = 9;//The number of buttons on screen
    private final static int addAPoint = 1;
    private Handler mHandler;
    private boolean checkRunnableAdded;
    private SharedPreferences saveGameScore;
    private static final String storeAndroidScore = "storeAndroidScore";
    private static final String storeMeScore = "storeMeScore";
    private static final String checkFirsTime = "checkFirstTimes";
    private int scoreComputer, scoreMe;
    private AdView adView;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
    }

    protected void setupViews() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adView = LUIUtil.Companion.createAdBanner(adView = this.findViewById(R.id.adView));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                logD("onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                logD("onAdFailedToLoad errorCode " + errorCode);
            }

            @Override
            public void onAdOpened() {
                logD("onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                logD("onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                logD("onAdClosed");
            }
        });


        referenceToButton();
        tvDisplayScoreAndroid = findViewById(R.id.tvDisplayScoreAndroid);
        tvDisplayScoreMe = findViewById(R.id.tvDisplayScoreMe);
        gameEngine = new GameEngine(numberOfButton);
        mHandler = new Handler();
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
        if (item.getItemId() == R.id.rate_app) {
            LSocialUtil.Companion.rateApp(this, getPackageName());
        } else if (item.getItemId() == R.id.more_app) {
            LSocialUtil.Companion.moreApp(this, "Toi Yeu Viet Nam");
        } else if (item.getItemId() == R.id.action_info) {
            showHowToPlay();
        } else if (item.getItemId() == R.id.action_reset) {
            scoreComputer = 0;
            scoreMe = 0;
            displayScore();
            storeWinnerScore(storeAndroidScore, scoreComputer);
            storeWinnerScore(storeMeScore, scoreMe);
            startNewGame();
        }
        return false;
    }

    private void referenceToButton() {
        button = new Button[numberOfButton];
        //First Row
        button[0] = findViewById(R.id.btEntry1);
        button[1] = findViewById(R.id.btEntry2);
        button[2] = findViewById(R.id.btEntry3);
        //Second Row
        button[3] = findViewById(R.id.btEntry4);
        button[4] = findViewById(R.id.btEntry5);
        button[5] = findViewById(R.id.btEntry6);
        //Third Row
        button[6] = findViewById(R.id.btEntry7);
        button[7] = findViewById(R.id.btEntry8);
        button[8] = findViewById(R.id.btEntry9);
    }

    private void displayScore() {
        tvDisplayScoreAndroid.setText(String.valueOf(scoreComputer));
        tvDisplayScoreMe.setText(String.valueOf(scoreMe));
    }

    //Start a new Game
    private void startNewGame() {
        int k;
        gameEngine.clearBoard();
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
//            button[mePosition].setTextColor(getResources().getColor(R.color.black));
            gameEngine.storePlayerMove(mePosition, gameEngine.mePlayer());
            button[mePosition].setEnabled(false);
            disableButtons();
            checkRunnableAdded = mHandler.postDelayed(displayAndroidMove, 1000);
        }
    }

    private final Runnable displayAndroidMove = new Runnable() {
        @Override
        public void run() {
            int checkWinner;
            checkWinner = gameEngine.checkWinner();
            if (checkWinner == gameEngine.noWinnerYet()) {
                int androidPosition = gameEngine.findComputerMove();
                button[androidPosition].setText("o");
//                button[androidPosition].setTextColor(getResources().getColor(R.color.black));
                checkWinner = gameEngine.checkWinner();
            }

            if (checkWinner == gameEngine.mePlayerWin()) {
                scoreMe = scoreMe + addAPoint;
                tvDisplayScoreMe.setText(String.valueOf(scoreMe));
                storeWinnerScore(storeMeScore, scoreMe);
                showGameOverDialog(R.string.meWinner);
            } else if (checkWinner == gameEngine.computerPlayerWin()) {
                scoreComputer = scoreComputer + addAPoint;
                tvDisplayScoreAndroid.setText(String.valueOf(scoreComputer));
                storeWinnerScore(storeAndroidScore, scoreComputer);
                showGameOverDialog(R.string.androidWinner);
            } else if (checkWinner == gameEngine.thereIsTie()) {
                showGameOverDialog(R.string.tiedGame);//Show Dialog
            } else {
                int[] enableButtons = gameEngine.getAllEntry();
                for (int k = 0; k < numberOfButton; k++) {
                    if (enableButtons[k] == gameEngine.emptyBoard()) {
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
        LDialogUtil.Companion.showDialog1(
                this,
                getString(R.string.gameTittle),
                getResources().getString(messageId),
                getString(R.string.confirm),
                unit -> {
                    startNewGame();
                    return Unit.INSTANCE;
                }
        );
    }

    private void showHowToPlay() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_instruction);
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
            mHandler.removeCallbacks(displayAndroidMove);//Stop postDelay
            checkRunnableAdded = false;
        }
        startNewGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout lnAd = findViewById(R.id.lnAd);
        if (LConnectivityUtil.Companion.isConnected()) {
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
