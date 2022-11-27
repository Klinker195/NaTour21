package edu.unina.natour21.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

public class NatourFileHandler {

    public NatourFileHandler() {

    }

    public String createImageFromBitmap(Context context, Bitmap bitmap) {
        String fileName;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            fileName = UUID.nameUUIDFromBytes(bytes.toByteArray()).toString();
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public void deleteFileFromFilename(Context context, String filename) {
        context.deleteFile(filename);
    }

    public Bitmap openBitmapFromFilename(Context context, String filename) {
        Bitmap bitmap = null;
        try {
            FileInputStream fi = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fi);
        } catch(Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

}
