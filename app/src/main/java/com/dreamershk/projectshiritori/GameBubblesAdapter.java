package com.dreamershk.projectshiritori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    public void addBubble(UserType type, String author, String message, int life){
        BubbleDetail b = new BubbleDetail(type,author,message,life);
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
                viewTagOther.life1 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life1);
                viewTagOther.life2 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life2);
                viewTagOther.life3 = (ImageView)v.findViewById(R.id.tv_chat_bubble_received_life3);
                v.setTag(viewTagOther);
            } else {
                v = convertView;
                viewTagOther = (ViewTagOther)v.getTag();
            }
            viewTagOther.author.setText(bubble.getAuthor());
            viewTagOther.message.setText(bubble.getMessage());
            int c = bubble.getLife();
            switch(c){
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
        TextView author, message;
        ImageView life1, life2, life3;
    }
    class ViewTagSelf{
        TextView message;
    }
    class ViewTagSystem{
        TextView author, message;
    }
}
