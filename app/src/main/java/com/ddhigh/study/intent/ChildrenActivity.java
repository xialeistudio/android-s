package com.ddhigh.study.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * @project Study
 * @package com.ddhigh.study.intent
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class ChildrenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("!!!");
        setContentView(textView);
    }
}
