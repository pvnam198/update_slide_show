package Jni;

import androidx.annotation.Keep;

/**
 * Created by 杨杰 on 2017/2/14.
 */

@Keep
public class FFmpegCmd {
    /**
     * 加载所有相关链接库
     */
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("ffmpeg-exec");
    }

    private static OnEditorListener listener;

    private static long duration;

    /**
     * 调用底层执行
     *
     * @param argc
     * @param argv
     * @return
     */
    @Keep
    public static native int exec(int argc, String[] argv);

    @Keep
    public static native void exit();

    @Keep
    public static native void term_exit();

    @Keep
    public static void onExecuted(int ret) {
        if (listener != null) {
            if (ret == 0) {
                listener.onProgress(100);
                if (listener.onSuccess())
                    listener = null;
            } else {
                listener.onFailure();
                listener = null;
            }
        }
    }

    @Keep
    public static void onProgress(float currentDuration) {
        if (listener != null) {
            listener.onProgress(currentDuration * 100 / duration);
        }
    }

    @Keep
    public static void exec(String[] cmds, OnEditorListener listener, int duration) {
        FFmpegCmd.listener = listener;
        FFmpegCmd.duration = duration;
        exec(cmds.length, cmds);
    }
}
