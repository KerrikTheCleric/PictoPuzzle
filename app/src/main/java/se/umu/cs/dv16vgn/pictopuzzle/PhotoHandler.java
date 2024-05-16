package se.umu.cs.dv16vgn.pictopuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.File;
import java.io.IOException;

/**
 * <h1>PhotoHandler</h1>
 * Governs manipulations of the photo.
 *
 */

public class PhotoHandler {

    /**
     * Standard constructor.
     */

    public PhotoHandler(){

    }

    /**
     * Creates an image file.
     * @param storageDir Folder to put file in.
     * @return The file.
     * @throws IOException if the file cannot be created.
     */

    public File createImageFile(File storageDir) throws IOException {

        File image = File.createTempFile(
                "temp",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * Opens a photo and turns it into a bitmap. Rotates the
     * photo if it's in landscape form.
     * @param path The path to the photo.
     * @return The Bitmap version of the photo.
     */

    public Bitmap produceBitmapFromPath(String path){
        File file = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        if(!file.exists()){
            return null;
        }else {
            Bitmap b = BitmapFactory.decodeFile(path, bmOptions);
            if(b.getWidth() > b.getHeight()){
                return rotateBitmap(b, 90);
            }else {
                return b;
            }
        }
    }

    /**
     * Rotates a Bitmap.
     * @param source The bitmap to rotate.
     * @param angle The amount of degrees to rotate.
     * @return The rotated Bitmap.
     */

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Splits the Bitmap into a Matrix and resizes the images so they fit the screen.
     * @param bitmap
     * @param gg
     * @return
     */

    public Bitmap[][] splitAndResizeBitmap(Bitmap bitmap, GameGrid gg) {
        int columns = gg.getColumns();
        int rows = gg.getRows();
        Bitmap[][] bitmaps = new Bitmap[columns][rows];
        int width, height;

        width = bitmap.getWidth() / columns;
        height = bitmap.getHeight() / rows;

        for(int x = 0; x < columns; ++x) {
            for(int y = 0; y < rows; ++y) {
                bitmaps[x][y] = Bitmap.createBitmap(bitmap, x * width,
                        y * height, width, height);
                bitmaps[x][y] = getResizedBitmap(bitmaps[x][y],
                        gg.getTileWidth(), gg.getTileHeight());
            }
        }
        return bitmaps;
    }

    /**
     * Resizes a Bitmap.
     * @param bm The Bitmap to resize.
     * @param newWidth The new width.
     * @param newHeight The new height.
     * @return A resized Bitmap.
     */

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    /**
     * Deletes all files in the provided directory.
     * @param storageDir The directory to delete in.
     */

    public void deleteExcessPhotos(File storageDir){
        if (storageDir.isDirectory()) {
            String[] children = storageDir.list();
            for (int i = 1; i < children.length; i++) {
                new File(storageDir, children[i]).delete();
            }
        }
    }
}
