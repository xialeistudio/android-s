package com.ddhigh.study.intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ddhigh.study.R;

/**
 * @project Study
 * @package com.ddhigh.study.intent
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class IntentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
    }

    public void onClick(View view){
        if(view.getId() == R.id.launch_children_activity){
            Intent intent = new Intent(this,ChildrenActivity.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            String data2 = data.getStringExtra("data");
            Log.d("111",data2);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
