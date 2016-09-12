package com.dreamershk.projectshiritori;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dreamershk.projectshiritori.model.Player;

public class SinglePlayerGameWindowWithCanva extends GameWindowWithCanva {

    String log_name = "SINGLEGAMEWINDOWWITHCANVA";
    private SinglePlayerGameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final GameView g = this;
        final Context c = getApplicationContext();
        SinglePlayerGameManager.releaseGameManagerInstance();
        gameManager = SinglePlayerGameManager.getGameManagerInstance();
        Player player = new Player(g, "玩家");
        player.setIconResId(R.drawable.character_janice);
        player.setPlayerType(Player.TYPE_HUMAN);
        gameManager.addGameView(g, player);
        //context should be set after host player is in the player queue.
        gameManager.setContext(c);
        //set AI players
        final Intent intent = getIntent();
        int numberOfAi = intent.getExtras().getInt("numberOfAi");
        for (int i = 0; i < numberOfAi; i++){
            Player computer = new Player(null, "電腦" + (i+1));
            switch (i){
                case 0:
                    computer.setIconResId(R.drawable.character_angus);
                    break;
                case 1:
                    computer.setIconResId(R.drawable.character_kelvin);
                    break;
                case 2:
                    computer.setIconResId(R.drawable.character_christy);
                    break;
                default:
                    computer.setIconResId(R.drawable.character_janice);
            }
            computer.setPlayerType(Player.TYPE_COMPUTER);
            gameManager.addGameView(null, computer); //add computer player
        }
    }
}
