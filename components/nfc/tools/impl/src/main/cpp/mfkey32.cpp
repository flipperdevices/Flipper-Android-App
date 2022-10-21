#include <jni.h>
#include <string>

extern "C" {
#include "nfc-tools/mfkey32v2/crapto1/crapto1.h"
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_flipperdevices_nfc_tools_impl_bindings_MfKey32Binding_tryRecoverKey(
        JNIEnv *env,
        jclass clazz,
        jlong uid,
        jlong nt0, jlong nr0,
        jlong ar0, jlong nt1,
        jlong nr1, jlong ar1
) {
    struct Crypto1State *s, *t;
    uint64_t key;     // recovered key

    uint32_t p64 = prng_successor(nt0, 64);
    uint32_t p64b = prng_successor(nt1, 64);

    s = lfsr_recovery32(ar0 ^ p64, 0);

    bool foundedKey = false;

    for (t = s; t->odd | t->even; ++t) {
        lfsr_rollback_word(t, 0, 0);
        lfsr_rollback_word(t, nr0, 1);
        lfsr_rollback_word(t, uid ^ nt0, 0);
        crypto1_get_lfsr(t, &key);

        crypto1_word(t, uid ^ nt1, 0);
        crypto1_word(t, nr1, 1);
        if (ar1 == (crypto1_word(t, 0, 0) ^ p64b)) {
            foundedKey = true;
            break;
        }
    }
    free(s);

    if (!foundedKey) {
        return nullptr;
    }

    std::string result = std::to_string(key);
    return env->NewStringUTF(result.c_str());
}