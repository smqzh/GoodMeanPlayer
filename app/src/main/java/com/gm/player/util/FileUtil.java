package com.gm.player.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.vov.vitamio.utils.Log.TAG;

public class FileUtil {



    public static final String[] VIDEO_EXTENSIONS = { "264", "3g2", "3gp","mp3",
            "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d", "aep", "ajp", "amv",
            "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs",
            "axm", "bdm", "bdmv", "bik", "bix", "bmk",  "bs4",
            "bsf",  "camre", "clpi", "cpi", "cvc", "d2v", "d3v",
            "dav", "dce", "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb",
            "dmsm", "dmss", "dnc", "dpg", "dream", "dsy", "dv", "dv-avi",
            "dv4", "dvdmedia", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt",
            "evo", "eye", "f4p", "f4v", "fbr", "fbr", "fbz", "fcp", "flc",
            "flh", "fli", "flv", "flx", "gl", "grasp", "gts", "gvi", "gvp",
            "hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf", "ivr",
            "ivs", "izz", "izzy", "jts", "lsf", "lsx", "m15", "m1pg", "m1v",
            "m21", "m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u",
            "m4v", "m75", "meta", "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv",
            "mnv", "mod", "modd", "moff", "moi", "moov", "mov", "movie",
            "mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4",
            "mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls", "mpv", "mpv2", "mqv",
            "msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve",
            "mvp", "mxf", "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx",
            "osp", "par", "pds", "pva", "pvr", "pxv", "qt", "qtch", "qtl",
            "qtm", "qtz", "rcproject", "rdb", "rec", "rm", "rmd", "rmp", "rms",
            "rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt",
            "scm", "scm", "scn", "sec", "seq", "sfvidcap", "smil", "smk", "swf", "swi",
            "swt", "tda3mt", "tivo", "tix", "tod", "tp0", "tpd", "tpr",
            "trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr", "veg",
            "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo",
            "vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp", "w32",
            "wcp", "webm", "wm", "wmd", "wmmp", "wmv", "wmx", "wp3", "wpl",
            "wtv", "wvx", "xfl", "xvid", "yuv", "zm1", "zm2", "zm3", "zmv","MP4","AVI" };

    private static final HashSet<String> vHashSet = new HashSet<String>(
            Arrays.asList(VIDEO_EXTENSIONS));

    /**
     * 检测是否是视频文件
     *
     * @param path
     * @return
     */
    public static boolean isVideo(String path) {
        path=getFileExtension(path);
        return vHashSet.contains(path);
    }


    /**
     * 检测支持office 文件格式
     * */
    public static final String[] OFFICE_EXTEND = {"doc","docx","ppt","pptx","pdf","xls","xlsx"};
    private static final HashSet<String> officeHs = new HashSet<String>(
            Arrays.asList(OFFICE_EXTEND));
    public static boolean isOffice(String path) {
        path=getFileExtension(path);
        return officeHs.contains(path);
    }


    /**
     * 检测是否是图片格式 bmp,jpg,png,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp
     * */
    public static final String[] Picture_EXTEND = {"bmp","jpg","JPG","png","PNG","BMP","tiff","gif","pcx","tga","exif","fpx","exif","fpx","svg","psd"};
    private static final HashSet<String> picList = new HashSet<String>(
            Arrays.asList(Picture_EXTEND));
    public static boolean isPicTure(String path) {
        path=getFileExtension(path);
        return picList.contains(path);
    }



    /**
     * 获取文件后缀名
     *
     * @param path
     * @return
     */
    public static String getFileExtension(String path) {
        if (null != path) {
            // 后缀点 的位置
            int dex = path.lastIndexOf(".");
            // 截取后缀名
            return path.substring(dex + 1);
        }
        return null;
    }


    public static String getFileName(String path){
        if(path!=null){
            int index=path.lastIndexOf("/");
            return path.substring(index+1);
        }
        return null;

    }


