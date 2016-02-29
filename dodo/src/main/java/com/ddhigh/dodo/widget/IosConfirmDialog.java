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
public class IosConfirmDialog extends Dialog {

    protected IosConfirmDialog(Context context) {
        super(context, R.style.MyDialogStyle);
    }

    protected IosConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    protected IosConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String btnOkText, btnCancelText;
        private OnClickListener okListener, cancelListener;

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

        public Builder setPositiveButton(int buttonText, OnClickListener listener) {
            this.btnOkText = (String) context.getText(buttonText);
            this.okListener = listener;
            return this;
        }

        public Builder setPositiveButton(String buttonText, OnClickListener listener) {
            this.btnOkText = buttonText;
            this.okListener = listener;
            return this;
        }

        public Builder setNegativeButton(int buttonText, OnClickListener listener) {
            this.btnCancelText = (String) context.getText(buttonText);
            this.cancelListener = listener;
            return this;
        }

        public Builder setNegativeButton(String buttonText, OnClickListener listener) {
            this.btnCancelText = buttonText;
            this.cancelListener = listener;
            return this;
        }


        public IosConfirmDialog create() {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_ios_confirm, null);

            final IosConfirmDialog dialog = new IosConfirmDialog(context);

            TextView txtTitle, txtContent;
            Button buttonOk, buttonCancel;

            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtContent = (TextView) view.findViewById(R.id.txtContent);
            buttonOk = (Button) view.findViewById(R.id.btnOk);
            buttonCancel = (Button) view.findViewById(R.id.btnCancel);

            if (title != null && !title.isEmpty()) {
                txtTitle.setText(title);
            }
            if (message != null && !message.isEmpty()) {
                txtContent.setText(message);
            }
            if (btnOkText != null && !btnOkText.isEmpty()) {
                buttonOk.setText(btnOkText);
            }
            if (btnCancelText != null && !btnCancelText.isEmpty()) {
                buttonCancel.setText(btnCancelText);
            }

            if (okListener != null) {
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        okListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            } else {
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            if (cancelListener != null) {
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            } else {
                buttonCancel.setOnClickListener(new View.OnClickListener() {
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
