package com.ddhigh.overtime.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.ddhigh.overtime.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project android-s
 * @package com.ddhigh.overtime.activity
 * @user xialeistudio
 * @date 2016/3/11 0011
 */
@ContentView(R.layout.activity_overtime_cu)
public class OvertimeFormBaseActivity extends BaseActivity {
    @ViewInject(R.id.txtBeginAt)
    EditText txtBeginAt;
    @ViewInject(R.id.txtEndAt)
    EditText txtEndAt;
    @ViewInject(R.id.txtContent)
    EditText txtContent;
    @ViewInject(R.id.spinnerDirector)
    Spinner spinnerDirector;
    @ViewInject(R.id.checkRealInfo)
    CheckBox checkRealInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        showActionBar();
        txtBeginAt.setKeyListener(null);
        txtEndAt.setKeyListener(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuHelp:
                showHelpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(getString(R.string.help))
                //TODO:帮助内容
                .setMessage("帮助内容")
                .setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        b.create().show();
    }

    @Event(value = R.id.txtBeginAt, type = View.OnFocusChangeListener.class)
    private void onBeginAtFocusChanged(View view, boolean hasFocus) {
        Log.d("overtime-form", "beginAt focus: " + hasFocus);
    }

    @Event(value = R.id.txtEndAt, type = View.OnFocusChangeListener.class)
    private void onEndAtFocusChanged(View view, boolean hasFocus) {
        Log.d("overtime-form", "endAt focus: " + hasFocus);
    }
}
