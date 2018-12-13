package cn.quickits.aoide.encoder;

public class AoideSoftEncoder {

    static {
        System.loadLibrary("aoide");
    }

    public static native void mp3EncodeInit(int inSamplerate, int inChannel, int outSamplerate, int outBitrate, int quality);

    public static native int mp3EncodeWrite(short[] bufferLeft, short[] bufferRight, int count, byte[] mp3buf);

    public native static int mp3EncodeFlush(byte[] mp3buf);

    public native static void mp3EncodeClose();

}
