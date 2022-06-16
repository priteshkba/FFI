package com.ffi.linkClickableTextView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;



public class UnderlinedClickableSpan extends ClickableSpan {

    String text;// Keyword or url

    public UnderlinedClickableSpan(String text) {
        this.text = text;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        //adding colors

        ds.setColor(Color.BLUE);
        ds.setUnderlineText(true);
        // ds.setTypeface(Typeface.DEFAULT_BOLD);
    }


    @Override
    public void onClick(View v) {
        try {
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(Uri.parse(text));
            v.getContext().startActivity(defaultBrowser);
        } catch (Exception e) {

        }

    }

}
