package com.ddhigh.dodo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ddhigh.dodo.R;

/**
 * @project Study
 * @package com.ddhigh.dodo.widget
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class LoadingDialog extends Dialog {
    ProgressBar loadingProgress;
    TextView loadingText;

    public LoadingDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        init();
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null);
        loadingProgress = (ProgressBar) view.findViewById(R.id.loadingProgress);
        loadingText = (TextView) view.findViewById(R.id.loadingText);
        super.setContentView(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        loadingText.setText(title.toString());
        super.setTitle(title);
    }
}
