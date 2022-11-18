package com.example.lib.effect.effect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.EdgeEffect;

public class NoEdgeEffect extends EdgeEffect {
    public NoEdgeEffect(Context context) {
        super(context);
    }

    public boolean draw(Canvas canvas) {
        return false;
    }
}
