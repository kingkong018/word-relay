package com.dreamershk.projectshiritori;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
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
    //UI elements
    ProgressDialog loadingDialog;
    boolean isLoadingDialogClosed;
    AlertDialog.Builder startGameAlertDialog;
    ProgressBar pb;
    EditText et_input, et_lastChar;
    ImageButton buttonSend, buttonAction, buttonAbstain;
    TextView tv_score, tv_round;
    ImageView image_life1, image_life2, image_life3;
    ListView bubbleList;
    GameBubblesAdapter adapter;
    PopupWindow popupWindow4Action;
    View popupWindowView;
    boolean isPopupWindowClicked = false;
    List<ImageView> imageArrowList;
    List<ImageView> imageIconList;
    //Game playUtility
    int maxTime = 30;
    CountDownTimer countDownTimer;
    boolean isServer, isConnected, isActive, isOver, isPlayerQueueBarInitialized;

    private GameActionListener gameActionListener;

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
        setContentView(R.layout.activity_game_window);

        //Set up UI
        //Remaining time progress bar
        pb = (ProgressBar)findViewById(R.id.pb_remaining_time);
        //Edit Text field for et_input and lastCharacter
        et_input = (EditText)findViewById(R.id.et_input);
        et_input.addTextChangedListener(new GameTextWatcher());
        et_lastChar = (EditText)findViewById(R.id.et_lastChar);
        //Sent button
        buttonSend = (ImageButton)findViewById(R.id.ib_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameActionListener.wordSubmitted(et_lastChar.getText().toString()+et_input.getText().toString());
                et_input.setText("");
                //TODO: hide the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        //Set up textfields: tv_score, tv_round
        tv_score = (TextView)findViewById(R.id.tv_score);
        tv_round = (TextView)findViewById(R.id.tv_round);
        //Set up image view for three lifes
        image_life1 = (ImageView)findViewById(R.id.image_life1);
        image_life2 = (ImageView)findViewById(R.id.image_life2);
        image_life3 = (ImageView)findViewById(R.id.image_life3);
        //Set up listview
        bubbleList = (ListView)findViewById(R.id.listview_multiplayer_bubbles);
        adapter = new GameBubblesAdapter(new ArrayList<BubbleDetail>(), this);
        bubbleList.setAdapter(adapter);
        bubbleList.setEmptyView(findViewById(R.id.emptyview_listview_multiplayer_bubbles));
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
            }
        });
        //Set action button for pop up action windwo
        buttonAction = (ImageButton)findViewById(R.id.ib_action);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isPopupWindowClicked) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getRealSize(size);
                    int[] location = new int[2];
                    findViewById(R.id.linear_layout_bottom_bar).getLocationOnScreen(location);
                    int y = location[1];
                    popupWindow4Action.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, size.y-y);
                    isPopupWindowClicked = true;
                } else {
                    popupWindow4Action.dismiss();
                    isPopupWindowClicked = false;
                }
            }
        });
        //Set abstain button
        buttonAbstain = (ImageButton)popupWindowView.findViewById(R.id.button_abstain);
        buttonAbstain.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                gameActionListener.abstain();
            }
        });

        isLoadingDialogClosed = false;
        isPlayerQueueBarInitialized = false;
        imageArrowList = new ArrayList<ImageView>();
        imageIconList = new ArrayList<ImageView>();
    }

    public void setStartGame(){
        startGameAlertDialog = new AlertDialog.Builder(GameWindow.this);
        startGameAlertDialog.setTitle("開始遊戲");
        startGameAlertDialog.setMessage("需等待最少一名玩家才可開始遊戲。");
        startGameAlertDialog.setCancelable(false);
        startGameAlertDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gameActionListener.startGame();
                isPlayerQueueBarInitialized = true; //after all player enter the room, the number of players on the bar does not change anymore.
            }
        });
        startGameAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        startGameAlertDialog.show();
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
        if (m.equals(SystemMessage.WORD_DOES_NOT_EXIST) || m.equals(SystemMessage.WORD_IS_INVALID) || m.equals(SystemMessage.WORD_REPEATED) || m.equals(SystemMessage.DRAWN)) {
            addChatBubble(UserType.SYSTEM, "系統", circulateMessage.getAuthor() + " " + m, -1);
        }else if (m.equals(SystemMessage.NOT_POSSIBLE_TO_ANSWER)){
            addChatBubble(UserType.SYSTEM, "系統", "由於詞庫沒有合適的答案，請回答新詞語：\n"+circulateMessage.getWord(), -1);
        }else if (m.equals(SystemMessage.LAST_WON_PLAYER_CANNOT_ANSWER_HER_WORD) || m.equals(SystemMessage.LAST_WON_PLAYER_CAN_GIVE_ANY_ANSWER)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_lastChar.setText("　");
                    }
                });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //appendTextAndScroll(txt_message, m);
                addChatBubble(UserType.SYSTEM, "系統", m, -1);
                //Toast.makeText(getBaseContext(), m, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void addChatBubble(UserType userType, String author, String message, int life) {
        BubbleDetail b = new BubbleDetail(userType, author, message, life);
        adapter.addBubble(b);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        bubbleList.post(new Runnable() {
            @Override
            public void run() {
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
                //enable buttons
                et_input.setEnabled(true);
                buttonSend.setEnabled(true);
                //Draw button is disabled when only 1 chance left.
                if (!image_life2.getDrawable().equals(R.drawable.ic_favorite_border_black_24dp))
                    buttonAbstain.setEnabled(true);
                et_input.setInputType(EditorInfo.TYPE_CLASS_TEXT);
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
                et_input.setEnabled(false);
                buttonSend.setEnabled(false);
                buttonAbstain.setEnabled(false);
                et_input.setInputType(EditorInfo.TYPE_NULL);
                //timer & progresas bar
                if (countDownTimer != null) countDownTimer.cancel();
                pb.setProgress(0);
                //
                if (popupWindow4Action != null && isPopupWindowClicked){
                    isPopupWindowClicked = false;
                    popupWindow4Action.dismiss();
                }
            }
        });
    }

    @Override
    public void setPlayerInfo(String name, int score, int chance, int round) {
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
    public void setPlayerQueueBar(List<Player> playerList, Player currentPlayer, Player leftPlayer) {
        //Max player limit: 6
        if (!isPlayerQueueBarInitialized){
            //final View playerQueueView = popupWindowView.findViewById(R.id.linear_layout_player_queue_bar);
            LinearLayout playerQueueLayout = (LinearLayout)popupWindowView.findViewById(R.id.linear_layout_player_queue_bar);
            for (int i = 0; i<playerList.size(); i++) {
                LinearLayout linearLayout = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setGravity(Gravity.CENTER);

                TextView textView = new TextView(this);
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                textView.setLayoutParams(layoutParams);
                textView.setText(playerList.get(i).getName());
                textView.setMaxLines(2);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                int width = size.x / 10;
                textView.setMinWidth(width);
                textView.setMaxWidth(width);

                ImageView imageView = new ImageView(this);
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageResource(R.drawable.ic_rename);
                imageIconList.add(imageView);

                linearLayout.addView(imageView);
                linearLayout.addView(textView);

                imageView = new ImageView(this);
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
                imageArrowList.add(imageView);

                playerQueueLayout.addView(imageView);
                playerQueueLayout.addView(linearLayout);
            }
        }

        for (int i = 0; i<playerList.size(); i++) {
            //set arrow
            if (currentPlayer != null){
                if (playerList.get(i).equals(currentPlayer)){
                    imageArrowList.get(i).setImageResource(R.drawable.ic_keyboard_arrow_right_white_18dp);
                }else{
                    imageArrowList.get(i).setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
                }
            }
            //when player leave....
            if (leftPlayer != null){
                if (playerList.get(i).equals(leftPlayer)){
                    imageIconList.get(i).setImageDrawable(null);
                }
            }
        }
        popupWindow4Action.update();
    }

    @Override
    public void showResult(String message){
        final String m=message;
        isOver = true;
        ArrayList<String> rank = new ArrayList<String>();
        ArrayList<Player> listOfPlayer = new ArrayList<Player>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(message.getBytes())));
            String str = null;
            while ((str = reader.readLine()) != null) {
                rank.add(str);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, "Exception: Rank cannot be made.", Toast.LENGTH_LONG).show();
        }
        if(true) {
            for (String x : rank) {
                String[] parts = x.split("\t");
                //Name,round,score
                if (parts.length == 3)
                    listOfPlayer.add(new Player(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(GameWindow.this);
        builder.setTitle("結果 (回合/分數)");
        builder.setCancelable(false);
        builder.setNegativeButton("拜拜", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setAdapter(new ListAdapter(this, listOfPlayer), null);
        if (!isFinishing())
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cleanUp();
                    builder.show();
                }
            });
    }

    class ListAdapter extends BaseAdapter {
        ArrayList<Player> list_of_player;
        LayoutInflater inflater;

        public ListAdapter(Context c, ArrayList<Player> list_of_player){
            this.list_of_player = list_of_player;
            inflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return list_of_player.size();
        }

        @Override
        public Object getItem(int position) {
            return list_of_player.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.rank_item, null);
            TextView rank = (TextView)convertView.findViewById(R.id.rank_item_rank);
            TextView name = (TextView)convertView.findViewById(R.id.rank_item_name);
            TextView round = (TextView)convertView.findViewById(R.id.rank_item_round);
            TextView score = (TextView)convertView.findViewById(R.id.rank_item_score);

            rank.setText(String.format("%d", position+1));
            name.setText(list_of_player.get(position).getName());
            score.setText(list_of_player.get(position).getScore()+"");
            round.setText(list_of_player.get(position).getRound()+"");

            return convertView;
        }
    }

    protected void cleanUp(){
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (popupWindow4Action != null  && isPopupWindowClicked)
            popupWindow4Action.dismiss();
    }

    private class GameTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try{
                if(et_input != null){
                    String str = et_input.getText().toString();
                    //TODO: For 倉頡輸入法, enter ChangJie code will be regarded as text changed. Like "人" when input "他"
                    String[] code4ChangJie = {"手","田","水","口","廿","卜","山","戈","人","心","日","尸","木","火","土","竹","十","大","中","金","女","月","弓","一"};
                    boolean notCode4ChangJie = true;
                    for (String x : code4ChangJie){
                        if (str.substring(str.length()-1).equals(x)) notCode4ChangJie = false;
                    }
                    //TODO: hide the soft keyboard the last input character is as same as the last character of the phrases, except for those ChangJie code.
                    if(notCode4ChangJie && str.substring(str.length() - 1).equals(et_lastChar.getText().toString())){
                        Log.d("TextWatch", "Hide soft keyboard successfully");
                        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }catch(Exception e){
                Log.d("TextWatch","Cannot hide soft keyboard");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    }
}
