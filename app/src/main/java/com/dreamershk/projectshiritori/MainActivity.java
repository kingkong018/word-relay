package com.dreamershk.projectshiritori;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dreamershk.projectshiritori.model.Database;
import com.dreamershk.projectshiritori.model.WordDictionary;
import com.dreamershk.projectshiritori.model.WordItem;
import com.dreamershk.projectshiritori.util.WordItemDAO;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button_single;
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

        wordItemDAO = new WordItemDAO(getApplicationContext());
        //wordItemDAO.deleteAllEntries();
        noOfFirstCharEntry = wordItemDAO.getCount(WordItemDAO.TABLE_NAME_4_First_Char);
        noOfWordEntry = wordItemDAO.getCount(WordItemDAO.TABLE_NAME_4_Word);

        button_single = (Button) findViewById(R.id.button_single_game);
        button_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinglePlayerGameWindow.class);
                startActivity(intent);
            }
        });


        ArrayList<WordDictionary> wordList = new ArrayList<WordDictionary>();
        try{
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = null;
            BufferedReader br = null;
            is = am.open("database-v1.txt");
            br = new BufferedReader((new InputStreamReader(is, "UTF-8")));
            String line = null;
            while((line = br.readLine()) != null){
                //Add all data to Wordictionary.
                WordDictionary dict = new WordDictionary();
                String parts[] = line.split(":");
                String firstChar = parts[0];
                dict.firstChar = firstChar;
                parts = parts[1].split(",");
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
        if (noOfFirstCharEntry == 0 && noOfWordEntry == 0) {
            WordDictionary[] list = new WordDictionary[wordList.size()]; //convert arraylist to array
            for (int i=0; i<wordList.size(); i++){
                list[i] = wordList.get(i);
            }
            maxProgress = wordList.size();
            new SetupDatabase().execute(list);
        }
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
