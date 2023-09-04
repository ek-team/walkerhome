package com.pharos.walker.error;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pharos.walker.application.MyApplication;
import com.pharos.walker.ui.WelcomeActivity;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @Description: 异常捕获
 * @Author: zf
 * @Time 2019/4/28
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static volatile CrashHandler instance; // 单例模式

    private Context context; // 程序Context对象
    private Thread.UncaughtExceptionHandler defalutHandler; // 系统默认的UncaughtException处理类
    private DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);

    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     *
     * @return CrashHandler
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 异常处理初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        // 获取系统默认的UncaughtException处理器
        defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        int rebootTime = SPHelper.getRebootTime();
        if (rebootTime > 2)
            return;
        // 自定义错误处理
        boolean res = handleException(ex);
        if (!res && defalutHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            defalutHandler.uncaughtException(thread, ex);

        } else {
            Log.e("程序出现异常了", "Thread = " + thread.getName() + "\nThrowable = " + ex.getMessage());
            String stackTraceInfo = getStackTraceInfo(ex);
            Log.e("stackTraceInfo", stackTraceInfo);
            saveThrowableMessage(stackTraceInfo);
            SystemClock.sleep(3000);
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            rebootTime++;
            SPHelper.saveRebootTime(rebootTime);
//            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
//                ex.printStackTrace();
//                String err = "[" + ex.getMessage() + "]";
//                Toast.makeText(context, "程序出现异常." + err, Toast.LENGTH_LONG)
//                        .show();
                Toast.makeText(context, "程序出现异常.[正在重启请稍后]", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }

        }.start();

        // 收集设备参数信息 \日志信息
        return true;
    }
    /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }

    private String logFilePath = Environment.getExternalStorageDirectory() + File.separator + "Android" +
            File.separator + "data" + File.separator + MyApplication.getInstance().getPackageName() + File.separator + "crashLog";

    public void saveThrowableMessage(String errorMessage) {
        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }
        File file = new File(logFilePath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                writeStringToFile(errorMessage, file);
            }
        } else {
            writeStringToFile(errorMessage, file);
        }
    }

    public void writeStringToFile(final String errorMessage, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outputStream = null;
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(errorMessage.getBytes());
                    outputStream = new FileOutputStream(new File(file, DateFormatUtil.getNowDate() + ".txt"));
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                    }
                    outputStream.flush();
                    Log.e("程序出异常了", "写入本地文件成功：" + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
