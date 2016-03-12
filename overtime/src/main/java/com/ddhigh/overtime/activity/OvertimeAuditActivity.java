package com.ddhigh.overtime.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class OvertimeAuditActivity extends OvertimeViewActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageCall.setVisibility(View.VISIBLE);
        imageSms.setVisibility(View.VISIBLE);
    }
}
