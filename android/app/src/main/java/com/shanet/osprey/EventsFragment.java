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
  private static final int EVENT_0 = 0;
  private static final int EVENT_1 = 1;

  private TextView event0AltitudeDisplay;
  private TextView event1AltitudeDisplay;

  private TextView event0FiredDisplay;
  private TextView event1FiredDisplay;

  private Integer event0Altitude;
  private Integer event1Altitude;

  private Integer event0Fired;
  private Integer event1Fired;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View layout = inflater.inflate(R.layout.events_fragment, null);

    event0AltitudeDisplay = (TextView)layout.findViewById(R.id.event_0_altitude_display);
    event1AltitudeDisplay = (TextView)layout.findViewById(R.id.event_1_altitude_display);

    event0FiredDisplay = (TextView)layout.findViewById(R.id.event_0_fired_display);
    event1FiredDisplay = (TextView)layout.findViewById(R.id.event_1_fired_display);

    ((Button)layout.findViewById(R.id.set_event_0_button)).setOnClickListener(setEvent0);
    ((Button)layout.findViewById(R.id.set_event_1_button)).setOnClickListener(setEvent1);

    ((Button)layout.findViewById(R.id.fire_event_0_button)).setOnClickListener(fireEvent0);
    ((Button)layout.findViewById(R.id.fire_event_1_button)).setOnClickListener(fireEvent1);

    return layout;
  }

  public void updateDataset(Dataset dataset) {
    // Don't update if not added to an activity yet
    if(!isAdded()) return;

    // Show a toast if the event just now first
    showFiredToast(event0Fired, (Integer)dataset.getField("event0_fired"), 0);
    showFiredToast(event1Fired, (Integer)dataset.getField("event1_fired"), 1);

    event0Altitude = mToFt((Integer)dataset.getField("event0_alt"));
    event1Altitude = mToFt((Integer)dataset.getField("event1_alt"));

    event0Fired = (Integer)dataset.getField("event0_fired");
    event1Fired = (Integer)dataset.getField("event1_fired");

    updateDisplay(event0AltitudeDisplay, event0Altitude, R.string.default_events, R.string.event_0_altitude, R.string.feet);
    updateDisplay(event1AltitudeDisplay, event1Altitude, R.string.default_events, R.string.event_1_altitude, R.string.feet);

    updateDisplay(event0FiredDisplay, event0Fired, R.string.default_events);
    updateDisplay(event1FiredDisplay, event1Fired, R.string.default_events);
  }

  private void showFiredToast(Integer current, Integer upcoming, int eventNum) {
    if(current != null && current.intValue() == 0 && upcoming.intValue() == 1) {
      Toast.makeText(getActivity(), String.format(getString(R.string.event_fired_toast), eventNum), Toast.LENGTH_SHORT).show();
    }
  }

  public String getTitle(Context context) {
    return context.getString(R.string.page_title_events);
  }

  private View.OnClickListener setEvent0 = new View.OnClickListener() {
    public void onClick(View view) {
      // Show a dialog to get a new altitude from the user
      NumberInputDialogFragment dialog = new NumberInputDialogFragment(EventsFragment.this, R.string.event_altitude_dialog_title, event0Altitude.intValue(), EVENT_0);
      dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
    }
  };

  private View.OnClickListener setEvent1 = new View.OnClickListener() {
    public void onClick(View view) {
      // Show a dialog to get a new altitude from the user
      NumberInputDialogFragment dialog = new NumberInputDialogFragment(EventsFragment.this, R.string.event_altitude_dialog_title, event1Altitude.intValue(), EVENT_1);
      dialog.show(getActivity().getSupportFragmentManager(), "NumberInputDialog");
    }
  };

  public void onNumberReceived(double number, int which) {
    sendCommand(String.format("6%d%d", which, ftToM((int)number)));
  }

  private View.OnClickListener fireEvent0 = new View.OnClickListener() {
    public void onClick(View view) {
      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.event_0_fire_confirm_dialog_title, EVENT_0);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  private View.OnClickListener fireEvent1 = new View.OnClickListener() {
    public void onClick(View view) {
      // Make sure the user wants to fire this event
      ConfirmDialogFragment dialog = new ConfirmDialogFragment(EventsFragment.this, R.string.event_1_fire_confirm_dialog_title, EVENT_1);
      dialog.show(getActivity().getSupportFragmentManager(), "EventFireConfirmDialog");
    }
  };

  public void onConfirmation(int which) {
    sendCommand(String.format("7%d", which));
  }
}
