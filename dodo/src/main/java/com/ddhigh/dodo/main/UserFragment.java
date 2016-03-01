package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.authorize.LoginFragment;
import com.ddhigh.dodo.orm.User;
import com.ddhigh.dodo.user.UserInfoActivity;
import com.ddhigh.dodo.util.BitmapUtil;
import com.ddhigh.dodo.util.DateUtil;
import com.ddhigh.dodo.widget.IosConfirmDialog;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @project Study
 * @package com.ddhigh.dodo.main
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class UserFragment extends Fragment {
    @ViewInject(R.id.imageAvatar)
    ImageView imageAvatar;
    @ViewInject(R.id.txtNickname)
    TextView txtNickname;
    @ViewInject(R.id.txtCreatedAt)
    TextView txtCreatedAt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        x.view().inject(this, v);

        //显示用户信息
        displayUser();
        return v;
    }

    /**
     * 显示用户信息
     */
    private void displayUser() {
        MyApplication app = (MyApplication) getActivity().getApplication();

        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(65), DensityUtil.dip2px(65))
                .setRadius(DensityUtil.dip2px(4))
                .setFadeIn(true)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.img_avatar_placeholder)
                .setFailureDrawableId(R.drawable.img_avatar_placeholder)
                .build();
        x.image().bind(imageAvatar, BitmapUtil.thumbQiniu(app.user.getAvatar(),"/1/w/128"), imageOptions);

        txtNickname.setText(app.user.getNickname());

        String c = DateUtil.format(app.user.getCreatedAt(), "yyyy-MM-dd") + "加入";
        txtCreatedAt.setText(c);
    }

    @Event(R.id.btnUser)
    private void onBtnUserClicked(View view) {
        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
        startActivity(intent);
    }

    @Event(R.id.btnSetting)
    private void onBtnSettingClicked(View view) {
        Log.d(MyApplication.TAG, "onBtnSettingClicked");
    }

    @Event(R.id.btnLogout)
    private void onBtnLogoutClicked(View view) {
        IosConfirmDialog.Builder builder = new IosConfirmDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("确定退出当前账号？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                });
        builder.create().show();
    }

    /**
     * 注销
     */
    private void logout() {
        MyApplication app = (MyApplication) getActivity().getApplication();
        app.user.logout(getActivity().getApplicationContext());
        app.user = new User();
        app.accessToken = new User.AccessToken();

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        String tag;
        tag = "loginFragment";
        fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new LoginFragment();
        }
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragmentContainer, fragment, tag)
                .show(fragment)
                .commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.my);
    }
}
