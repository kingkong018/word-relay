package com.dreamershk.projectshiritori;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.WordDictionary;
import com.dreamershk.projectshiritori.model.WordItem;
import com.dreamershk.projectshiritori.util.WiFiDirectActivity;
import com.dreamershk.projectshiritori.util.WordItemDAO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String log_name = "MAINACTIVITY";
    private String player_name = "玩家", player_icon_name = "janice";
    private int numberOfAI = 3;
    Button button_single, button_multi, button_single_with_canva,button_single_with_audio, button_setting;
    ImageButton button_voice;
    public static WordItemDAO wordItemDAO;
    private ProgressDialog loadingDialog;
    private int noOfFirstCharEntry, noOfWordEntry, maxProgress;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    //ArrayList<WordDictionary> database = new ArrayList<WordDictionary>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //wordItemDAO = new WordItemDAO(getApplicationContext());
        //wordItemDAO.deleteAllEntries();
        //noOfFirstCharEntry = wordItemDAO.getCount(WordItemDAO.TABLE_NAME_4_First_Char);
        //noOfWordEntry = wordItemDAO.getCount(WordItemDAO.TABLE_NAME_4_Word);

        loadDatabase();
        //load settings
        try {
            InputStream inputStream = getApplicationContext().openFileInput("settings.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                player_name = bufferedReader.readLine();
                player_icon_name = bufferedReader.readLine();
                numberOfAI = Integer.parseInt(bufferedReader.readLine());
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(log_name, "File not found: " + e.toString());
        }catch (IOException e) {
            Log.e(log_name, "Cannot read file: " + e.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        //UI
        button_single = (Button) findViewById(R.id.button_single_game);
        if (SinglePlayerGameWindow.isSinglePlayerGameWindowActivityRunning){
            button_single.setText(R.string.button_single_game_running);
        }else{
            button_single.setText(R.string.button_single_game_new);
        }
        button_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindow.class);
                intent.putExtra("numberOfAi", numberOfAI);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// clear all other activity in back stack
                startActivity(intent);
            }
        });

        button_multi = (Button)findViewById(R.id.button_multi_game);
        button_multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WiFiDirectActivity.class);
                startActivity(intent);
            }
        });

        button_single_with_canva = (Button)findViewById(R.id.button_single_game_with_canva);
        button_single_with_canva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindowWithCanva.class);
                intent.putExtra("numberOfAi", numberOfAI);
                intent.putExtra("player_name", player_name);
                intent.putExtra("player_icon_name", player_icon_name);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// clear all other activity in back stack
                startActivity(intent);
            }
        });
        button_single_with_audio = (Button)findViewById(R.id.button_single_game_with_audio);
        button_single_with_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindowWithAudio.class);
                intent.putExtra("numberOfAi", numberOfAI);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// clear all other activity in back stack
                startActivity(intent);
            }
        });
        button_setting = (Button)findViewById(R.id.button_setting);
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.settings, null);
                //set name
                final EditText editText_name = (EditText)linearLayout.findViewById(R.id.setting_edittxt_name);
                editText_name.setText(player_name);
                //set icon
                final CheckBox checkBox_Angus = (CheckBox)linearLayout.findViewById(R.id.setting_checkbox_angus);
                final CheckBox checkBox_Janice = (CheckBox)linearLayout.findViewById(R.id.setting_checkbox_janice);
                final CheckBox checkBox_Kelvin = (CheckBox)linearLayout.findViewById(R.id.setting_checkbox_kelvin);
                final CheckBox checkBox_Christy = (CheckBox)linearLayout.findViewById(R.id.setting_checkbox_christy);
                final CheckBox[] checkBoxes = new CheckBox[]{checkBox_Angus, checkBox_Janice, checkBox_Kelvin, checkBox_Christy};
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            for (int i =0; i < checkBoxes.length; i++){
                                checkBoxes[i].setChecked(false);
                            }
                            buttonView.setChecked(true);
                        }
                    }
                };
                for (int i =0; i < checkBoxes.length; i++){
                    checkBoxes[i].setOnCheckedChangeListener(onCheckedChangeListener);
                }
                switch(player_icon_name){
                    case "angus":
                        checkBox_Angus.setChecked(true);
                        break;
                    case "janice":
                        checkBox_Janice.setChecked(true);
                        break;
                    case "kelvin":
                        checkBox_Kelvin.setChecked(true);
                        break;
                    case "christy":
                        checkBox_Christy.setChecked(true);
                        break;
                }
                //set spinner
                final Spinner spinner = (Spinner)linearLayout.findViewById(R.id.spinner);
                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_item,new String[]{"1","2","3","4"});
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(numberOfAI-1);
                //set button
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("儲存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player_name = editText_name.getText().toString();
                        if (checkBox_Angus.isChecked()) player_icon_name = "angus";
                        else if (checkBox_Janice.isChecked()) player_icon_name = "janice";
                        else if (checkBox_Kelvin.isChecked()) player_icon_name = "kelvin";
                        else if (checkBox_Christy.isChecked()) player_icon_name = "christy";
                        numberOfAI = spinner.getSelectedItemPosition()+1;
                        String data = player_name + "\n" + player_icon_name + "\n" + numberOfAI;
                        try {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("settings.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(data);
                            outputStreamWriter.close();
                        }
                        catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }

                        dialog.dismiss();
                    }
                });
                //
                builder.setCancelable(false);
                builder.setView(linearLayout);
                builder.show();
            }
        });

        // 如果資料庫是空的，就載入資料庫。
        /*if (noOfFirstCharEntry == 0 && noOfWordEntry == 0) {
            WordDictionary[] list = new WordDictionary[wordList.size()]; //convert arraylist to array
            for (int i=0; i<wordList.size(); i++){
                list[i] = wordList.get(i);
            }
            maxProgress = wordList.size();
            new SetupDatabase().execute(list);
        }*/

    }

    private void loadDatabase(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WordDictionary> wordList = new ArrayList<WordDictionary>();
                try{
                    AssetManager am = getApplicationContext().getAssets();
                    InputStream is = null;
                    BufferedReader br = null;
                    is = am.open("database-v3.csv");
                    br = new BufferedReader((new InputStreamReader(is, "UTF-8")));
                    String line = null;
                    while((line = br.readLine()) != null){
                        //Add all data to Wordictionary.
                        WordDictionary dict = new WordDictionary();
                        String parts[] = line.split(",");
                        String firstChar = parts[0].substring(0,1);
                        dict.firstChar = firstChar;
                        for (int i = 0; i<parts.length; i++){
                            dict.list.add(parts[i]);
                        }
                        wordList.add(dict);
                    }
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                GameRulesHandler.database = wordList;
            }
        });
        t.start();
    }
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
        if (actionBar!=null)actionBar.hide();
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
        moveTaskToBack(true);
    }

    private class SetupDatabase extends AsyncTask<WordDictionary ,Integer, String>{
        protected void onPreExecute(){
            loadingDialog = new ProgressDialog(MainActivity.this);
            loadingDialog.setMessage("Loading Database. This may take a few minutes. Please do not exit the program. \n\n*Only the first launch requires database setup.");
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loadingDialog.setCancelable(false);
            loadingDialog.setInverseBackgroundForced(false);
            loadingDialog.setProgress(0);
            loadingDialog.setMax(maxProgress);
            loadingDialog.show();
        }
        protected String doInBackground(WordDictionary... wordDictionaries) {
            for (int i=0; i< wordDictionaries.length; i++) {
                wordItemDAO.processData(wordDictionaries[i]);
                publishProgress(i);
            }
            return "executed";
        }

        protected void onProgressUpdate(Integer... progress) {
            loadingDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String s) {
            if (s.equals("executed"))
                loadingDialog.dismiss();
        }
    }
    private class LoadDatabase extends AsyncTask<Void, Integer, String>{
        protected void onPreExecute() {
            loadingDialog = new ProgressDialog(MainActivity.this);
            loadingDialog.setMessage("Loading Database. This may take a few minutes. Please do not exit the program. ");
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            loadingDialog.setCancelable(false);
            loadingDialog.setInverseBackgroundForced(false);
            loadingDialog.setIndeterminate(true);
            loadingDialog.setProgress(0);
            loadingDialog.show();
        }

        protected String doInBackground(Void... voids) {
            List <WordItem> firstCharList = wordItemDAO.getAllFromFirstChar();
            //List <WordItem> wordList = wordItemDAO.getAllFromWord();
            ArrayList<WordDictionary> result = new ArrayList<>();
            for (int i=0; i<firstCharList.size(); i++){
                WordDictionary dict = new WordDictionary();
                String firstChar = firstCharList.get(0).getFirstChar();
                dict.firstChar = firstChar;
                List <WordItem> wordList  = wordItemDAO.getAllFromWord(firstChar);
                for (int j=0; j<wordList.size(); j++){
                    dict.list.add(wordList.get(j).getWord());
                }
                result.add(dict);
                publishProgress(Integer.valueOf(i));
            }
            /*
            int lastIndex = 0;
            for (int i=0; i<wordList.size(); i++){
                String word = wordList.get(i).getWord();
                String firstChar = wordList.get(i).getFirstChar();
                //Check the last accessed WordDictionary first
                //since the database insertion is according to the firstChar
                if (result.get(lastIndex).firstChar.equals(firstChar)){
                    WordDictionary dict = result.get(lastIndex);
                    dict.list.add(word);
                    result.set(lastIndex, dict);
                }else{
                    for (int k=0; k<result.size(); k++){
                        if (result.get(k).firstChar.equals(firstChar)){
                            WordDictionary dict = result.get(k);
                            dict.list.add(word);
                            result.set(k, dict);
                            lastIndex = k;
                            break;
                        }
                    }
                }
                publishProgress(Integer.valueOf(i));
            }*/
            GameRulesHandler.database = result;
            return "executed";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // TODO Auto-generated method stub
            loadingDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String result) {
            loadingDialog.dismiss();
        }
    }

}
