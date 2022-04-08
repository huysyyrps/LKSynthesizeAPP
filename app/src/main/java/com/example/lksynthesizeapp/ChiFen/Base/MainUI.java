package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.lksynthesizeapp.ChiFen.Activity.LocalActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.PhotoActivity;
import com.example.lksynthesizeapp.ChiFen.Activity.VideoActivity;
import com.example.lksynthesizeapp.R;

public class MainUI {
    public void showPopupMenu(View view, LocalActivity mainActivity) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mainActivity, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.dialog, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("图片")){
                    Intent intent = new Intent(mainActivity, PhotoActivity.class);
                    mainActivity.startActivity(intent);
                }else if (item.getTitle().equals("视频")){
                    Intent intent = new Intent(mainActivity, VideoActivity.class);
                    mainActivity.startActivity(intent);
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }
}
