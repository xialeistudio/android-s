package com.ddhigh.study;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @project Study
 * @package com.ddhigh.study
 * @user xialeistudio
 * @date 2016/2/22 0022
 */
public class MyCompoundView extends LinearLayout {
    EditText editText;
    Button clearButton;

    public MyCompoundView(Context context) {
        super(context);

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getContext().getSystemService(infService);
        layoutInflater.inflate(R.layout.clearable_edit_text, this, true);

        editText = (EditText) findViewById(R.id.editText);
        clearButton = (Button) findViewById(R.id.clearButton);

        hookupButton();
    }

    private void hookupButton() {
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    public MyCompoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
