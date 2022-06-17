package com.example.lksynthesizeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.why.project.poilib.PoiUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        try {
            InputStream templetDocStream = getAssets().open("test1.doc");
            String targetDocPath = this.getExternalFilesDir("poi").getPath() + File.separator + "test1234.doc";//这个目录，不需要申请存储权限
            Map<String, String> dataMap = new HashMap<String, String>();
//            dataMap.put("$writeDate$", "2022年10月14日");
//            dataMap.put("$name$", "HaiyuKing11111");
//            dataMap.put("$dept$", "移动开发组");
//            dataMap.put("$leaveType$", "☑倒休 √年假 ✔事假 ☐病假 ☐婚假 ☐产假 ☐其他");
//            dataMap.put("$leaveReason$", "倒休一天。");
//            dataMap.put("$leaveStartDate$", "2018年10月14日上午");
//            dataMap.put("$leaveEndDate$", "2018年10月14日下午");
//            dataMap.put("$leaveDay$", "1");
//            dataMap.put("$leaveLeader$", "同意");
//            dataMap.put("$leaveDeptLeaderImg$", "同意！");
            dataMap.put("$name$", "2022年10月14日");
            dataMap.put("$name1$", "HaiyuKing11111");
            dataMap.put("$name2$", "移动开发组");
            dataMap.put("$name3$", "☑倒休 √年假 ✔事假 ☐病假 ☐婚假 ☐产假 ☐其他");
            PoiUtils.writeToDoc(templetDocStream,targetDocPath,dataMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
