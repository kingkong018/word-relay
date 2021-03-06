package com.dreamershk.projectshiritori;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.BubbleDetail;
import com.dreamershk.projectshiritori.model.CirculateMessage;
import com.dreamershk.projectshiritori.model.Player;
import com.dreamershk.projectshiritori.model.SystemMessage;
import com.dreamershk.projectshiritori.model.UserType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameWindow extends AppCompatActivity implements GameView{
    protected String log_name= "GAMEWINDOW";
    //UI elements
    ProgressDialog loadingDialog;
    boolean isLoadingDialogClosed, isPlayerQueueBarInitialized, isButtonSendChanged2Abstain;
    AlertDialog startGameAlertDialog, assignWindowAlertDialog;
    ProgressBar pb;
    EditText et_input, et_lastChar;
    ImageButton buttonSend, buttonAction, buttonAbstain, buttonSkip, buttonReverse, buttonHelp, buttonAssign;
    TextView tv_score, tv_round, tv_abstain_button, tv_help_button, tv_assign_button, tv_reverse_button, tv_skip_button;
    ImageView image_life1, image_life2, image_life3;
    ListView bubbleList;
    GameBubblesAdapter adapter;
    PopupWindow popupWindow4Action;
    View popupWindowView;
    View.OnClickListener onClickListener4SendButton, onClickListener4AbstainButton;
    LinearLayout linear_layout_bottom_pop_up_window, linear_layout_4_button_skip, linear_layout_4_button_abstain,
            linear_layout_4_button_reverse, linear_layout_4_button_assign,linear_layout_4_button_help, linear_layout_queue_bar,
            linear_layout_bottom_bar;
    boolean isPopupWindowClicked = false;
    List<ImageView> imageArrowList;
    List<ImageView> imageIconList;
    int chance;
    //Game playUtility
    int maxTime = 30;
    CountDownTimer countDownTimer;
    boolean isActive, isOver, isQueueBarFinishedUpdate;

    protected GameActionListener gameActionListener;

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
         int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
         decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_window);

        //Set up UI
        //Remaining time progress bar
        pb = (ProgressBar)findViewById(R.id.pb_remaining_time);
        //Edit Text field for et_input and lastCharacter
        et_input = (EditText)findViewById(R.id.et_input);
        et_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide the action window.
                isPopupWindowClicked = false;
                buttonAction.setImageResource(R.drawable.ic_open_in_browser_black_36dp);
                buttonAction.setBackgroundColor(Color.parseColor("#FFFFFF"));
                linear_layout_bottom_pop_up_window.setVisibility(View.GONE);
            }
        });
        et_input.addTextChangedListener(new GameTextWatcher());
        et_lastChar = (EditText)findViewById(R.id.et_lastChar);
        //Sent button
        buttonSend = (ImageButton)findViewById(R.id.ib_send);
        onClickListener4SendButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameActionListener.wordSubmitted(et_lastChar.getText().toString()+et_input.getText().toString());
                et_input.setText("");
                //TODO: hide the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        buttonSend.setOnClickListener(onClickListener4SendButton);
        //Set up textfields: tv_score, tv_round
        tv_score = (TextView)findViewById(R.id.tv_score);
        tv_round = (TextView)findViewById(R.id.tv_round);
        //Set up image view for three lifes
        image_life1 = (ImageView)findViewById(R.id.image_life1);
        image_life2 = (ImageView)findViewById(R.id.image_life2);
        image_life3 = (ImageView)findViewById(R.id.image_life3);
        //Set up listview
        bubbleList = (ListView)findViewById(R.id.listview_multiplayer_bubbles);
        bubbleList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        adapter = new GameBubblesAdapter(new ArrayList<BubbleDetail>(), this);
        bubbleList.setAdapter(adapter);
        bubbleList.setEmptyView(findViewById(R.id.emptyview_listview_multiplayer_bubbles));
        //
        linear_layout_bottom_pop_up_window = (LinearLayout)findViewById(R.id.pop_up_window);
        linear_layout_bottom_pop_up_window.setVisibility(View.GONE);
        //Set up pop up action window
        popupWindowView = LayoutInflater.from(this).inflate(R.layout.popup_window_for_game_action, null, false);
        popupWindow4Action = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //to dismiss the window when touch outside
        popupWindow4Action.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        popupWindow4Action.setOutsideTouchable(true);
        popupWindow4Action.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPopupWindowClicked = false;
                buttonAction.setImageResource(R.drawable.ic_open_in_browser_black_36dp);
                buttonAction.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });
        //Set action button for pop up action windwo
        buttonAction = (ImageButton)findViewById(R.id.ib_action);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isPopupWindowClicked) {
                    /*Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int[] location = new int[2];
                    findViewById(R.id.relative_layout_bottom_bar).getLocationOnScreen(location);
                    int y = location[1];
                    popupWindow4Action.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, size.y-y);*/
                    isPopupWindowClicked = true;
                    buttonAction.setImageResource(R.drawable.ic_open_in_browser_white_36dp);
                    buttonAction.setBackgroundColor(Color.parseColor("#80cbc4"));
                    linear_layout_bottom_pop_up_window.setVisibility(View.VISIBLE);
                } else {
                    //popupWindow4Action.dismiss();
                    isPopupWindowClicked = false;
                    buttonAction.setImageResource(R.drawable.ic_open_in_browser_black_36dp);
                    buttonAction.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    linear_layout_bottom_pop_up_window.setVisibility(View.GONE);
                }
            }
        });
        //Set the five buttons in the pop up window.
        buttonAbstain = (ImageButton) linear_layout_bottom_pop_up_window.findViewById(R.id.button_abstain);
        onClickListener4AbstainButton = new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.abstain();
            }
        };
        buttonAbstain.setOnClickListener(onClickListener4AbstainButton);
        buttonAssign = (ImageButton) linear_layout_bottom_pop_up_window.findViewById(R.id.button_assign);
        buttonAssign.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (assignWindowAlertDialog != null) assignWindowAlertDialog.show();
            }
        });
        buttonSkip = (ImageButton) linear_layout_bottom_pop_up_window.findViewById(R.id.button_skip);
        buttonSkip.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.skip();
            }
        });
        buttonReverse = (ImageButton) linear_layout_bottom_pop_up_window.findViewById(R.id.button_reverse);
        buttonReverse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.reverse();
            }
        });
        buttonHelp = (ImageButton) linear_layout_bottom_pop_up_window.findViewById(R.id.button_help);
        buttonHelp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.help();
            }
        });
        tv_abstain_button = (TextView) linear_layout_bottom_pop_up_window.findViewById(R.id.tv_abstain_button);
        tv_help_button = (TextView) linear_layout_bottom_pop_up_window.findViewById(R.id.tv_help_button);
        tv_assign_button = (TextView) linear_layout_bottom_pop_up_window.findViewById(R.id.tv_assign_button);
        tv_reverse_button = (TextView) linear_layout_bottom_pop_up_window.findViewById(R.id.tv_reverse_button);
        tv_skip_button = (TextView) linear_layout_bottom_pop_up_window.findViewById(R.id.tv_skip_button);
        linear_layout_4_button_skip = (LinearLayout) linear_layout_bottom_pop_up_window.findViewById(R.id.linear_layout_4_button_skip);
        linear_layout_4_button_abstain = (LinearLayout) linear_layout_bottom_pop_up_window.findViewById(R.id.linear_layout_4_button_abstain);
        linear_layout_4_button_assign = (LinearLayout) linear_layout_bottom_pop_up_window.findViewById(R.id.linear_layout_4_button_assign);
        linear_layout_4_button_help = (LinearLayout) linear_layout_bottom_pop_up_window.findViewById(R.id.linear_layout_4_button_help);
        linear_layout_4_button_reverse = (LinearLayout) linear_layout_bottom_pop_up_window.findViewById(R.id.linear_layout_4_button_reverse);

        linear_layout_queue_bar = (LinearLayout)findViewById(R.id.linear_layout_queue_bar);
        linear_layout_bottom_bar = (LinearLayout)findViewById(R.id.linear_layout_bottom_bar);

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameWindow.this);
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
                                    Toast.makeText(GameWindow.this, "需等待最少一名玩家才可開始遊戲。", Toast.LENGTH_SHORT).show();
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
        if (m.equals(SystemMessage.DATABASE_LOADING_ERROR)){
            Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (m.equals(SystemMessage.WORD_DOES_NOT_EXIST) || m.equals(SystemMessage.WORD_IS_INVALID) || m.equals(SystemMessage.WORD_REPEATED) ||
                m.equals(SystemMessage.ACTION_ABSTAIN) || m.equals(SystemMessage.ACTION_SKIP) || m.equals(SystemMessage.ACTION_REVERSE)) {
            addChatBubble(UserType.SYSTEM, circulateMessage.getAuthor() + " " + m + "請繼續回答：\n" + circulateMessage.getWord());
        }else if(m.equals(SystemMessage.ACTION_ASSIGN)){
            addChatBubble(UserType.SYSTEM, circulateMessage.getAuthor() + " 點名要求 " + circulateMessage.getExtra() + "幫助。請繼續回答：\n" + circulateMessage.getWord());
        } else if (m.equals(SystemMessage.ACTION_HELP)){
            addChatBubble(UserType.SYSTEM, circulateMessage.getAuthor() + " " + m);
        }else if (m.equals(SystemMessage.NOT_POSSIBLE_TO_ANSWER)){
            addChatBubble(UserType.SYSTEM, "由於詞庫沒有合適的答案，請回答新詞語：\n"+circulateMessage.getWord());
        }else if (m.equals(SystemMessage.LAST_WON_PLAYER_CANNOT_ANSWER_HER_WORD) || m.equals(SystemMessage.LAST_WON_PLAYER_CAN_GIVE_ANY_ANSWER)){
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_lastChar.setText("　");
                    }
                });
        }else if (m.equals(SystemMessage.EXTRA_CHANCE) || m.equals(SystemMessage.EXTRA_SKILL)){
            if (circulateMessage.getExtra().equals(SystemMessage.SCORE_ACHIEVED)){
                addChatBubble(UserType.SYSTEM, circulateMessage.getAuthor() + " 達到等級 " + circulateMessage.getPlayer().getLevel() + " ，" + m);
            }else if (circulateMessage.getExtra().equals(SystemMessage.ROUND_ACHIEVED)){
                addChatBubble(UserType.SYSTEM, circulateMessage.getAuthor() + " 目前進行了 " + circulateMessage.getPlayer().getScore() + " 回合，" + m);
            }
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //appendTextAndScroll(txt_message, m);
                addChatBubble(UserType.SYSTEM, m);
                //Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void addChatBubble(UserType userType, String message) {
        addChatBubble(userType, "", message, -1, -1, -1, -1);
    }
    @Override
    public void addChatBubble(UserType userType, String author, String message, int score, int life, int round, int iconResId) {
        BubbleDetail b = new BubbleDetail(userType, author, message, score, life, round);
        if (iconResId != -1) b.setIconResId(iconResId);
        adapter.addBubble(b);
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        bubbleList.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                bubbleList.setSelection(adapter.getCount() - 1);
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
                linear_layout_bottom_bar.setVisibility(View.VISIBLE);
                //enable buttons
                et_input.setEnabled(true);
                et_input.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                buttonSend.setEnabled(true);
                buttonAction.setEnabled(true);
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
                linear_layout_bottom_bar.setVisibility(View.GONE);
                linear_layout_queue_bar.setVisibility(View.VISIBLE);
                //
                et_input.setEnabled(false);
                buttonSend.setEnabled(false);
                buttonAbstain.setEnabled(false);
                buttonAction.setEnabled(false);
                et_input.setInputType(EditorInfo.TYPE_NULL);
                //timer & progresas bar
                if (countDownTimer != null) countDownTimer.cancel();
                pb.setProgress(0);
                //
                if (isPopupWindowClicked){
                    isPopupWindowClicked = false;
                    buttonAction.setImageResource(R.drawable.ic_open_in_browser_black_36dp);
                    buttonAction.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    linear_layout_bottom_pop_up_window.setVisibility(View.GONE);
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
        final String s = lastChar;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_lastChar.setText(s);
            }
        });
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
                        LinearLayout queue_bar_item = (LinearLayout)LayoutInflater.from(GameWindow.this).inflate(R.layout.queue_bar_item, null);
                        TextView name = (TextView)queue_bar_item.findViewById(R.id.txt_queue_player_name);
                        name.setText(p.getName());
                        //the icon...
                        ImageView imageView = (ImageView)queue_bar_item.findViewById(R.id.image_queue_player_icon);
                        imageView.setImageResource(p.getIconResId());

                        ImageView arrow = new ImageView(GameWindow.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameWindow.this);
                    LinearLayout assign_window = (LinearLayout)LayoutInflater.from(GameWindow.this).inflate(R.layout.assign_player_window, null);
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
                        imageButton[i].setImageResource(list.get(i).getIconResId());
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GameWindow.this);
                    builder.setCancelable(false);
                    builder.setNegativeButton("結束", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    LinearLayout linearLayout = (LinearLayout)LayoutInflater.from(GameWindow.this).inflate(R.layout.ranking, null);
                    for (int i = 0; i < sortedPlayerList.size(); i++){
                        Player p = sortedPlayerList.get(i);
                        LinearLayout layout = (LinearLayout)LayoutInflater.from(GameWindow.this).inflate(R.layout.rank_item, null);
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

    private class GameTextWatcher implements TextWatcher {
        String log_name = "GAMETEXTWATCHER";
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                if(!s.toString().equals("")){
                    //TODO: For 倉頡輸入法, enter ChangJie code will be regarded as text changed. Like "人" when input "他"
                    String[] code4ChangJie = {"手","田","水","口","廿","卜","山","戈","人","心","日","尸","木","火","土","竹","十","大","中","金","女","月","弓","一"};
                    boolean notCode4ChangJie = true;
                    for (String x : code4ChangJie){
                        if (s.toString().equals(x)) notCode4ChangJie = false;
                    }
                    //TODO: hide the soft keyboard the last input character is as same as the last character of the phrases, except for those ChangJie code.
                    if(notCode4ChangJie && s.toString().equals(et_lastChar.getText().toString())){
                        Log.d(log_name, "Hide soft keyboard successfully");
                        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }catch(Exception e){
                Log.d(log_name, "exception");
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("") && chance >= 2){
                if (!isButtonSendChanged2Abstain){
                    isButtonSendChanged2Abstain = true;
                    buttonSend.setImageResource(R.drawable.ic_flag_black_36dp);
                    buttonSend.setOnClickListener(onClickListener4AbstainButton);
                }
            }else{
                if (isButtonSendChanged2Abstain){
                    isButtonSendChanged2Abstain = false;
                    buttonSend.setImageResource(R.drawable.ic_send_black_36dp);
                    buttonSend.setOnClickListener(onClickListener4SendButton);
                }
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    }
}
