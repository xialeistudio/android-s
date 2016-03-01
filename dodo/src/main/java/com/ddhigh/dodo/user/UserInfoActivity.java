package com.ddhigh.dodo.user;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.dodo.Config;
import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.widget.SelectImageActivity;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.user
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        displayUser();
    }

    @ViewInject(R.id.imageAvatar)
    private ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    private TextView txtNickname;
    @ViewInject(R.id.txtSex)
    private TextView txtSex;
    @ViewInject(R.id.txtEmail)
    private TextView txtEmail;
    @ViewInject(R.id.txtMobile)
    private TextView txtMobile;

    private void displayUser() {
        MyApplication app = (MyApplication) getApplication();
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(65), DensityUtil.dip2px(65))
                .setRadius(DensityUtil.dip2px(4))
                .setFadeIn(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.img_avatar_placeholder)
                .setFailureDrawableId(R.drawable.img_avatar_placeholder)
                .build();
        x.image().bind(imageAvatar, app.user.getAvatar(), imageOptions);

        txtNickname.setText(app.user.getNickname());
        txtSex.setText(app.user.getReadableSex());
        txtEmail.setText(app.user.getEmail());
        txtMobile.setText(app.user.getMobile());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Event(R.id.btnAvatar)
    private void onBtnAvatarClicked(View view) {
        //打开图片选择
        startActivityForResult(new Intent(this, SelectImageActivity.class), Config.Constants.CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.Constants.CODE_PICK_IMAGE:
                if (data != null) {
                    String path = data.getStringExtra("path");
                    if (path != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        imageAvatar.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
