package com.example.lib.effect.effect.widget;

import android.widget.AbsListView;

public interface ISprintView {
    void SVScrollChanged(int l, int t, int oldl, int oldt);
    boolean SVawakenScrollBars();
    int SVgetHeaderViewsCount();
    int SVgetFooterViewsCount();
    void SVsetOnScrollListener(AbsListView.OnScrollListener l);
}
