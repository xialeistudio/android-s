package com.ddhigh.dodo.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddhigh.dodo.R;

/**
 * @project Study
 * @package com.ddhigh.dodo.widget
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class IosAlertDialog extends Dialog {

    protected IosAlertDialog(Context context) {
        super(context, R.style.MyDialogStyle);
    }

    protected IosAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected IosAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String buttonText;
        private View contentView;
        private DialogInterface.OnClickListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setButton(int buttonText, DialogInterface.OnClickListener listener) {
            this.buttonText = (String) context.getText(buttonText);
            this.listener = listener;
            return this;
        }

        public Builder setButton(String buttonText, DialogInterface.OnClickListener listener) {
            this.buttonText = buttonText;
            this.listener = listener;
            return this;
        }

        public IosAlertDialog create() {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_ios_alert, null);

            final IosAlertDialog dialog = new IosAlertDialog(context);

            TextView txtTitle, txtContent;
            Button button;

            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtContent = (TextView) view.findViewById(R.id.txtContent);
            button = (Button) view.findViewById(R.id.btnOk);

            if (title != null && !title.isEmpty()) {
                txtTitle.setText(title);
            }
            if (message != null && !message.isEmpty()) {
                txtContent.setText(message);
            }
            if (buttonText != null && !buttonText.isEmpty()) {
                button.setText(buttonText);
            }

            if (listener != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            dialog.setContentView(view);
            return dialog;
        }
    }
}
