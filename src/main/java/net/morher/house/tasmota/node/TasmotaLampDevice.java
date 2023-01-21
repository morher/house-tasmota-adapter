package net.morher.house.tasmota.node;

import static net.morher.house.api.mqtt.payload.NumberMessage.integer;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.light.LightOptions;
import net.morher.house.api.entity.light.LightState;
import net.morher.house.api.entity.light.LightState.PowerState;
import net.morher.house.api.entity.light.LightStateHandler;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;

public class TasmotaLampDevice {
  private final String node;
  private final HouseMqttClient client;
  private final List<LampDimmerChannel> channels = new ArrayList<>();

  public TasmotaLampDevice(String node, LightEntity lightEntity, HouseMqttClient client) {
    this.node = node;
    this.client = client;

    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Tasmota");
    new LightStateHandler(lightEntity, deviceInfo, this::onLightState);
    lightEntity.setOptions(new LightOptions(true, null));
  }

  public void addDimmerChannel(int dimmer, String channel) {
    Topic<Integer> topic = client.topic("cmnd/" + node + "/channel" + dimmer, integer(), false);
    channels.add(new LampDimmerChannel(topic));
  }

  public void onLightState(LightState lightState) {
    channels.forEach(channel -> channel.updateState(lightState));
  }

  @RequiredArgsConstructor
  private class LampDimmerChannel {
    private final Topic<Integer> topic;

    void updateState(LightState lightState) {
      Integer brightness = toBrightness(lightState);
      if (brightness != null) {
        topic.publish(brightness);
      }
    }

    private Integer toBrightness(LightState lightState) {
      if (PowerState.OFF.equals(lightState.getState())) {
        return 0;
      }
      if (lightState.getBrightness() != null) {
        return (lightState.getBrightness() * 100) / 255;
      }
      if (PowerState.ON.equals(lightState.getState())) {
        return 100;
      }
      return null;
    }
  }
}
