#include <jni.h>

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class CallJava {
public:
    JNIEnv *jenv = NULL;
    JavaVM *jvm = NULL;
    jobject jobj;
    jclass jcs;
    jmethodID jmid;


public:
    CallJava(JNIEnv *jniEnv, JavaVM *javaVM, jobject jobj);

    ~CallJava();

    void callJava1(bool isMainThread, float progress);
};

