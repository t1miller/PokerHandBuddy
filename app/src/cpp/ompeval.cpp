
#include "omp/HandEvaluator.h"
#include "omp/EquityCalculator.h"
#include "omp/Random.h"
#include "ttest/ttest.h"
#include <iostream>
#include <unordered_set>
#include <unordered_map>
#include <vector>
#include <list>
#include <numeric>
#include <cmath>
#include <jni.h>
#include <string>
#include <android/log.h>


using namespace std;
using namespace omp;

extern "C" JNIEXPORT jstring JNICALL
Java_com_poker_pokerhandbuddy_evalnative_EvalNativeBridge_testJacksJNI(
        JNIEnv* env,
        jobject /* this */) {


    std::string hello = "Hello from C++";


    __android_log_print(ANDROID_LOG_ERROR, "test","testJacksJni()");

    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jobject JNICALL
Java_com_poker_pokerhandbuddy_evalnative_EvalNativeBridge_evaluateJNI(
        JNIEnv *env, jobject javaThis, jdouble stopTime, jint threads, jdouble stdError, jstring boardS, jstring deadS, jobjectArray playersCards)
{

    const char *boardString = env->GetStringUTFChars(boardS, 0);
    const char *deadString = env->GetStringUTFChars(deadS, 0);
    int playerCount = env->GetArrayLength(playersCards);
    vector<CardRange> ranges;
    vector<string> rangesStrings;
    vector<jstring> rangesJStrings;

    for (int i=0; i<playerCount; i++) {
        // get players cards
        jstring playerString = (jstring)env->GetObjectArrayElement(playersCards, i);
        const char *rawString = env->GetStringUTFChars(playerString, 0);
        rangesStrings.push_back(rawString);
        rangesJStrings.push_back(playerString);
        ranges.push_back(CardRange(rawString));
    }

    EquityCalculator eq;
    uint64_t board = CardRange::getCardMask(boardString);
    uint64_t dead = CardRange::getCardMask(deadString);


   auto callback = [&](const EquityCalculator::Results& results) {
        if (results.time > stopTime) // Stop after 5s
            eq.stop();

       __android_log_print(ANDROID_LOG_ERROR, "", "time = %f stopTime = %f \n",results.time, stopTime);
    };

    double updateInterval = 0.25; // Callback called every 0.25s.
    eq.start(ranges, board, dead, false, stdError, callback, updateInterval, threads);
    eq.wait();
    auto r2 = eq.getResults();


    // Get the class we wish to return an instance of
    jclass clazz = env->FindClass("com/poker/pokerhandbuddy/evalnative/EvalResult");
    jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");
    jobject obj = env->NewObject(clazz, constructor);

    // Get Field references
    jfieldID param1Field = env->GetFieldID(clazz, "time", "D");
    jfieldID param2Field = env->GetFieldID(clazz, "hands", "I");
    jfieldID param3Field = env->GetFieldID(clazz, "speed", "D");
    jfieldID param4Field = env->GetFieldID(clazz, "std", "D");
    jfieldID param5Field = env->GetFieldID(clazz, "equity", "[D");
    jfieldID param6Field = env->GetFieldID(clazz, "win", "[D");
    jfieldID param7Field = env->GetFieldID(clazz, "tie", "[D");

    // Set fields for object
    env->SetDoubleField(obj, param1Field, r2.time);
    env->SetIntField(obj, param2Field, (int)r2.hands);
    env->SetDoubleField(obj, param3Field, r2.speed);
    env->SetDoubleField(obj, param4Field, r2.stdev);

    jdoubleArray equity = env->NewDoubleArray(playerCount);
    jdoubleArray win = env->NewDoubleArray(playerCount);
    jdoubleArray tie = env->NewDoubleArray(playerCount);

    env->SetDoubleArrayRegion(equity,0,playerCount,&r2.equity[0]);
    env->SetDoubleArrayRegion(win,0,playerCount,&r2.wins[0]);
    env->SetDoubleArrayRegion(tie,0,playerCount,&r2.ties[0]);

    env->SetObjectField(obj,param5Field, (jobject)equity);
    env->SetObjectField(obj,param6Field, (jobject)win);
    env->SetObjectField(obj,param7Field, (jobject)tie);


    // todo todo todo release stuff
//    env->ReleaseStringUTFChars(boardS, boardString);
//    env->ReleaseStringUTFChars(deadS, deadString);
//    for (int i=0; i<playerCount; i++) {
//        env->ReleaseStringUTFChars(rangesJStrings[i],rangesStrings[i].c_str());
//    }

    // return object
    return obj;
}


//void* jniOnprogress(void *pgl){
//    if(pgl == NULL){
//        return NULL;
//    }
//
//    JNIEnv *pgl_thread_env = NULL;
//    int env_state = jvm->GetEnv((void **)&pgl_thread_env,JNI_VERSION_1_6);
//
//    /*if(pgl_thread_env == NULL){
//        LOGD("pgl_thread_env init fail 1_6...");
//        env_state = jvm->GetEnv((void **)&pgl_thread_env,JNI_VERSION_1_4);
//    }*/
//
//    //The current thread has no Attach
//    if(env_state == JNI_EDETACHED){
//        ("cur thread not attach,do attach...");
//        //TODO:attach cur thread
//        if(jvm->AttachCurrentThread(&pgl_thread_env,NULL) != JNI_OK){
//            return NULL;
//        }
//        mAttached = JNI_TRUE;
//    }
//
//    jobject callback = (jobject)pgl;
//    //LOGD("cur thread not attach...env_state = %d",env_state);
//    if(pgl_thread_env){
//        jclass clz = pgl_thread_env->GetObjectClass(callback);
//        if(clz == NULL){
//            __android_log_print(ANDROID_LOG_ERROR,"cannot find cls...");
//            jvm->DetachCurrentThread();
//            return NULL;
//        }
//        jmethodID methodid = pgl_thread_env->GetMethodID(clz,"onProgress","(I)V");
//        if(methodid == NULL){
//            __android_log_print(ANDROID_LOG_ERROR,"cannot find method onProgress");
//            jvm->DetachCurrentThread();
//            return NULL;
//        }
//        pgl_thread_env->CallVoidMethod(callback,methodid,99);
//    }
//
//    //TODO:1, release the global reference 2, release the env 3 of the current thread, and make the global reference empty
//    //Release global references
//    pgl_thread_env->DeleteGlobalRef(callback);
//    //Release the env of the current thread
//
//    if(mAttached){
//        jvm->DetachCurrentThread();
//    }
//    __android_log_print(ANDROID_LOG_ERROR, "after DetachCurrentThread...");
//    pgl_thread_env = NULL;
//    callback = NULL;
//    return NULL;
//}
//
