package com.example.lksynthesizeapp;

import android.content.res.AssetManager;
import android.view.Surface;

public class SCRFDNcnn {
    public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
    public native boolean openCamera(int facing);
    public native boolean closeCamera();
    public native boolean setOutputWindow(Surface surface);
    public native boolean callJavaBack();

    static {
        System.loadLibrary("scrfdncnn");
    }
}
