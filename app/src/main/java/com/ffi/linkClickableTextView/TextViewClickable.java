package com.ffi.linkClickableTextView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextViewClickable extends androidx.appcompat.widget.AppCompatTextView {
    String originalText = "";
    String copyLable="Copied!";

    public TextViewClickable(@NonNull Context context) {
        super(context);
    }

    public TextViewClickable(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewClickable(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void copyText(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(copyLable, textToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, copyLable, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(displayText(text), type);
    }

    private CharSequence displayText(CharSequence text) {
        if (text != null && text.length() > 0) {
            SpannableStringBuilder sb = new SpannableStringBuilder(text, 0, text.length());

            originalText = text.toString();

            boolean hasonlyLink=false;

            Pattern urlPattern = Patterns.WEB_URL;
            Matcher weblink = urlPattern.matcher(sb);
            while (weblink.find()) {
                if (weblink.group().equals(originalText)) {
                    setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            copyText(view.getContext(), originalText);
                            return true;
                        }
                    });
                    sb.setSpan(
                            new OnlyBlueUnderline(), weblink.start(), weblink.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
                                defaultBrowser.setData(Uri.parse(originalText));
                                view.getContext().startActivity(defaultBrowser);
                            } catch (Exception e) {

                            }
                        }
                    });
                } else {
                    sb.setSpan(
                            new UnderlinedClickableSpan(weblink.group()), weblink.start(), weblink.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }

            return sb;
        } else {
            return "";
        }
    }

}
