package com.ddhigh.dodo.widget;

import android.app.Activity;
import android.os.Bundle;

import com.ddhigh.dodo.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.widget
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_select_image)
public class SelectImageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
