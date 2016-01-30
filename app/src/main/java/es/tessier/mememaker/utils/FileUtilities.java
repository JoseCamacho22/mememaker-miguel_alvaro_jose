package es.tessier.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import es.tessier.mememaker.MemeMakerApplicationSettings;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class FileUtilities {

    private static final String TAG = FileUtilities.class.getName();
    private static final int TAM_BUFFER = 1024;
    private static final String ALBUM_NAME = "mememaker";

    public static void saveAssetImage(Context context, String assetName) {
        File fileDirectory = getFileDirectory(context);

        File fileToWrite = new File(fileDirectory, assetName);

        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = assetManager.open(assetName);
            out = context.openFileOutput(
                    fileToWrite.getName(),
                    context.MODE_PRIVATE);

            out = new FileOutputStream(fileToWrite);
            copyFile(in, out);


        } catch (FileNotFoundException e) {
            Log.e("FileUtilities", "Fichero no encontrado", e);
        } catch (IOException e) {
            Log.e(TAG, "Exception E/S", e);
        } finally {

            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                }
        }


    }

    private static File getFileDirectory(Context context) {

        MemeMakerApplicationSettings settings = new MemeMakerApplicationSettings(context);
        String storageType = settings.getStoragePreference();

        if (storageType.equals(StorageType.INTERNAL)) {
            return context.getFilesDir();
        } else {

            //que comprobará si está disponible el almacenamiento externo

            if (isExternalStorageAvailable()) {
                if (storageType.equals(StorageType.PRIVATE_EXTERNAL))
                    return context.getExternalFilesDir(null);
                else {
                    //En caso de que queramos que puedan ser accedidas por otras aplicaciones
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), ALBUM_NAME);
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory not created");
                    }
                    return file;

                }
            }
        }

        return null;
    }


    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else return false;
    }



    private static void copyFile(InputStream in, FileOutputStream out) throws IOException {

        byte[] buffer = new byte[TAM_BUFFER];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public static Uri saveImageForSharing(Context context, Bitmap bitmap, String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }


    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory = context.getFilesDir();
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File[] listFiles(Context context) {

        File fileDirectory = context.getFilesDir();
        File[] filteredFiles =
                fileDirectory.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        if(pathname.getAbsolutePath().contains("jpg")) {
                            return true;
                        }
                        else if(pathname.getAbsolutePath().contains("png")) {
                            return true;
                        }
                        else return false;

                    }
                });


        return filteredFiles;
    }


    }
