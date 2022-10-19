#include <jni.h>
#include <string>
#include "nfc-tools/mfkey32v2/crapto1/crapto1.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_flipperdevices_nfc_tools_impl_MfKey32Binding_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}