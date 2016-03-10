package com.ddhigh.mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddhigh.mylibrary.R;

/**
 *加载浮层
 */
public class LoadingDialog extends Dialog {
    ProgressBar progressBar;
    TextView textView;

    public LoadingDialog(Context context) {
        super(context,R.style.Dialog);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void setMessage(String message) {
        textView.setText(message);
    }

    public void setMessage(int resid) {
        textView.setText(resid);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }
}
