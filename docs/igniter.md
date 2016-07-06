## Igniter

Osprey uses a custom designed, capacitor-based igniter circuit with two channels and capable of being powered by a 3.7V lipo battery. *This is an optional component.* It is perfectly okay to simply skip this for use in a non-dual deployment or low power rocket. The microcontroller itself can fit inside a 1.65" diameter rocket which makes for a great standalone tracking system.

All assembled, the total size is 8.2cm x 2.5cm x 2.1cm and weighs in at 26g.

![](/images/igniter.jpg?raw=true)

### Hardware

* [30V 30A N-channel MOSFET x2](http://www.mouser.com/ProductDetail/NXP/PSMN022-30PL127/?qs=%2fha2pyFadugbNQputLNgHZsLYiQDmT5aklbeCjYn0GICQfJUN9XpxyX0d4VVNJgw)
* [1000uF capacitor x2](http://www.mouser.com/ProductDetail/Lelon/REA102M1ABK-1012P/?qs=%2fha2pyFaduihP%2fa%252bRFM5Yk8YA5ABm2TQkaGa5xlsTG8fpPkkF2c7%2fTZdLTc4RXtG)
* [500k Ohm resistor x2](http://www.mouser.com/ProductDetail/Vishay/RN65E5003BB14/?qs=%2fha2pyFaduidusYt8VW5IWPh4O0tbXQgGT9h5gjhx09KlAwI9RvHYA%3d%3d)
* [100k Ohm resistor x2](http://www.mouser.com/ProductDetail/Vishay-BC-Components/PR02000201003JR500/?qs=sGAEpiMZZMu61qfTUdNhG%2f4r7Iw6CIky20eXd4jNHbg%3d)
* [1k Ohm resistor x2](http://www.mouser.com/ProductDetail/Vishay-BC-Components/PR02000201001JR500/?qs=sGAEpiMZZMu61qfTUdNhG1DYDXWaU6u7M9KgRGRIM2c%3d)
* 3-position terminal block
* 4-position terminal block
* JST connector for battery
* [Magnetic switch](http://aeroconsystems.com/cart/switches/magnetic-switch-with-led-indicator/). Note: Any switch will work; I used a magnetic switch for the ability to easily turn the electronics on/off without the need to put a large access hole in the e-bay. You'll also most likely want a bigger magnet for turning the switch on/off.

### Schematic & Wiring

![](/images/igniter_schem.png?raw=true)

It's a tight fit, but it is possible to fit the igniter circuit on the other half of the proto-board used for the microcontroller and sensors as such:

![](/images/igniter_bb.png?raw=true)

The JST connector is not shown on this diagram, but it would be placed directly on the positive and negative power rails.
