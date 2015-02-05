package fr.scaron.sfreeboxtools.view;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import fr.scaron.sfreeboxtools.model.Base64;

/**
 * Created by gregory on 22/11/2013.
 */
public class AvatarFactory {
    Bitmap bitmap;

    public Bitmap getFromPrefs(SharedPreferences prefs) {
        String encodedImage = prefs.getString("avatar", "");
        if (!encodedImage.equalsIgnoreCase("")) {
            try {
                byte[] b = Base64.decode(encodedImage);
                bitmap = BitmapFactory.decodeByteArray(b, 0,
                        b.length);
                //bitmap = eraseBG(bitmap, -1);         // use for white background
                //bitmap = eraseBG(bitmap, -16777216);  // use for black background
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private static Bitmap eraseBG(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }
}
