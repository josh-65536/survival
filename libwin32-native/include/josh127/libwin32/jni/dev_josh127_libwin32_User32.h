/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class dev_josh127_libwin32_User32 */

#ifndef _Included_dev_josh127_libwin32_User32
#define _Included_dev_josh127_libwin32_User32
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     dev_josh127_libwin32_User32
 * Method:    nativeInit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_dev_josh127_libwin32_User32_nativeInit
  (JNIEnv *, jclass);

/*
 * Class:     dev_josh127_libwin32_User32
 * Method:    createWindow
 * Signature: (IILjava/lang/String;)Ldev/josh127/libwin32/NativePtr;
 */
JNIEXPORT jobject JNICALL Java_dev_josh127_libwin32_User32_createWindow
  (JNIEnv *, jclass, jint, jint, jstring);

/*
 * Class:     dev_josh127_libwin32_User32
 * Method:    peekEvent
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_dev_josh127_libwin32_User32_peekEvent
  (JNIEnv *, jclass);

/*
 * Class:     dev_josh127_libwin32_User32
 * Method:    dispatchEvents
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_dev_josh127_libwin32_User32_dispatchEvents
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
