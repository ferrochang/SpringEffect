package com.example.lib.effect.effect.widget;

import android.view.View;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SEdgeEffectFactory {
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_BOTTOM = 3;

    public SEdgeEffectFactory() {
    }

    @NonNull
    protected EdgeEffect createEdgeEffect(@NonNull View view, int direction) {
        return new EdgeEffect(view.getContext());
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeDirection {
    }
}
