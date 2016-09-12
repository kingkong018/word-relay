package com.dreamershk.projectshiritori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreamershk.projectshiritori.model.BubbleDetail;
import com.dreamershk.projectshiritori.model.UserType;

import java.util.ArrayList;

/**
 * Created by Kong on 4/6/2016.
 */
public class GameBubblesAdapter extends BaseAdapter {

    public static int BUBBLE_RECEIVED = 0;
    public static int BUBBLE_SENT = 1;


    private ArrayList<BubbleDetail> bubbleDetailArrayList;
    private Context context;

    GameBubblesAdapter(ArrayList<BubbleDetail> bubbleDetailArrayList, Context context){
        this.bubbleDetailArrayList = bubbleDetailArrayList;
        this.context = context;
    }

    public void addBubble(BubbleDetail bubble){
        bubbleDetailArrayList.add(bubble);
    }
    public void addBubble(UserType type, String author, String message, int score, int life, int round){
        BubbleDetail b = new BubbleDetail(type,author,message,score, life, round);
        addBubble(b);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return bubbleDetailArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bubbleDetailArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        BubbleDetail bubble = bubbleDetailArrayList.get(position);
        ViewTagOther viewTagOther = new ViewTagOther();
        ViewTagSelf viewTagSelf = new ViewTagSelf();
        ViewTagSystem viewTagSystem = new ViewTagSystem();

        if (bubble.getUserType() == UserType.SELF) {
            if (convertView == null || !convertView.getTag().equals(viewTagSelf)) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_chat_bubble_sent, null, false);
                viewTagSelf.message = (TextView) v.findViewById(R.id.tv_chat_bubble_sent_message);
                v.setTag(viewTagSelf);
            } else {
                v = convertView;
                viewTagSelf = (ViewTagSelf)v.getTag();
            }
            viewTagSelf.message.setText(bubble.getMessage());
        } else if (bubble.getUserType() == UserType.OTHER) {
            if (convertView == null || !convertView.getTag().equals(viewTagOther) ) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_chat_bubble_received, null, false);
                viewTagOther.author = (TextView) v.findViewById(R.id.tv_chat_bubble_received_author);
                viewTagOther.message = (TextView) v.findViewById(R.id.tv_chat_bubble_received_message);
                viewTagOther.icon = (ImageView)v.findViewById(R.id.image_profile_icon);
                viewTagOther.life1 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life1);
                viewTagOther.life2 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life2);
                viewTagOther.life3 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life3);
                viewTagOther.hiddenPlayerScore = (TextView)v.findViewById(R.id.tv_hidden_player_status_score);
                viewTagOther.hiddenPlayerLife = (TextView)v.findViewById(R.id.tv_hidden_player_status_life);
                viewTagOther.hiddenPlayerRound = (TextView)v.findViewById(R.id.tv_hidden_player_status_round);
                viewTagOther.chatBubbleDetail = (RelativeLayout)v.findViewById(R.id.relative_layout_chat_bubble_received_detail);
                viewTagOther.hiddenPlayerStatus = (RelativeLayout)v.findViewById(R.id.relative_layout_chat_bubble_hidden_player_status);
                v.setTag(viewTagOther);
            } else {
                v = convertView;
                viewTagOther = (ViewTagOther)v.getTag();
            }
            viewTagOther.author.setText(bubble.getAuthor());
            viewTagOther.message.setText(bubble.getMessage());
            final int score = bubble.getScore();
            final int round = bubble.getRound();
            final int life = bubble.getLife();
            viewTagOther.hiddenPlayerScore.setText(score + "");
            viewTagOther.hiddenPlayerLife.setText(life + "");
            viewTagOther.hiddenPlayerRound.setText(round + "");
            if (viewTagOther.hiddenPlayerStatus.getVisibility() == View.VISIBLE) viewTagOther.hiddenPlayerStatus.setVisibility(View.GONE);
            //set icon
            final ViewGroup detail = viewTagOther.chatBubbleDetail;
            final ViewGroup status = viewTagOther.hiddenPlayerStatus;
            viewTagOther.icon.setImageResource(bubble.getIconResId());
            viewTagOther.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail.getVisibility() == View.VISIBLE){
                        detail.setVisibility(View.GONE);
                        status.setVisibility(View.VISIBLE);
                        /*new CountDownTimer(300, 500){
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                status.setVisibility(View.GONE);
                                detail.setVisibility(View.VISIBLE);
                            }
                        };*/
                    }else{
                        detail.setVisibility(View.VISIBLE);
                        status.setVisibility(View.GONE);
                    }
                }
            });
            //set life images.
            switch(life){
                case 0:
                    viewTagOther.life1.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    viewTagOther.life2.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    viewTagOther.life3.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    break;
                case 1:
                    viewTagOther.life1.setImageResource(R.drawable.ic_favorite_black_18dp);
                    viewTagOther.life2.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    viewTagOther.life3.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    break;
                case 2:
                    viewTagOther.life1.setImageResource(R.drawable.ic_favorite_black_18dp);
                    viewTagOther.life2.setImageResource(R.drawable.ic_favorite_black_18dp);
                    viewTagOther.life3.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                    break;
                default:
                    viewTagOther.life1.setImageResource(R.drawable.ic_favorite_black_18dp);
                    viewTagOther.life2.setImageResource(R.drawable.ic_favorite_black_18dp);
                    viewTagOther.life3.setImageResource(R.drawable.ic_favorite_black_18dp);
            }
        } else if (bubble.getUserType() == UserType.SYSTEM){
            if (convertView == null || !convertView.getTag().equals(viewTagSystem) ) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_chat_bubble_system, null, false);
                viewTagSystem.author = (TextView) v.findViewById(R.id.tv_chat_bubble_system_author);
                viewTagSystem.message = (TextView) v.findViewById(R.id.tv_chat_bubble_system_message);
                v.setTag(viewTagSystem);
            } else {
                v = convertView;
                viewTagSystem = (ViewTagSystem)v.getTag();
            }
            viewTagSystem.author.setText("系統");
            viewTagSystem.message.setText(bubble.getMessage());
        }
        return v;
    }

    class ViewTagOther{
        TextView author, message, hiddenPlayerLife, hiddenPlayerScore, hiddenPlayerRound;
        ImageView icon, life1, life2, life3;
        RelativeLayout chatBubbleDetail, hiddenPlayerStatus;
    }
    class ViewTagSelf{
        TextView message;
    }
    class ViewTagSystem{
        TextView author, message;
    }
}
