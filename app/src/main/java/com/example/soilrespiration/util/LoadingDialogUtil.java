package com.example.soilrespiration.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soilrespiration.R;

/**
 * 加载对话框控制类
 */
public class LoadingDialogUtil {

    private static Dialog loadDialog;
    private static View loadingView;

    public static void showLoadingDialog(Context context){
        if (loadDialog == null){
            prepareLoadingView(context, true, "正在为您加载，请稍后...");
            loadDialog = new Dialog(context, R.style.MyDialogStyle);
            loadDialog.setContentView(loadingView);
            loadDialog.setCancelable(false);
            loadDialog.show();
        }
    }

    public static void cancelLoading(){
        if (loadDialog != null && loadDialog.isShowing()){
            loadDialog.dismiss();
        }
    }

    private static void prepareLoadingView(final Context context, boolean showLoadingStr, String loadingStr){
        loadingView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        ImageView img = (ImageView) loadingView.findViewById(R.id.img_loading);
        AnimationDrawable anim = (AnimationDrawable) img.getDrawable();
        anim.start();

        TextView tvTitle = (TextView) loadingView.findViewById(R.id.tv_loading);
        tvTitle.setText(loadingStr);
        tvTitle.setVisibility(showLoadingStr ? View.VISIBLE : View.INVISIBLE);
    }
}
