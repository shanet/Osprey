package com.shanet.osprey;

import org.json.JSONException;
import org.json.JSONObject;

public class Dataset {
  private JSONObject dataset;

  public Dataset(String line) throws JSONException {
    dataset = new JSONObject(line);
  }

  public Object getField(String key) {
    return dataset.opt(key);
  }

  public String getCoordinates() {
    Double latitude = (Double)getField("latitude");
    Double longitude = (Double)getField("longitude");

    // Treat coordinates of (0, 0) as 'no location'
    if(latitude == null || longitude == null || (latitude.intValue() == 0 && longitude.intValue() == 0)) {
      return null;
    }

    return String.format("%f, %f", latitude.doubleValue(), longitude.doubleValue());
  }

  public boolean wasCommandSuccessful() {
    Integer commandStatus = (Integer)getField("command_status");
    return (commandStatus.intValue() == 1);
  }

  public boolean isLogging() {
    Integer logging = (Integer)getField("logging");
    return (logging.intValue() == 1);
  }

  public String toString() {
    try {
      return dataset.toString(2);
    } catch(JSONException err) {
      // If pretty printing fails, return an unformatted string
      return dataset.toString();
    }
  }
}
