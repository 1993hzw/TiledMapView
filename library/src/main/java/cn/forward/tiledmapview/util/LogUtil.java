/*
 * Copyright (C) 2019  Ziwei Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package cn.forward.tiledmapview.util;

import android.util.Log;

import cn.forward.tiledmapview.BuildConfig;

public class LogUtil {
    public static String LOG_DIR = "ALog";
    public static final String LOG_TAG = "log";
    public static boolean sIsLog = false;

    public static void d(String paramString) {
        d("log", paramString);
    }

    public static void d(String paramString1, String paramString2) {
        if (sIsLog) {
            Log.d(paramString1, paramString2);
        }
    }

    public static void d(String paramString1, String paramString2, Throwable paramThrowable) {
        if (sIsLog) {
            Log.d(paramString1, paramString2, paramThrowable);
        }
    }

    public static void e(String paramString) {
        e("log", paramString);
    }

    public static void e(String paramString1, String paramString2) {
        if (sIsLog) {
            Log.e(paramString1, paramString2);
        }
    }

    public static void e(String paramString1, String paramString2, Throwable paramThrowable) {
        if (sIsLog) {
            Log.e(paramString1, paramString2, paramThrowable);
        }
    }

    public static void i(String paramString) {
        i("log", paramString);
    }

    public static void i(String paramString1, String paramString2) {
        if (sIsLog) {
            Log.i(paramString1, paramString2);
        }
    }

    public static void i(String paramString1, String paramString2, Throwable paramThrowable) {
        if (sIsLog) {
            Log.i(paramString1, paramString2, paramThrowable);
        }
    }

    public static void v(String paramString) {
        v("log", paramString);
    }

    public static void v(String paramString1, String paramString2) {
        if (sIsLog) {
            Log.v(paramString1, paramString2);
        }
    }

    public static void v(String paramString1, String paramString2, Throwable paramThrowable) {
        if (sIsLog) {
            Log.v(paramString1, paramString2, paramThrowable);
        }
    }

    public static void w(String paramString) {
        w("log", paramString);
    }

    public static void w(String paramString1, String paramString2) {
        if (sIsLog) {
            Log.w(paramString1, paramString2);
        }
    }

    public static void w(String paramString1, String paramString2, Throwable paramThrowable) {
        if (sIsLog) {
            Log.w(paramString1, paramString2, paramThrowable);
        }
    }

    public static void w(String paramString, Throwable paramThrowable) {
        if (sIsLog) {
            Log.w(paramString, paramThrowable);
        }
    }
}


