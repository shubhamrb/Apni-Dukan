package com.mamits.apnaonlines.userv.util;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FilePathUtil {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private FilePathUtil() {
    } //private constructor to enforce Singleton pattern


    public static String getPath(final Context context, final Uri uri) {
        if (uri == null)
            return "";
        try {
            // Store to tmp file
            File mFolder = new File(context.getFilesDir()+"/Images");
            if (!mFolder.exists()) {
                mFolder.mkdirs();
            }

            ContentResolver contentResolver = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));

            File tmpFile = new File(mFolder.getAbsolutePath(), "IMG_" + System.currentTimeMillis() + "." + extension);

            Log.d("tag", "tempFile => "+tmpFile.getPath());

            FileOutputStream fos;
            try {
                InputStream inputStream = contentResolver.openInputStream(uri);
                fos = copyInputStreamToFile(inputStream, tmpFile);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmpFile.getPath();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }


    private static FileOutputStream copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {
        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return outputStream;
        }

    }
}
