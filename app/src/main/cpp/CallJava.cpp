//
// Created by admin on 2018/9/18.
//

#include "CallJava.h"


CallJava::CallJava(JNIEnv *jniEnv, JavaVM *javaVM, jobject obj) {
    jenv  = jniEnv;
    jvm  = javaVM;
    //设置为全局
    jobj = jniEnv->GetObjectClass(jobj);
//    jcs = jniEnv->GetObjectClass(jobj);
    jcs = jenv->FindClass("com/example/lksynthesizeapp/MainActivity");
    jmid = jniEnv->GetMethodID(jcs, "messageMe", "(Ljava/lang/String;)V");
}

void CallJava::callJava1(bool isMainThread, float progress) {
    if (isMainThread) {
        jstring js = jenv->NewStringUTF("123");
        jenv->CallVoidMethod(jobj, jmid, js);
    } else {
        /**
        * c++子线程中调用java方法
        * 由于JniEnv是线程相关的，所以子线程中不能使用主线程的JniEnv。
        * 由于JVM是进程相关的，所以可以通过JVM获取当前线程的JniEnv，然后就可以和上面一样调用java方法了
        */
//        JNIEnv *env;
//        jvm->AttachCurrentThread(&env, 0);
        jstring js = jenv->NewStringUTF("123");
        jenv->CallVoidMethod(jobj, jmid, js);
        jvm->DetachCurrentThread();
    }
}
