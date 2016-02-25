package com.ddhigh.dodo.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.AuthorizeActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewInject(R.id.btnLogin)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

    @Event(R.id.btnLogin)
    private void onBtnLoginClicked(View view) {
        Intent intent = new Intent(this, AuthorizeActivity.class);
        startActivity(intent);
    }
}
