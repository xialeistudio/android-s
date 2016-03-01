package com.ddhigh.dodo.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddhigh.dodo.MyApplication;
import com.ddhigh.dodo.R;
import com.ddhigh.dodo.util.DateUtil;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
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

    private void displayUser() {
        MyApplication app = (MyApplication) getActivity().getApplication();

        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(65), DensityUtil.dip2px(65))
                .setRadius(DensityUtil.dip2px(4))
                .setCrop(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
                .setLoadingDrawableId(R.drawable.img_avatar_placeholder)
                .setFailureDrawableId(R.drawable.img_avatar_placeholder)
                .build();
        x.image().bind(imageAvatar, app.user.getAvatar(), imageOptions);

        txtNickname.setText(app.user.getNickname());

        String c = DateUtil.format(app.user.getCreatedAt(), "yyyy-MM-dd") + "加入";
        txtCreatedAt.setText(c);
    }
}
