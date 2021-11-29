package fr.newglace.notedesnazes.Styles;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

public class Size {
    final DisplayMetrics metrics = new DisplayMetrics();
    int phoneWidth;
    int phoneHeight;
    public Size(Activity activity) {
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.phoneWidth = metrics.widthPixels;
        this.phoneHeight = metrics.heightPixels;
        Log.d("TAG", "Size: "+metrics.densityDpi + " | "+ metrics.density + " | "+  metrics.widthPixels + " | "+  metrics.heightPixels);
    }

    public int[] reSizing(View view,  String... options) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int width = params.width;
        int height = params.height;

        final int[] finalSize = PixelPercentageToExamplePhone(width, height, options);

        params.width = finalSize[0];
        params.height = finalSize[1];
        view.setLayoutParams(params);

        if (view instanceof TextView) {
            final float width2 = ((TextView) view).getTextSize();
            final float height2 = ((TextView) view).getTextSize();
            int[] finalSize2 = PixelPercentageToExamplePhone((int) width2, (int) height2);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, finalSize2[0]);
        }

        return finalSize;
    }

    private int[] PixelPercentageToExamplePhone(int width, int height, String... options) {

        int layoutHeight = 1920; // 721 dp => 1db = 2.66px
        int layoutWidth = 1080; // 406 dp => 1db = 2.66px

        // Percentage Value to Layout
        double percentageHeight = (100d / layoutHeight * height)/100d;
        double percentageWidth = (100d / layoutWidth * width)/100d;

        // Fake Width
        double percentageWidthToHeight = ((100d/layoutHeight) * layoutWidth)/100d;
        double falseWidth = this.phoneHeight * percentageWidthToHeight;

        double finalWidth = falseWidth * percentageWidth;
        double finalHeight = this.phoneHeight * percentageHeight;

        if (width == height) {
            int value = (int) Math.min(finalWidth, finalHeight);
            return new int[]{value, value};
        } else return new int[]{(int) finalWidth, (int) finalHeight};
    }
}
