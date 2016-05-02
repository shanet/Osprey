package com.shanet.osprey;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventsFragment extends DatasetFragment implements ConfirmDialogFragment.ConfirmDialogListener, NumberInputDialogFragment.NumberInputDialogListener {
  private static final int APOGEE = 0;
  private static final int MAIN = 1;

  private Integer apogeeFired;
  private Integer armed;
  private Integer mainAltitude;
  private Integer mainFired;

  private TextView apogeeFiredDisplay;
  private TextView armedDisplay;
  private TextView mainAltitudeDisplay;
  private TextView mainFiredDisplay;

  private View apogeeIndicator;
  private View mainIndicator;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.events_fragment, null);

    apogeeIndicator = layout.findViewById(R.id.apogee_indicator);
    mainIndicator = layout.findViewById(R.id.main_indicator);

    apogeeFiredDisplay = (TextView)layout.findViewById(R.id.apogee_fired_display);
    mainFiredDisplay = (TextView)layout.findViewById(R.id.main_fired_display);

    armedDisplay = (TextView)layout.findViewById(R.id.armed_display);
    mainAltitudeDisplay = (TextView)layout.findViewById(R.id.main_altitude_display);

    ((Button)layout.findViewById(R.id.fire_apogee_button)).setOnClickListener(fireApogee);
    ((Button)layout.findViewById(R.id.fire_main_button)).setOnClickListener(fireMain);

    // Tell Android this fragment has an options menu
    setHasOptionsMenu(true);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    // Show a toast if the event just now fired
    showFiredToast(apogeeFired, (Integer)dataset.getField("apogee_fired"), R.string.apogee_fired);
    showFiredToast(mainFired, (Integer)dataset.getField("main_fired"), R.string.main_fired);

    armed = (Integer)dataset.getField("armed");
    mainAltitude = mToFt((Integer)dataset.getField("main_alt"));

    apogeeFired = (Integer)dataset.getField("apogee_fired");
    mainFired = (Integer)dataset.getField("main_fired");

    updatedFiredIndicator(apogeeIndicator, apogeeFired);
    updatedFiredIndicator(mainIndicator, mainFired);

    updateDisplay(apogeeFiredDisplay, (apogeeFired.intValue() == 1 ? R.string.yes : R.string.no), R.string.default_events, R.string.apogee_fired);
    updateDisplay(mainFiredDisplay, (mainFired.intValue() == 1 ? R.string.yes : R.string.no), R.string.default_events, R.string.main_fired);

    updateDisplay(armedDisplay, (armed.intValue() == 1 ? R.string.yes : R.string.no), R.string.default_events, R.string.armed);
    updateDisplay(mainAltitudeDisplay, mainAltitude, R.string.default_events, R.string.main_altitude, R.string.feet);
  }

  private void showFiredToast(Integer current, Integer upcoming, int string) {
    if(current != null && current.intValue() == 0 && upcoming.intValue() == 1) {
      Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }
  }

  private void updatedFiredIndicator(View indicator, Integer fired) {
    int drawable;

    if(fired.intValue() == 1) {
      drawable = R.drawable.green_circle;
    } else {
      drawable = R.drawable.red_circle;
    }

    indicator.setBackgroundDrawable(getResources().getDrawable(drawable));
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_events);
  }

  // On-click methods
  // ---------------------------------------------------------------------------------------------------

  private View.OnClickListener fireApogee = new View.OnClickListener() {
    public void onClick(View view) {
      showNotArmedToast();

      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.dialog_title_apogee_fire_confirm, APOGEE);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  private View.OnClickListener fireMain = new View.OnClickListener() {
    public void onClick(View view) {
      showNotArmedToast();

      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.dialog_title_main_fire_confirm, MAIN);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  public void onConfirmation(int which) {
    sendCommand(String.format("7%d", which));
  }

  private void showNotArmedToast() {
    if(armed.intValue() != 1) {
      Toast.makeText(getActivity(), R.string.not_armed, Toast.LENGTH_SHORT).show();
    }
  }
  // ---------------------------------------------------------------------------------------------------

  // Options menu methods
  // ---------------------------------------------------------------------------------------------------
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.events_option_menu, menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.arm_igniter_option:
        sendCommand("8");
        return true;
      case R.id.disarm_igniter_option:
        sendCommand("9");
        return true;
      case R.id.set_main_altitude_option:
        // Show a dialog to get a new altitude from the user
        NumberInputDialogFragment dialog = new NumberInputDialogFragment(this, R.string.dialog_title_event_altitude, mainAltitude.intValue(), MAIN);
        dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
        return true;
    }

    return false;
  }

  public void onNumberReceived(double number, int which) {
    sendCommand(String.format("6%d%d", which, ftToM((int)number)));
  }
  // ---------------------------------------------------------------------------------------------------
}
