package com.ddhigh.testview;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnTest) {
//            ProgressFragment progressFragment = new ProgressFragment();
//            progressFragment.show(getFragmentManager(), "dialog");
            startActivity(new Intent(this, ProgressActivity.class));
        }
    }
}
