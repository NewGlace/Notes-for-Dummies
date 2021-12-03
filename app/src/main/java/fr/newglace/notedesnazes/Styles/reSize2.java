package fr.newglace.notedesnazes.Styles;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class reSize2 {
    public reSize2 () {

    }
    public void reSize2(View view, int width, int height, boolean... options) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width == -1 ? params.width : width;
        params.height = height == -1 ? params.height : height;
        view.setLayoutParams(params);

        if (view instanceof TextView && options.length > 0 && options[0]) {
            if (options.length > 1) {
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height/2d));
            } else ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (height));

        }
    }
    public void reSize2(View[] view, int width, int height, boolean... options) {
        for(View v : view) {
            reSize2(v, width, height, options);
        }
    }
}