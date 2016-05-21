package com.shanet.osprey;

import android.app.Activity;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class Main extends FragmentActivity {
  private static final int MAP_FRAGMENT = 0;
  private static final int TRACKING_FRAGMENT = 1;
  private static final int ALTITUDE_FRAGMENT = 2;
  private static final int ACCELERATION_FRAGMENT = 3;
  private static final int ORIENTATION_FRAGMENT = 4;
  private static final int EVENTS_FRAGMENT = 5;
  private static final int STATUS_FRAGMENT = 6;
  private static final int RAW_FRAGMENT = 7;

  private int curFragment;
  private HashMap<Integer, DatasetFragment> fragments;

  private Radio radio;
  private Thread recvThread;

  private Dataset dataset;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    curFragment = 0;

    try {
      radio = new Radio(this);
    } catch(IOException err) {
      DialogUtils.displayErrorDialog(this, R.string.dialog_title_radio_error, R.string.dialog_radio_init);
    }

    fragments = new HashMap<Integer, DatasetFragment>();
    fragments.put(MAP_FRAGMENT, new MapFragment());
    fragments.put(TRACKING_FRAGMENT, new TrackingFragment());
    fragments.put(ALTITUDE_FRAGMENT, new AltitudeFragment());
    fragments.put(ACCELERATION_FRAGMENT, new AccelerationFragment());
    fragments.put(ORIENTATION_FRAGMENT, new OrientationFragment());
    fragments.put(EVENTS_FRAGMENT, new EventsFragment());
    fragments.put(STATUS_FRAGMENT, new StatusFragment());
    fragments.put(RAW_FRAGMENT, new RawFragment());

    updatePagerAdapter();
  }

  protected void onPause() {
    super.onPause();

    try {
      radio.close();
    } catch(IOException err) {}

    //finish();
  }

  protected void onResume() {
    super.onResume();

    try {
      radio.open();
    } catch(Exceptions.NoUsbDriversException err) {
      DialogUtils.displayErrorDialog(this, getString(R.string.dialog_title_radio_error), err.getMessage());
      return;
    } catch(Exceptions.UnableToOpenUsbDeviceException err) {
      DialogUtils.displayErrorDialog(this, getString(R.string.dialog_title_radio_error), err.getMessage());
      return;
    }

    recvThread = new Thread(new Runnable() {
      public void run() {
        while(true) {
          try {
            final String line = radio.readLine();

            runOnUiThread(new Runnable() {
              public void run() {
                updateReceivedData(line);
              }
            });
          } catch(IOException err) {}
        }
      }
    });

    recvThread.start();
  }

  private void updateReceivedData(String line) {
    try {
      dataset = new Dataset(line);

      // Update each fragment with the new dataset
      for(DatasetFragment fragment : fragments.values()) {
        fragment.updateDataset(dataset);
      }
    } catch(JSONException err) {}
  }

  public void write(String message) {
    radio.write(message);
  }

  public void clearGraphs() {
    for(DatasetFragment fragment : fragments.values()) {
      if(fragment instanceof GraphFragment) {
        ((GraphFragment)fragment).clearGraph();
      }
    }
  }

  // View pager methods
  // ---------------------------------------------------------------------------------------------------
  private void updatePagerAdapter() {
    // Create the adapter that will return the relay and relay groups fragments
    PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    ViewPager pager = (ViewPager)findViewById(R.id.pager);
    pager.setAdapter(pagerAdapter);

    // Set the pager to the current fragment
    pager.setCurrentItem(curFragment);

    // Keep all fragments in the pager alive
    pager.setOffscreenPageLimit(fragments.size());

    // Listen for pager changes to keep track of the currently displayed fragment so the state can restored when this function is called
    pager.setOnPageChangeListener(new OnPageChangeListener() {
      public void onPageSelected(int position) {
        curFragment = position;
      }

      public void onPageScrollStateChanged(int state) {}
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    });
  }

  public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
    }

    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    public int getCount() {
      return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
      return fragments.get(position).getTitle(Main.this);
    }
  }
  // ---------------------------------------------------------------------------------------------------
}
