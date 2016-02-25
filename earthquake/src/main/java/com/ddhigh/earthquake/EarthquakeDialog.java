package com.ddhigh.earthquake;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @project Study
 * @package com.ddhigh.earthquake
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class EarthquakeDialog extends DialogFragment {
    private static String DIALOG_STRING = "DIALOG_STRING";

    public static EarthquakeDialog newInstance(Context context, Quake quake) {
        //创建带参数的Fragment
        EarthquakeDialog fragment = new EarthquakeDialog();
        Bundle args = new Bundle();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.CHINA);
        String dateStr = sdf.format(quake.getDate());
        String quakeText = dateStr + "\n" + "Magnitude " + quake.getMagnitude() + "\n" + quake.getDefails() + "\n" + quake.getLink();

        args.putString(DIALOG_STRING, quakeText);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quake_details, container, false);

        String title = getArguments().getString(DIALOG_STRING);
        TextView tv = (TextView) view.findViewById(R.id.quakeDetailsTextView);
        tv.setText(title);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Earthquake Details");
        return dialog;
    }
}
