package com.ffi.linkClickableTextView;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.NonNull;

public class OnlyBlueUnderline extends UnderlineSpan {
    @Override
    public void updateDrawState(TextPaint ds) {
        //adding colors

        ds.setColor(Color.BLUE);
        ds.setUnderlineText(true);
        // ds.setTypeface(Typeface.DEFAULT_BOLD);
    }
}
