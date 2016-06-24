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
        notifyDataSetChanged();
    }
    public void addBubble(UserType type, String author, String message, int life){
        BubbleDetail b = new BubbleDetail(type,author,message,life);
        addBubble(b);
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
        ViewTagOther viewTagOther;
        ViewTagSelf viewTagSelf;

        if (bubble.getUserType() == UserType.SELF) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_chat_bubble_sent, null, false);
                viewTagSelf = new ViewTagSelf();
                viewTagSelf.message = (TextView) v.findViewById(R.id.tv_chat_bubble_sent_message);
                v.setTag(viewTagSelf);
            } else {
                v = convertView;
                viewTagSelf = (ViewTagSelf)v.getTag();
            }
            viewTagSelf.message.setText(bubble.getMessage());
        } else if (bubble.getUserType() == UserType.OTHER || bubble.getUserType() == UserType.SYSTEM) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_chat_bubble_received, null, false);
                viewTagOther = new ViewTagOther();
                viewTagOther.author = (TextView) v.findViewById(R.id.tv_chat_bubble_received_author);
                viewTagOther.message = (TextView) v.findViewById(R.id.tv_chat_bubble_received_message);
                //tag.tv_life ....
                v.setTag(viewTagOther);
            } else {
                v = convertView;
                viewTagOther = (ViewTagOther)v.getTag();
            }
            viewTagOther.author.setText(bubble.getAuthor());
            viewTagOther.message.setText(bubble.getMessage());
        }
        return v;
    }

    class ViewTagOther{
        TextView author, message;
        ImageView life;
    }
    class ViewTagSelf{
        TextView message;
    }
}
