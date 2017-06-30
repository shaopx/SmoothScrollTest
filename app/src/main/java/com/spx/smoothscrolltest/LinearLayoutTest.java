package com.spx.smoothscrolltest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAOPENGXIANG on 2017/6/30.
 */

public class LinearLayoutTest extends Activity {

    private static final String TAG = "ListTest";
    Context mContext;

    private List<Item> data = new ArrayList<>();

    Handler mHandler = new Handler();


    private static String[] words = {"go", "name", "say", "hat", "what", "as", "come", "hello", "world", "food", "you", "stupid", "little", "man", "women", "test"};

    MyLinearLayout container;
    ScrollView myScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.mylist_test_layout);

        for (String word : words) {
            Item item = new Item();
            item.word = word;
            item.type = 0;
            data.add(item);
        }

        myScrollView = (ScrollView) findViewById(R.id.scrollview);
        container = (MyLinearLayout) findViewById(R.id.container);
        container.setScrollView(myScrollView);

        for (int i = 0; i < data.size(); i++) {
            Item item = data.get(i);
            final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_col_layout, container, false);
            TextView wordTv1 = (TextView) view.findViewById(R.id.word_tv);
            TextView wordTv2 = (TextView) view.findViewById(R.id.word2_tv);

            wordTv1.setText(item.word);
            wordTv2.setText(item.word);

            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked(position, view);
                }
            });

            container.addView(view);
        }

//        View footer = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_footer, container, false);
//        container.addView(footer);

        container.requestLayout();
    }

    public void onItemClicked(int position, View view){
        container.onItemClicked(position, view);
    }
}
