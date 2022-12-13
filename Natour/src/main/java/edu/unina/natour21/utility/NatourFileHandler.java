package edu.unina.natour21.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

public class NatourFileHandler {

    public NatourFileHandler() {

    }

    /**
     * Creates an image file from a Bitmap object given a Context.
     * @param context Context is used to open a file output stream.
     * @param bitmap Bitmap image that is going to be written in file.
     * @return A String object containing the created file name.
     */
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

    /**
     * Deletes a file using its name given a Context.
     * @param context Context which contains the file to delete.
     * @param filename File name used to identify the file to delete.
     */
    public void deleteFileFromFilename(Context context, String filename) {
        context.deleteFile(filename);
    }

    /**
     * Reads a file object as Bitmap given a Context.
     * @param context Context which contains the file to open.
     * @param filename File name used to identify the file to open.
     * @return A Bitmap object containing the data fetched from a file.
     */
    public Bitmap openBitmapFromFilename(Context context, String filename) {
        Bitmap bitmap = null;
        try {
            FileInputStream fi = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fi);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

}
