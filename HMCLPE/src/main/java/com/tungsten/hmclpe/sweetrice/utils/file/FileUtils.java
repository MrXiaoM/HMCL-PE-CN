package com.tungsten.hmclpe.sweetrice.utils.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.widget.*;
import com.tungsten.hmclpe.sweetrice.launcher.SplashActivity;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FileUtils {
    public static void createDirectory(String path){
        if (!new File(path).exists()){
            new File(path).mkdirs();
        }
    }

    public static void createFile(String path) throws IOException {
        if (!new File(path).exists()){
            new File(path).createNewFile();
        }
    }

    public static boolean rename(String path,String newName){
        File file = new File(path);
        String newPath = path.substring(0,path.lastIndexOf("/") + 1) + newName;
        File newFile = new File(newPath);
        return file.renameTo(newFile);
    }

    public static boolean copyDirectory(String srcPath, String destPath) {
        File src = new File(srcPath);
        File dest = new File(destPath);
        if (!src.isDirectory()) {
            return false;
        }
        if (!dest.isDirectory() && !dest.mkdirs()) {
            return false;
        }
        File[] files = src.listFiles();
        for (File file : files) {
            File destFile = new File(dest, file.getName());
            if (file.isFile()) {
                if (!copyFile(file.getAbsolutePath(), destFile.getAbsolutePath())) {
                    return false;
                }
            } else if (file.isDirectory()) {
                if (!copyDirectory(file.getAbsolutePath(), destFile.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyFile(String srcPath,String destPath){
        File src = new File(srcPath);
        File dest = new File(destPath);
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(src));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] flush = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(flush)) != -1){
                outputStream.write(flush,0,len);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void copyFileWithUri(Uri uri, String destPath, Activity activity) throws IOException {
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        OutputStream outputStream = new FileOutputStream(new File(destPath));
        byte[] flush = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(flush)) != -1) {
            outputStream.write(flush, 0, len);
        }
    }

    /**
     * 多线程统计目录大小(适合特别多小文件使用)
     * 该方法仅适合有公有，私有目录
    **/
    public static long getNormalPathSize(String dir) {
        File path = new File(dir);
        //如果path不是目录则输出该文件大小
        if(!path.isDirectory()) {
            return path.length();
        }
        //是目录则开始多线程统计大小
        return IoOperateHolder.FORKJOIN_POOL.invoke(new CalDirCommand(path));
    }
    static class CalDirCommand extends RecursiveTask<Long> {
        private File folder;
        CalDirCommand(File folder){
            this.folder = folder;
        }
        @Override
        protected Long compute() {
            AtomicLong size = new AtomicLong(0);
            File[] files = folder.listFiles();
            if(files == null || files.length == 0) {
                return 0L;
            }
            List<ForkJoinTask<Long>> jobs = new ArrayList<>();
            for(File f : files) {
                if(!f.isDirectory()) {
                    size.addAndGet(f.length());
                } else {
                    jobs.add(new CalDirCommand(f));
                }
            }
            for(ForkJoinTask<Long> t : invokeAll(jobs)) {
                size.addAndGet(t.join());
            }
            return size.get();
        }
    }
    private static final class IoOperateHolder {
        final static ForkJoinPool FORKJOIN_POOL = new ForkJoinPool();
    }

    /**
     * 删除单个文件
     * @param   filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
    **/
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
    /**
     * 删除文件夹以及目录下的文件
     * @param   path 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
    **/
    public static boolean deleteDirectory(String path){
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //统计path的根目录下有多少个文件夹和文件总和
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录[递归]
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }
    //单位均为Byte
    private static long deleteFileSizeCount = 0;
    private static long pathSize;
    private static int nextProgressShare;
    private static double nowProgressShare;
    public static void deleteDirectoryOnMainThread(final String path, ProgressBar loadingProgress, TextView loadingProgressText, SplashActivity activity) {
        /**
         * 删除文件夹以及目录下的文件,该方法带进度条事件
         * @param   path 被删除目录的文件路径
         * @return 目录删除成功返回true，否则返回false
        **/
        pathSize = getNormalPathSize(path);
        //Log.d("事件3",String.valueOf(pathSize));
        if(pathSize > 0){
            loadingProgress.setMax(100);
        }
        deleteDirectory(path,loadingProgress,loadingProgressText,activity);
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private static void deleteDirectory(String path, ProgressBar loadingProgress, TextView loadingProgressText, SplashActivity activity){
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        //统计path的根目录下有多少个文件夹和文件总和
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                long thisFileSize = getNormalPathSize(files[i].getAbsolutePath());
                //删除文件
                deleteFile(files[i].getAbsolutePath());
                deleteFileSizeCount += thisFileSize;
                activity.runOnUiThread(() -> {
                    nowProgressShare = (((double)deleteFileSizeCount / pathSize) * 100);
                    if(nowProgressShare > nextProgressShare){
                        loadingProgress.setProgress((int)nowProgressShare);
                        nextProgressShare = (int)nowProgressShare + 1;
                        //Log.d("事件4", "nowProgressShare ->" + nowProgressShare + "，nextProgressShare ->" + nextProgressShare);
                    }
                    loadingProgressText.setText(String.format("%.2f",nowProgressShare) + " %");
                    //Log.d("事件5", "当前文件大小" + thisFileSize + "Byte，已累加大小" + deleteFileSizeCount + "Byte，需要删除的文件总大小" + pathSize + "Byte，当前进度删除" + nowProgressShare + " %");
                });
            } else {
                //删除目录[递归先删除里面文件最后删空目录]
                deleteDirectory(files[i].getAbsolutePath(),loadingProgress,loadingProgressText,activity);
            }
        }
        //删除当前空目录
        dirFile.delete();
    }
    /**
     * 根据提供的字符串匹配是删文件还是文件夹
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
    **/
    public boolean delete(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    public static ArrayList<File> getAllFiles(String path) {
        ArrayList<File> list = new ArrayList<>();
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return list;
        }
        if (dirFile.isFile()) {
            list.add(dirFile);
            return list;
        }
        File[] files = dirFile.listFiles();
        if(files == null){
            return list;
        }
        for (File file : files) {
            list.addAll(getAllFiles(file.toString()));
        }
        return list;
    }

    public static String getFileSha1(String path) {
        try {
            File file = new File(path);
            FileInputStream in = new FileInputStream(file);
            MessageDigest messagedigest;
            messagedigest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) >0) {
                //该对象通过使用 update()方法处理数据
                messagedigest.update(buffer, 0, len);
            }
            in.close();
            //对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest 对象被重新设置成其初始状态。
            return bytesToHex(messagedigest.digest()).toLowerCase();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 字节数组转Hex
     * @param bytes 字节数组
     * @return Hex
    **/
    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        if (bytes != null && bytes.length > 0) {
            for (int i = 0; i < bytes.length; i++) {
                String hex = byteToHex(bytes[i]);
                sb.append(hex);
            }
        }
        return sb.toString();
    }
    /**
     * Byte字节转Hex
     * @param b 字节
     * @return Hex
    **/
    private static String byteToHex(byte b) {
        String hexString = Integer.toHexString(b & 0xFF);
        //由于十六进制是由0~9、A~F来表示1~16，所以如果Byte转换成Hex后如果是<16,就会是一个字符（比如A=10），通常是使用两个字符来表示16进制位的,
        //假如一个字符的话，遇到字符串11，这到底是1个字节，还是1和1两个字节，容易混淆，如果是补0，那么1和1补充后就是0101，11就表示纯粹的11
        if (hexString.length() < 2) {
            hexString = new StringBuilder(String.valueOf(0)).append(hexString).toString();
        }
        return hexString.toUpperCase();
    }
}
