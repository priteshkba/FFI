package com.ffi.Utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ffi.R;


public class CustomDialog extends Dialog {

    public CustomDialog(Context context, String header, String title, String strOk, String strCancel,
                        final ItemReturnListener listener) {
        //super(context, R.style.CustomDialog);
        super(context);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.custom_alert_dialog);

            setCancelable(false);
            setCanceledOnTouchOutside(false);

            final Button btnOk, btnCancel;
            TextView tvMessage = findViewById(R.id.cad_tvMessage);
            TextView tvtitle = findViewById(R.id.cad_tvTitle);
            btnOk = findViewById(R.id.cad_btnOk);
            btnCancel = findViewById(R.id.cad_btnCancel);

            if (header.isEmpty()) {
                tvtitle.setVisibility(View.GONE);
            } else {
                tvtitle.setText(header);
                tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tvtitle.setVisibility(View.VISIBLE);
            }


            if (strCancel != null) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }

            tvMessage.setText(title);
            btnOk.setText(strOk);
            btnCancel.setText(strCancel);

            btnOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
//                        Log.e("tagWishList", "CustomDialog btnOk.setOnClickListener called");
                        if (listener != null) {
//                            Log.e("tagWishList", "CustomDialog btnOk.setOnClickListener called listener != null ");
                            listener.returnString(btnOk.getText().toString());
                        }
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ItemReturnListener {
        void returnString(String str);
    }
}