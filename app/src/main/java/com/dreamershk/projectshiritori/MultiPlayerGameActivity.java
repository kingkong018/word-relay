package com.dreamershk.projectshiritori;

import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.BubbleDetail;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;


public class MultiPlayerGameActivity extends AppCompatActivity {
    //UI elements
    ProgressBar pb;
    EditText et_input, et_lastChar;
    ImageButton buttonSend, buttonAction, buttonAbstain;
    TextView tv_score, tv_round;
    ImageView image_life1, image_life2, image_life3;
    ListView bubbleList;
    GameBubblesAdapter adapter;
    PopupWindow popupWindow4Action;
    boolean isPopupWindowClicked = false;
    //Game playUtility
    private ArrayList<String> database = new ArrayList<>(); //store all the stored word.
    private ArrayList<String> history = new ArrayList<>(); //store all the users' valid et_input
    int maxTime = 30;
    Thread timerThread, aiThread;
    boolean isTimerStop = false;
    //Player
    Player player = new Player();
    protected void onStart(){
        super.onStart();
        playByComputer();
    }
    protected void onResume() {
        super.onResume();
        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
    }
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_game);
        //Remaining time progress bar
        pb = (ProgressBar)findViewById(R.id.pb_remaining_time);
        //Edit Text field for et_input and lastCharacter
        et_input = (EditText)findViewById(R.id.et_input);
        et_lastChar = (EditText)findViewById(R.id.et_lastChar);
        //Sent button
        buttonSend = (ImageButton)findViewById(R.id.ib_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(et_lastChar.getText().toString()+et_input.getText().toString());
            }
        });
        //Set up database
        database = LoadDatabase.getDatabase(getApplicationContext());
        if (database.isEmpty()){
            Toast.makeText(MultiPlayerGameActivity.this, "資料庫載入失敗", Toast.LENGTH_SHORT).show();
            finish();
        } else
            Toast.makeText(MultiPlayerGameActivity.this, "成功載入資料庫", Toast.LENGTH_SHORT).show();
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
        final View popupWindowView = LayoutInflater.from(this).inflate(R.layout.popup_window_for_game_action, null, false);
        popupWindow4Action = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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
                abstain();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_player_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Why synchornized? concurreny: isTimerStop
    private synchronized void setTimer(){
        timerThread = null;
        isTimerStop = false;
        startTimer(maxTime);
    }
    //Count down for "time", and update the time progress bar.
    //Default time is 30 seconds.
    private void startTimer(int time){
        final int maxTime = time;
        timerThread = new Thread(){
            float currentTime = 0;
            @Override
            public void run() {
                while (currentTime < maxTime && !isTimerStop){
                    try{
                        sleep(100);
                        currentTime += 0.1;
                        final int progress = (int)(currentTime/maxTime*100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setProgress(progress);
                            }
                        });
                    }catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        return; //interrupted by main thread
                    }
                    if (currentTime >= maxTime){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exceedTime();
                            }
                        });
                    }
                }
            }
        };
        timerThread.start();
    }
    private void exceedTime(){
        lifeDrop();
        updateStatusBar();
        playByComputer();
    }
    //Why synchornized? concurreny: isTimerStop
    private synchronized void pauseTimer(){
        isTimerStop = true;
        try{
            timerThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void hidePopupWindow(){
        if (isPopupWindowClicked){
            popupWindow4Action.dismiss();
        }
    }
    private void updateStatusBar(){
        //Set score
        int score = player.getScore();
        if (score>9){
            tv_score.setText(player.getScore() + "");
        }else{
            tv_score.setText("0"+player.getScore());
        }
        //set round
        tv_round.setText(player.getRound() + "");
        //set life
        int life = player.getChance();
        switch(life){
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
    }

    private void addBubble2BubbleList(UserType userType, String author, String message, int life){
        BubbleDetail b = new BubbleDetail(userType, author, message, life);
        adapter.addBubble(b);
        bubbleList.post(new Runnable() {
            @Override
            public void run() {
                bubbleList.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void updateUI4Submission(){
        //status bar
        updateStatusBar();
        //main game bubble scroll window
        addBubble2BubbleList(UserType.SELF, player.getName(), history.get(history.size()-1), player.getChance());
        //bottom bar
        et_input.setText("");
    }
    private void updateUI4AI(){
        String lastAnswer = history.get(history.size()-1);
        //main game bubble scroll window
        //99 is wordaround
        addBubble2BubbleList(UserType.SYSTEM, "系統", lastAnswer, 99);
        //bottom bar
        int j = lastAnswer.length()-1;
        String lastChar = lastAnswer.charAt(j) + "";
        et_lastChar.setText(lastChar);
    }


    //Validate and send user answer
    private void submitAnswer(String answer){
        if (answer.length() != 0) {
            boolean isAWord = GameRulesHandler.isAWord(database, answer);
            boolean isExist = GameRulesHandler.isExist(history, answer);
            boolean isValid = GameRulesHandler.isValid(history, answer);

            if (isAWord && !isExist && isValid) {
                pauseTimer();
                player.scoreIncrement(answer.length());
                player.roundIncrement();
                history.add(answer);
                // update player status
                updateUI4Submission();
                //checkAllChanceBonus();
                //computer generates the next word
                playByComputer();
            }else{
                lifeDrop();
                updateStatusBar();
                if (!isAWord)
                    Toast.makeText(MultiPlayerGameActivity.this, "我唔係叫你倉頡造字啊...", Toast.LENGTH_SHORT).show();
                if (isExist)
                    Toast.makeText(MultiPlayerGameActivity.this, "重覆啊!記憶力有無咁差啊!", Toast.LENGTH_SHORT).show();
                if (!isValid)
                    Toast.makeText(MultiPlayerGameActivity.this, "跟規距玩啦大佬..", Toast.LENGTH_SHORT).show();
                //addChatItem(current_player.getName(),"[回答錯誤]",current_player.getChance(),current_player.getScore(),current_player.getRound());
            }
            //check chance is 0 or not; if yes, game ends.
        }
        //TODO: hide the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
    private void fail(){
        lifeDrop();
        updateStatusBar();
        playByComputer();
    }
    private void abstain(){
        pauseTimer();
        lifeDrop();
        updateStatusBar();
        playByComputer();
    }
    private void playByComputer() {
        //Set up new thread to prevent over workload on main thread.
        aiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String answer = "";
                boolean isAnswerFound = false;
                int search_count = 0;
                //ramdomly search for a suitable answer
                while (search_count < database.size()) {
                    int i = (int)(Math.random() * database.size());
                    search_count++;
                    if (GameRulesHandler.isValid(history, database.get(i)) && !GameRulesHandler.isExist(history, database.get(i))){
                        answer = database.get(i);
                        isAnswerFound = true;
                        break;
                    }
                }
                //Randomly pick a valid word if no suitable answer is found.
                if (!isAnswerFound) {
                    boolean isExist = true;
                    while(isExist){
                        int i = (int)(Math.random() * database.size());
                        answer = database.get(i);
                        if (GameRulesHandler.isExist(history,answer)){
                            isExist = false;
                        }
                    }
                }
                history.add(answer);
                final String s = answer;
                final boolean showAlertDialog = !isAnswerFound;
                if (Thread.interrupted()) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showAlertDialog) {
                            new AlertDialog.Builder(MultiPlayerGameActivity.this)
                                    .setTitle("...我諗唔到")
                                    .setMessage("新詞語! - " + s)
                                    .show();
                        }
                        updateUI4AI();
                        setTimer();
                    }
                });
            }
        });
        aiThread.start();
    }
    private void lifeDrop(){
        player.chanceDecrement();
        if (checkEndGameOrNot()){
            endGame();
        }
    }
    private boolean checkEndGameOrNot(){
        if (player.getChance() > 0){
            return false;
        }else{
            return true;
        }
    }
    private void endGame(){
        hidePopupWindow(); //prevent leaked window
        //Stop all thread
        timerThread.interrupt();
        aiThread.interrupt();

        //hint.setText("你無曬機會!");
        new AlertDialog.Builder(MultiPlayerGameActivity.this)
                .setTitle("遊戲結束")
                .setMessage("無鬼用! 咁就諗唔到接咩詞語!\n 你既分數係" + player.getScore() + "分, 玩左" + player.getRound() + "個回合!\n 下次再挑戰我啦!")
                .setCancelable(false)
                .setPositiveButton("我認輸囉...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //inputDialogForEnterName();
                        finish();
                    }
                })
                .show();
    }
    public void inputDialogForEnterName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請留閣下高姓大名");
        builder.setCancelable(false);
        // Set up the et_input
        final EditText input = new EditText(this);
        // Specify the type of et_input expected; this, for example, sets the et_input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(5);
        input.setFilters(FilterArray);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText() != null){
                    player.setName(input.getText().toString());
                }else{
                    player.setName("無名氏");
                }
                //outputRankFile(current_player.getName(), current_player.getRound(), current_player.getScore());
                finish();
            }
        });
        builder.show();
    }
}