    /**
     * 修改文件读写权限
     * @param context
     * @param filePath
     */
    public static void createFlePath(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (new File(filePath).exists()) {
            return;
        }
        String[] split = filePath.split("/");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (TextUtils.isEmpty(s)) {
                continue;
            }
            if(!s.contains(".")) {
                sb.append(File.separator);
                sb.append(s);
                if (s.equals("GoodMeanPlayer") || s.equals("video") || s.equals("picture") || s.equals("office")||s.equals("log")){
                    File f = new File(sb.toString());
                    if (!f.exists()) {
                        f.mkdir();
                        Log.d(TAG, "createFlePath: -----创建文件路径" + sb.toString());
                    }
                }
            }
        }
    }

    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(context,
                context.getPackageName() + ".fileprovider",
                file);
        return fileUri;
    }



    public static String formatFileSize(long size) {
        String sFileSize = "";
        if (size > 0) {
            double dFileSize = (double) size;

            double kiloByte = dFileSize / 1024;
            if (kiloByte < 1 && kiloByte > 0) {
                return size + "Byte";
            }
            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                sFileSize = String.format("%.2f", kiloByte);
                return sFileSize + "K";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                sFileSize = String.format("%.2f", megaByte);
                return sFileSize + "M";
            }

            double teraByte = gigaByte / 1024;
            if (teraByte < 1) {
                sFileSize = String.format("%.2f", gigaByte);
                return sFileSize + "G";
            }

            sFileSize = String.format("%.2f", teraByte);
            return sFileSize + "T";
        }
        return "0K";
    }



    public static String createFile(String dir_name, String file_name) {
        String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir_path = String.format("%s/Download/%s", sdcard_path, dir_name);
        String file_path = String.format("%s/%s", dir_path, file_name);
        try {
            File dirFile = new File(dir_path);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File myFile = new File(file_path);
            myFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file_path;
    }



    public static ZipEntry getPicEntry(ZipFile file, String type, int pic_index) {
        String entry_jpeg = type + "/media/image" + pic_index + ".jpeg";
        String entry_jpg = type + "/media/image" + pic_index + ".jpg";
        String entry_png = type + "/media/image" + pic_index + ".png";
        String entry_gif = type + "/media/image" + pic_index + ".gif";
        String entry_wmf = type + "/media/image" + pic_index + ".wmf";
        ZipEntry pic_entry = null;
        pic_entry = file.getEntry(entry_jpeg);
        // 以下为读取docx的图片 转化为流数组
        if (pic_entry == null) {
            pic_entry = file.getEntry(entry_png);
        }
        if (pic_entry == null) {
            pic_entry = file.getEntry(entry_jpg);
        }
        if (pic_entry == null) {
            pic_entry = file.getEntry(entry_gif);
        }
        if (pic_entry == null) {
            pic_entry = file.getEntry(entry_wmf);
        }
        Log.d(TAG, "pic_entry name="+pic_entry.getName()+", size="+pic_entry.getSize());
        return pic_entry;
    }

    public static byte[] getPictureBytes(ZipFile file, ZipEntry pic_entry) {
        byte[] pictureBytes = null;
        try {
            InputStream pictIS = file.getInputStream(pic_entry);
            ByteArrayOutputStream pOut = new ByteArrayOutputStream();
            byte[] b = new byte[1000];
            int len = 0;
            while ((len = pictIS.read(b)) != -1) {
                pOut.write(b, 0, len);
            }
            pictIS.close();
            pOut.close();
            pictureBytes = pOut.toByteArray();
            Log.d(TAG, "pictureBytes.length=" + pictureBytes.length);
            if (pictIS != null) {
                pictIS.close();
            }
            if (pOut != null) {
                pOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pictureBytes;

    }

    public static void writePicture(String pic_path, byte[] pictureBytes) {
        File myPicture = new File(pic_path);
        try {
            FileOutputStream outputPicture = new FileOutputStream(myPicture);
            outputPicture.write(pictureBytes);
            outputPicture.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}
