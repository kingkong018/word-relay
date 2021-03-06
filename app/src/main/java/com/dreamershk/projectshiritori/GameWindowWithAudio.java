package com.dreamershk.projectshiritori;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameWindowWithAudio extends AppCompatActivity implements GameView {
    protected String log_name= "GAMEWINDOWWITHAUDIO";
    //UI elements
    ProgressDialog loadingDialog;
    boolean isLoadingDialogClosed, isPlayerQueueBarInitialized, isButtonSendChanged2Abstain;
    AlertDialog startGameAlertDialog, assignWindowAlertDialog;
    ProgressBar pb;
    ImageView buttonShowActionWindow, buttonShowVoiceBar;
    ImageButton buttonAction, buttonAbstain, buttonSkip, buttonReverse, buttonHelp, buttonAssign, buttonVoiceInput;
    TextView tv_score, tv_round, tv_abstain_button, tv_help_button, tv_assign_button, tv_reverse_button, tv_skip_button, system_message;
    ImageView image_life1, image_life2, image_life3;

    PopupWindow popupWindow4Action;
    View popupWindowView;
    View.OnClickListener onClickListener4SendButton, onClickListener4AbstainButton;
    LinearLayout linear_layout_action_buttons_window, linear_layout_4_button_skip, linear_layout_4_button_abstain,
            linear_layout_4_button_reverse, linear_layout_4_button_assign,linear_layout_4_button_help, linear_layout_queue_bar,
            linear_layout_system_message_box;
    RelativeLayout canva, relative_layout_bottom_bar;
    FloatingActionButton floatingActionButtonSystemMessage;
    boolean isPopupWindowClicked = false;
    List<ImageView> imageArrowList;
    List<ImageView> imageIconList;
    int chance;
    //Game playUtility
    int maxTime = 30;
    int numberOfWordInCanva = 0;
    CountDownTimer countDownTimer;
    boolean isActive, isOver, isQueueBarFinishedUpdate;

    protected GameActionListener gameActionListener;
    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        //        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_window_with_audio);

        //Set up UI
        //Remaining time progress bar
        pb = (ProgressBar)findViewById(R.id.pb_remaining_time);
        //Edit Text field for et_input and lastCharacter
        buttonVoiceInput = (ImageButton) findViewById(R.id.ib_voice_input);
        buttonVoiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do the recognizion.
                promptSpeechInput();
            }
        });

        //Set up textfields: tv_score, tv_round
        tv_score = (TextView)findViewById(R.id.tv_score);
        tv_round = (TextView)findViewById(R.id.tv_round);
        //Set up image view for three lifes
        image_life1 = (ImageView)findViewById(R.id.image_life1);
        image_life2 = (ImageView)findViewById(R.id.image_life2);
        image_life3 = (ImageView)findViewById(R.id.image_life3);
        //
        canva = (RelativeLayout)findViewById(R.id.relative_layout_canva);
        //
        linear_layout_system_message_box = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.system_message_window, null, false);
        floatingActionButtonSystemMessage = (FloatingActionButton)findViewById(R.id.floating_button_system_message);
        floatingActionButtonSystemMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = new PopupWindow(linear_layout_system_message_box, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0 , 0);
            }
        });
        system_message = (TextView)findViewById(R.id.txt_system_message);
        //set up skill bar
        linear_layout_action_buttons_window = (LinearLayout)findViewById(R.id.pop_up_window);
        linear_layout_action_buttons_window.setVisibility(View.GONE);
        //Set action button for pop up action window
        buttonShowActionWindow = (ImageView) findViewById(R.id.iv_show_voice_bar);
        buttonShowActionWindow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isPopupWindowClicked) {
                    isPopupWindowClicked = true;
                    linear_layout_action_buttons_window.setVisibility(View.VISIBLE);
                    relative_layout_bottom_bar.setVisibility(View.GONE);
                }
            }
        });
        buttonShowVoiceBar = (ImageView) linear_layout_action_buttons_window.findViewById(R.id.iv_return);
        buttonShowVoiceBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPopupWindowClicked = false;
                relative_layout_bottom_bar.setVisibility(View.VISIBLE);
                linear_layout_action_buttons_window.setVisibility(View.GONE);
            }
        });
        //Set the five buttons in the pop up window.
        buttonAbstain = (ImageButton) linear_layout_action_buttons_window.findViewById(R.id.button_abstain);
        onClickListener4AbstainButton = new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.abstain();
            }
        };
        buttonAbstain.setOnClickListener(onClickListener4AbstainButton);
        buttonAssign = (ImageButton) linear_layout_action_buttons_window.findViewById(R.id.button_assign);
        buttonAssign.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (assignWindowAlertDialog != null) assignWindowAlertDialog.show();
            }
        });
        buttonSkip = (ImageButton) linear_layout_action_buttons_window.findViewById(R.id.button_skip);
        buttonSkip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.skip();
            }
        });
        buttonReverse = (ImageButton) linear_layout_action_buttons_window.findViewById(R.id.button_reverse);
        buttonReverse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.reverse();
            }
        });
        buttonHelp = (ImageButton) linear_layout_action_buttons_window.findViewById(R.id.button_help);
        buttonHelp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.help();
            }
        });
        tv_abstain_button = (TextView) linear_layout_action_buttons_window.findViewById(R.id.tv_abstain_button);
        tv_help_button = (TextView) linear_layout_action_buttons_window.findViewById(R.id.tv_help_button);
        tv_assign_button = (TextView) linear_layout_action_buttons_window.findViewById(R.id.tv_assign_button);
        tv_reverse_button = (TextView) linear_layout_action_buttons_window.findViewById(R.id.tv_reverse_button);
        tv_skip_button = (TextView) linear_layout_action_buttons_window.findViewById(R.id.tv_skip_button);
        linear_layout_4_button_skip = (LinearLayout) linear_layout_action_buttons_window.findViewById(R.id.linear_layout_4_button_skip);
        linear_layout_4_button_abstain = (LinearLayout) linear_layout_action_buttons_window.findViewById(R.id.linear_layout_4_button_abstain);
        linear_layout_4_button_assign = (LinearLayout) linear_layout_action_buttons_window.findViewById(R.id.linear_layout_4_button_assign);
        linear_layout_4_button_help = (LinearLayout) linear_layout_action_buttons_window.findViewById(R.id.linear_layout_4_button_help);
        linear_layout_4_button_reverse = (LinearLayout) linear_layout_action_buttons_window.findViewById(R.id.linear_layout_4_button_reverse);

        linear_layout_queue_bar = (LinearLayout)findViewById(R.id.linear_layout_queue_bar);
        relative_layout_bottom_bar = (RelativeLayout)findViewById(R.id.relative_layout_bottom_bar);

        isLoadingDialogClosed = false;
        isPlayerQueueBarInitialized = false;
        isQueueBarFinishedUpdate = true;
        isButtonSendChanged2Abstain = false;
        chance = 3;
        imageArrowList = new ArrayList<ImageView>();
        imageIconList = new ArrayList<ImageView>();
    }
    public void setStartGame(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameWindowWithAudio.this);
                dialog.setTitle("開始遊戲");
                //startGameAlertDialog.setMessage("需等待最少一名玩家才可開始遊戲。");
                dialog.setCancelable(false);
                dialog.setPositiveButton("確認", null);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                final AlertDialog alertDialog = dialog.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (gameActionListener.getNumberOfPlayers() < 2){
                                    Toast.makeText(GameWindowWithAudio.this, "需等待最少一名玩家才可開始遊戲。", Toast.LENGTH_SHORT).show();
                                }else{
                                    alertDialog.dismiss();
                                    gameActionListener.startGame();
                                    isPlayerQueueBarInitialized = true; //after all player enter the room, the number of players on the bar does not change anymore.
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public void setGameActionListener(GameActionListener gameActionListener) {
        this.gameActionListener=gameActionListener;
    }

    @Override
    public void addMessage(CirculateMessage message) {
        final CirculateMessage circulateMessage = message;
        final String m = circulateMessage.getMessage();
        String output = "";
        if (m.equals(SystemMessage.DATABASE_LOADING_ERROR)){
            Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (m.equals(SystemMessage.WORD_DOES_NOT_EXIST) || m.equals(SystemMessage.WORD_IS_INVALID) || m.equals(SystemMessage.WORD_REPEATED) ||
                m.equals(SystemMessage.ACTION_ABSTAIN) || m.equals(SystemMessage.ACTION_SKIP) || m.equals(SystemMessage.ACTION_REVERSE)) {
            output = circulateMessage.getAuthor() + " " + m + "請繼續回答：" + circulateMessage.getWord();
        }else if(m.equals(SystemMessage.ACTION_ASSIGN)){
            output = circulateMessage.getAuthor() + " 點名要求 " + circulateMessage.getExtra() + "幫助。請繼續回答：" + circulateMessage.getWord();
        } else if (m.equals(SystemMessage.ACTION_HELP)){
            output = circulateMessage.getAuthor() + " " + m;
        }else if (m.equals(SystemMessage.NOT_POSSIBLE_TO_ANSWER)){
            output = "由於詞庫沒有合適的答案，請回答新詞語："+circulateMessage.getWord();
        }else{
            output = m;
        }
        final String s = output;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                system_message.setText(s); //disappear after 3s..
                system_message.setVisibility(View.VISIBLE);
                new CountDownTimer(3000, 1500) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                    @Override
                    public void onFinish() {
                        system_message.setVisibility(View.GONE);
                    }
                }.start();
                TextView all_messages = (TextView)linear_layout_system_message_box.findViewById(R.id.txt_all_system_messages);
                all_messages.setText(s + "\n" + all_messages.getText().toString());
            }
        });

    }
    public void addChatBubble(UserType userType, String message) {
        addChatBubble(userType, "", message, -1, -1, -1, -1);
    }
    @Override
    public synchronized void addChatBubble(UserType userType, String author, final String message, int score, int life, int round, int iconResId) {
        //add word to canva
        //text size, rotation, color, position
        int canvaHeight = canva.getHeight();
        int canvaWidth = canva.getWidth();
        final int size = (int)(Math.random() * (canvaHeight / 20)) + 25;
        final int rotation = (int)(Math.random() * 60) - 60;
        int a,r,g,b;
        a = (int)(Math.random() * 115) + 140;
        r = (int)(Math.random() * 256);
        g = (int)(Math.random() * 180);
        b = (int)(Math.random() * 180);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int isAlignTop = (int)(Math.random() * 2);
        int isAlignLeft = (int)(Math.random() * 2);
        int marginTop = 0, marginLeft = 0, marginRight = 0, marginBottom = 0;
        if (isAlignTop == 0){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            marginTop = (int)(Math.random() * (canvaHeight/2));
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            marginBottom = (int)(Math.random() * (canvaHeight/2));
        }
        if (isAlignLeft == 0){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            marginLeft = (int)(Math.random() * (canvaWidth/2));
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            marginRight = (int)(Math.random() * (canvaWidth/2));
        }
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        final int color = Color.argb(255, r, g, b);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View v = canva.getChildAt(canva.getChildCount()-1); //Get the system message and button.
                canva.removeViewAt(canva.getChildCount()-1); //remove it first and add back afterwards.
                TextView textView = new TextView(GameWindowWithAudio.this);
                textView.setText(message);
                textView.setTextSize(size);
                textView.setRotation(rotation);
                textView.setTextColor(color);
                textView.setBackgroundColor(Color.parseColor("#8e2eb8ab"));
                textView.setLayoutParams(layoutParams);
                if (canva.getChildCount()>0){
                    canva.getChildAt(canva.getChildCount()-1).setBackgroundColor(Color.TRANSPARENT);
                }
                canva.addView(textView);
                canva.addView(v); //add back the message and button so that they are on the top.
                numberOfWordInCanva++;
                if (numberOfWordInCanva > 10) {
                    canva.removeViewAt(0);
                    numberOfWordInCanva--;
                }
            }
        });
    }

    @Override
    public void activate() {
        isActive = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Enable view
                linear_layout_queue_bar.setVisibility(View.GONE);
                relative_layout_bottom_bar.setVisibility(View.VISIBLE);
                buttonVoiceInput.setEnabled(true);
                //enable buttons
                floatingActionButtonSystemMessage.setVisibility(View.VISIBLE);


                setAssignPlayerWindow(gameActionListener.getPlayerQueue());
                //start timer
                countDownTimer = new CountDownTimer(maxTime*1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long currentTime = maxTime - millisUntilFinished/1000;
                        int progress = (int)(currentTime*100/maxTime);
                        pb.setProgress(progress);
                    }
                    public void onFinish() {
                        gameActionListener.abstain();
                    }
                };
                countDownTimer.start();

            }
        });
    }

    @Override
    public void deactivate() {
        isActive = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                relative_layout_bottom_bar.setVisibility(View.GONE);
                linear_layout_queue_bar.setVisibility(View.VISIBLE);
                //
                floatingActionButtonSystemMessage.setVisibility(View.GONE);

                buttonAbstain.setEnabled(false);


                //timer & progresas bar
                if (countDownTimer != null) countDownTimer.cancel();
                pb.setProgress(0);
                //
                if (isPopupWindowClicked){
                    isPopupWindowClicked = false;
                    linear_layout_action_buttons_window.setVisibility(View.GONE);
                }
                /*if (popupWindow4Action != null && isPopupWindowClicked){
                    isPopupWindowClicked = false;
                    popupWindow4Action.dismiss();
                }*/
            }
        });
    }

    @Override
    //synchronized beacuse of the chance variable.
    public synchronized void setPlayerInfo(String name, int score, int chance, int round) {
        this.chance = chance;
        final int s=score, c=chance, r=round;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (s>9){
                    tv_score.setText(s + "");
                }else{
                    tv_score.setText("0" + s);
                }
                switch(c){
                    case 0:
                        image_life1.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        image_life2.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        image_life3.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        break;
                    case 1:
                        image_life1.setImageResource(R.drawable.ic_favorite_black_24dp);
                        image_life2.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        image_life3.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        break;
                    case 2:
                        image_life1.setImageResource(R.drawable.ic_favorite_black_24dp);
                        image_life2.setImageResource(R.drawable.ic_favorite_black_24dp);
                        image_life3.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        break;
                    default:
                        image_life1.setImageResource(R.drawable.ic_favorite_black_24dp);
                        image_life2.setImageResource(R.drawable.ic_favorite_black_24dp);
                        image_life3.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
                tv_round.setText(Integer.toString(r));
            }
        });
    }

    @Override
    public void setLastChar(String lastChar) {
        //
    }

    @Override
    public synchronized void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer) {
        final List<Player> list = playerList;
        if (isQueueBarFinishedUpdate){
            isQueueBarFinishedUpdate = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Max player limit: 6
                    int i = 0;
                    linear_layout_queue_bar.removeAllViews();
                    for (Player p : list){
                        LinearLayout queue_bar_item = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.queue_bar_item, null);
                        TextView name = (TextView)queue_bar_item.findViewById(R.id.txt_queue_player_name);
                        name.setText(p.getName());
                        //the icon...
                        ImageView imageView = (ImageView)queue_bar_item.findViewById(R.id.image_queue_player_icon);
                        imageView.setImageResource(p.getIconResId());
                        ImageView arrow = new ImageView(GameWindowWithAudio.this);
                        if (i==0){
                            arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_white_18dp);
                            i++;
                        }else{
                            arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
                        }
                        linear_layout_queue_bar.addView(arrow);
                        linear_layout_queue_bar.addView(queue_bar_item);
                    }
                    isQueueBarFinishedUpdate = true;
                }
            });
        }
    }

    @Override
    public void setSkillButton(final int score, final int playerNumber) {
        final String enabledBackgroundColor = "#96c4bdbd";
        final String disabledBackgroundColor = "#f0bdb7b7";
        final String enabledTextColor = "#FFFFFF";
        final String disabledTextColor = "#a99f9f";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //skip button
                if (score < GameManager.SKIP_POINT){
                    buttonSkip.setEnabled(false);
                    linear_layout_4_button_skip.setBackgroundColor(Color.parseColor(disabledBackgroundColor));
                    tv_skip_button.setTextColor(Color.parseColor(disabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_skip));
                }else{
                    buttonSkip.setEnabled(true);
                    linear_layout_4_button_skip.setBackgroundColor(Color.parseColor(enabledBackgroundColor));
                    tv_skip_button.setTextColor(Color.parseColor(enabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_skip)+"x"+actionValue.get(i));
                }
                //reverse button
                if (playerNumber < 3 || score < GameManager.REVERSE_POINT){
                    buttonReverse.setEnabled(false);
                    linear_layout_4_button_reverse.setBackgroundColor(Color.parseColor(disabledBackgroundColor));
                    tv_reverse_button.setTextColor(Color.parseColor(disabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_reverse));
                }else{
                    buttonReverse.setEnabled(true);
                    linear_layout_4_button_reverse.setBackgroundColor(Color.parseColor(enabledBackgroundColor));
                    tv_reverse_button.setTextColor(Color.parseColor(enabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_reverse)+"x"+actionValue.get(i));
                }
                //help button
                if (score < GameManager.HELP_POINT){
                    buttonHelp.setEnabled(false);
                    linear_layout_4_button_help.setBackgroundColor(Color.parseColor(disabledBackgroundColor));
                    tv_help_button.setTextColor(Color.parseColor(disabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_help));
                }else{
                    buttonHelp.setEnabled(true);
                    linear_layout_4_button_help.setBackgroundColor(Color.parseColor(enabledBackgroundColor));
                    tv_help_button.setTextColor(Color.parseColor(enabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_help)+"x"+actionValue.get(i));
                }
                //assign button
                if (playerNumber < 3 || score < GameManager.ASSIGN_POINT){
                    buttonAssign.setEnabled(false);
                    linear_layout_4_button_assign.setBackgroundColor(Color.parseColor(disabledBackgroundColor));
                    tv_assign_button.setTextColor(Color.parseColor(disabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_assign));
                }else{
                    buttonAssign.setEnabled(true);
                    linear_layout_4_button_assign.setBackgroundColor(Color.parseColor(enabledBackgroundColor));
                    tv_assign_button.setTextColor(Color.parseColor(enabledTextColor));
                    //tv_skip_button.setText(getString(R.string.button_assign)+"x"+actionValue.get(i));
                }
                //abstain button
                if (chance <= 1){
                    buttonAbstain.setEnabled(false);
                    linear_layout_4_button_abstain.setBackgroundColor(Color.parseColor(disabledBackgroundColor));
                    tv_abstain_button.setTextColor(Color.parseColor(disabledTextColor));
                }else{
                    buttonAbstain.setEnabled(true);
                    linear_layout_4_button_abstain.setBackgroundColor(Color.parseColor(enabledBackgroundColor));
                    tv_abstain_button.setTextColor(Color.parseColor(enabledTextColor));
                }
                //popupWindow4Action.update();
            }
        });
    }

    @Override
    public void setAssignPlayerWindow(List<Player> playerList) {
        if (playerList.size() > 2){
            final List<Player> list = playerList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameWindowWithAudio.this);
                    LinearLayout assign_window = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.assign_player_window, null);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ImageButton[] imageButton = new ImageButton[5];
                    imageButton[0] = (ImageButton)assign_window.findViewById(R.id.image_button_assign_1);
                    imageButton[1] = (ImageButton)assign_window.findViewById(R.id.image_button_assign_2);
                    imageButton[2] = (ImageButton)assign_window.findViewById(R.id.image_button_assign_3);
                    imageButton[3] = (ImageButton)assign_window.findViewById(R.id.image_button_assign_4);
                    imageButton[4] = (ImageButton)assign_window.findViewById(R.id.image_button_assign_5);
                    TextView[] textViews = new TextView[5];
                    textViews[0] = (TextView)assign_window.findViewById(R.id.txt_assign_1);
                    textViews[1] = (TextView)assign_window.findViewById(R.id.txt_assign_2);
                    textViews[2] = (TextView)assign_window.findViewById(R.id.txt_assign_3);
                    textViews[3] = (TextView)assign_window.findViewById(R.id.txt_assign_4);
                    textViews[4] = (TextView)assign_window.findViewById(R.id.txt_assign_5);
                    LinearLayout[] linearLayouts = new LinearLayout[5];
                    linearLayouts[0] = (LinearLayout)assign_window.findViewById(R.id.linear_layout_assign1);
                    linearLayouts[1] = (LinearLayout)assign_window.findViewById(R.id.linear_layout_assign2);
                    linearLayouts[2] = (LinearLayout)assign_window.findViewById(R.id.linear_layout_assign3);
                    linearLayouts[3] = (LinearLayout)assign_window.findViewById(R.id.linear_layout_assign4);
                    linearLayouts[4] = (LinearLayout)assign_window.findViewById(R.id.linear_layout_assign5);
                    builder.setView(assign_window);
                    assignWindowAlertDialog = builder.create();
                    for (int i=0; i <list.size(); i++){
                        final int index = i;
                        linearLayouts[i].setVisibility(View.VISIBLE);
                        imageButton[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gameActionListener.assign(list.get(index).hashCode());
                                assignWindowAlertDialog.dismiss();
                            }
                        });
                        textViews[i].setText(list.get(i).getName());
                    }
                }
            });
        }
    }

    @Override
    public void showResult(final List<Player> sortedPlayerList){
        isOver = true;
        if (!isFinishing())
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cleanUp();
                    //
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameWindowWithAudio.this);
                    builder.setCancelable(false);
                    builder.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    LinearLayout linearLayout = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.ranking, null);
                    for (int i = 0; i < sortedPlayerList.size(); i++){
                        Player p = sortedPlayerList.get(i);
                        LinearLayout layout = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.rank_item, null);
                        TextView order = (TextView)layout.findViewById(R.id.txt_ranking_order);
                        order.setText((i+1) + "");
                        ImageView icon = (ImageView)layout.findViewById(R.id.iv_ranking_icon);
                        icon.setImageResource(p.getIconResId());
                        TextView name = (TextView)layout.findViewById(R.id.txt_ranking_name);
                        name.setText(p.getName());
                        TextView score = (TextView)layout.findViewById(R.id.txt_ranking_score);
                        score.setText(p.getScore()+"");
                        linearLayout.addView(layout);
                    }
                    builder.setView(linearLayout);
                    builder.show();
                }
            });
    }

    protected void cleanUp(){
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (popupWindow4Action != null  && isPopupWindowClicked)
            popupWindow4Action.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d(log_name, "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //Go back to main activity.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請大聲說出你的答案。");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Not supported", Toast.LENGTH_SHORT).show();
        }
    }
    private void showVoicInputSelectionDialog(ArrayList<String> result){
        LinearLayout layout = (LinearLayout)LayoutInflater.from(GameWindowWithAudio.this).inflate(R.layout.voice_input_window, null);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, result);
        final ListView listView = (ListView)layout.findViewById(R.id.list_view_voice_input);
        listView.setEmptyView(layout.findViewById(R.id.txt_empty_view_4_voice_input_listview));
        //set dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(GameWindowWithAudio.this);
        builder.setView(layout);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        layout.findViewById(R.id.ib_voice_input_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                promptSpeechInput();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.dismiss();
                String word = (String)listView.getItemAtPosition(position);
                gameActionListener.wordSubmitted(word);
            }
        });
        alertDialog.show();
    }
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    showVoicInputSelectionDialog(result);
                }else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){

                }else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){

                }else{

                }
                break;
            }
        }
    }
}
