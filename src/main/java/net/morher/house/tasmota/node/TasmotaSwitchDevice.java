package net.morher.house.tasmota.node;

import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.switches.SwitchEntity;
import net.morher.house.api.entity.switches.SwitchOptions;
import net.morher.house.api.entity.switches.SwitchStateHandler;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.client.Topic;
import net.morher.house.api.mqtt.payload.BooleanMessage;

public class TasmotaSwitchDevice {
  private final Topic<Boolean> relayTopic;

  public TasmotaSwitchDevice(String node, HouseMqttClient client, int relay, SwitchEntity entity) {
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer("Tasmota");
    new SwitchStateHandler(entity, deviceInfo, this::onSwitchState);
    entity.setOptions(new SwitchOptions());

    relayTopic = client.topic("cmnd/" + node + "/POWER" + relay, BooleanMessage.onOff());
  }

  public void onSwitchState(Boolean switchState) {
    relayTopic.publish(switchState);
  }
}
