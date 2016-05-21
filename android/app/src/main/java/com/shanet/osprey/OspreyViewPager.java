package com.shanet.osprey;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.mapbox.mapboxsdk.maps.MapView;

public class OspreyViewPager extends ViewPager {
  public OspreyViewPager(Context context) {
    super(context);
  }

  public OspreyViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected boolean canScroll(View view, boolean checkView, int deltaX, int x, int y) {
    // Don't allow scrolling of the view pager if the view being scrolled on is the map view
    if(view instanceof MapView) {
      return true;
    }

    return super.canScroll(view, checkView, deltaX, x, y);
  }
}