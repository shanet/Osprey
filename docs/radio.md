## Radio Configuration

Xbee Pro radios are used for communication between the rocket and the ground. Xbee Pros were chosen because of their increased range. Despite being more expensive, the cost is more than made up for simply by being able to recover a rocket due to the increased range. However, keep in mind that 900mHz is still line of sight. If your rocket lands on the other side of a hill, the signal will be temporarily lost. A high gain directional antenna is also highly recommended.

The radios themselves require some configuration, however, config files are provided so it's just a matter of setting two fields and uploading them to the radios.

0. Download, install, and run the [XCTU software](http://www.digi.com/products/xbee-rf-solutions/xctu-software/xctu).
0. With the radio plugged in via USB, select "Discover Radios" from the top left
0. Select the found radio and in the right column, scroll down to the "Addressing" section and copy the "SH - Serial Number High" and "SL - Serial Number Low" fields.
0. Repeat steps 1-3 for the other radio.
0. Select the found radio and then click "Profile" and select "Load configuration profile."
  * [Config for the rocket radio](/config/radio_rocket_config.xml)
  * [Config for the ground radio](/config/radio_ground_config.xml)
0. In the right column under the "Addressing" section, fill in the value from the "SH - Serial Number High" field from the opposite radio into the "DH - Destination Address High" field. Do the same for the "SL - Serial Number Low" value and the "DL - Destination Address Low" field.
0. Write the config to the radio by clicking the "Write" button.
0. Repeat steps 5-7 for the other radio.
