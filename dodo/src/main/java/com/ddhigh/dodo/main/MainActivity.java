package com.ddhigh.dodo.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.AuthorizeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            Intent intent = new Intent(this, AuthorizeActivity.class);
            startActivity(intent);
        }
    }
}
