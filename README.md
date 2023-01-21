# Tasmota adapter
Control devices flashed with [Tasmota](https://tasmota.github.io/).

The adapter will report sensor data from bluetooth sensors and allow MQTT controll of connected relays and PWM-dimmers.

Communication between the adapter and the ESP-chips goes through MQTT, so MQTT must be set up in Tasmota.

## Lamps
An ESP8266 or ESP32 can be used as a PWM-dimmer when [flashed with Tasmota](https://arendst.github.io/Tasmota-firmware/). To setup Tasmota for multiple separate PWM-channels run `SetOption68 1`. Fading between brightness-levels can be enabled with `Fade 1`.

A lamp can consist of multiple separate PWM channels, but all channels must be controlled by the same ESP-chip.

The chip is identified by it's `node` name, set in the topic-field in Tasmota MQTT parameters. The lamp is mapped to a House device-id. Finally the channels are added by giving the PWM-channel as `dimmer`.

### Configuration example

```yaml
tasmota:
   lamps:
    - node: living-room-wall-lamp
      device:
         room: Living room
         name: Wall lamp
      dimmers:
       - dimmer: 1
       - dimmer: 2
```

## Sensors
Sensors are fetched from all connected Tasmota-chips, no need to provide the `node`. The sensor are mapped by their name. Optionally a type can be provided. The type must be defined under `sensorTypes`.

```yaml
tasmota:
   sensors:
    - device:
         room: 'Bathroom'
         name: 'Mijia'
      name: 'ATCaabbcc'
      type: mijia

   sensorTypes:
      mijia:
         manufacturer: Xiaomi
         brand: Mijia
         temparature: true
         humidity: true
         deviceBattery: true
```
