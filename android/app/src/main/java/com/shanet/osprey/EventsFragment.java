package com.shanet.osprey;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventsFragment extends DatasetFragment implements ConfirmDialogFragment.ConfirmDialogListener, NumberInputDialogFragment.NumberInputDialogListener {
  private static final int APOGEE = 0;
  private static final int MAIN = 1;

  private TextView apogeeFiredDisplay;
  private TextView armedDisplay;
  private TextView mainAltitudeDisplay;
  private TextView mainFiredDisplay;

  private Integer mainAltitude;

  private Integer apogeeFired;
  private Integer mainFired;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.events_fragment, null);

    apogeeFiredDisplay = (TextView)layout.findViewById(R.id.apogee_fired_display);
    mainFiredDisplay = (TextView)layout.findViewById(R.id.main_fired_display);

    armedDisplay = (TextView)layout.findViewById(R.id.armed_display);

    mainAltitudeDisplay = (TextView)layout.findViewById(R.id.main_altitude_display);
    ((Button)layout.findViewById(R.id.set_main_button)).setOnClickListener(setMainAltitude);

    ((Button)layout.findViewById(R.id.arm_igniter_button)).setOnClickListener(armIgniter);
    ((Button)layout.findViewById(R.id.disarm_igniter_button)).setOnClickListener(disarmIgniter);

    ((Button)layout.findViewById(R.id.fire_apogee_button)).setOnClickListener(fireApogee);
    ((Button)layout.findViewById(R.id.fire_main_button)).setOnClickListener(fireMain);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    // Show a toast if the event just now fired
    showFiredToast(apogeeFired, (Integer)dataset.getField("apogee_fired"), R.string.apogee_fired);
    showFiredToast(mainFired, (Integer)dataset.getField("main_fired"), R.string.main_fired);

    Integer armed = (Integer)dataset.getField("armed");
    mainAltitude = mToFt((Integer)dataset.getField("main_alt"));

    apogeeFired = (Integer)dataset.getField("apogee_fired");
    mainFired = (Integer)dataset.getField("main_fired");

    updateDisplay(armedDisplay, armed, R.string.default_events, R.string.armed);
    updateDisplay(mainAltitudeDisplay, mainAltitude, R.string.default_events, R.string.main_altitude, R.string.feet);

    updateDisplay(apogeeFiredDisplay, apogeeFired, R.string.default_events, R.string.apogee_fired);
    updateDisplay(mainFiredDisplay, mainFired, R.string.default_events, R.string.main_fired);
  }

  private void showFiredToast(Integer current, Integer upcoming, int string) {
    if(current != null && current.intValue() == 0 && upcoming.intValue() == 1) {
      Toast.makeText(getActivity(), getString(string), Toast.LENGTH_SHORT).show();
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_events);
  }

  private View.OnClickListener setMainAltitude = new View.OnClickListener() {
    public void onClick(View view) {
      // Show a dialog to get a new altitude from the user
      NumberInputDialogFragment dialog = new NumberInputDialogFragment(EventsFragment.this, R.string.event_altitude_dialog_title, mainAltitude.intValue(), MAIN);
      dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
    }
  };

  public void onNumberReceived(double number, int which) {
    sendCommand(String.format("6%d%d", which, ftToM((int)number)));
  }

  private View.OnClickListener armIgniter = new View.OnClickListener() {
    public void onClick(View view) {
      sendCommand("8");
    }
  };

  private View.OnClickListener disarmIgniter = new View.OnClickListener() {
    public void onClick(View view) {
      sendCommand("9");
    }
  };

  private View.OnClickListener fireApogee = new View.OnClickListener() {
    public void onClick(View view) {
      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.apogee_fire_confirm_dialog_title, APOGEE);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  private View.OnClickListener fireMain = new View.OnClickListener() {
    public void onClick(View view) {
      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.main_fire_confirm_dialog_title, MAIN);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  public void onConfirmation(int which) {
    sendCommand(String.format("7%d", which));
  }
}
