package com.codextech.ibtisam.bills_app.utils;

import android.net.Uri;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

public class HelpingMethods {
    public static void setFrescoImage(SimpleDraweeView imageView, String url) {
        Uri uri = Uri.parse(url);
        imageView.setImageURI(uri);
    }
}
