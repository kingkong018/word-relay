package com.dreamershk.projectshiritori;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.dreamershk.projectshiritori.model.Database;
import com.dreamershk.projectshiritori.model.WordDictionary;
import com.dreamershk.projectshiritori.model.WordItem;
import com.dreamershk.projectshiritori.util.WiFiDirectActivity;
import com.dreamershk.projectshiritori.util.WordItemDAO;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String log_name = "MAINACTIVITY";
    Button button_single, button_multi, button_single_with_canva,button_single_with_audio;
    ImageButton button_voice;
    Spinner spinner;
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

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new String[]{"1","2","3","4"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2);


        button_single = (Button) findViewById(R.id.button_single_game);
        if (SinglePlayerGameWindow.isSinglePlayerGameWindowActivityRunning){
            button_single.setText(R.string.button_single_game_running);
            spinner.setVisibility(View.GONE);
        }else{
            button_single.setText(R.string.button_single_game_new);
            spinner.setVisibility(View.VISIBLE);
        }
        button_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindow.class);
                intent.putExtra("numberOfAi", Integer.parseInt(spinner.getSelectedItem().toString()));
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
                intent.putExtra("numberOfAi", Integer.parseInt(spinner.getSelectedItem().toString()));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// clear all other activity in back stack
                startActivity(intent);
            }
        });
        button_single_with_audio = (Button)findViewById(R.id.button_single_game_with_audio);
        button_single_with_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindowWithAudio.class);
                intent.putExtra("numberOfAi", Integer.parseInt(spinner.getSelectedItem().toString()));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// clear all other activity in back stack
                startActivity(intent);
            }
        });

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

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak.");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Not supported", Toast.LENGTH_SHORT).show();
        }
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
                    String s = "";
                    for (int i = 0; i < result.size(); i++){
                        s += " " + result.get(i);
                    }
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){

                }else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){

                }else{

                }
                break;
            }

        }
    }
}
