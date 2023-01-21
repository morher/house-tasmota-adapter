package net.morher.house.tasmota.node;

import static net.morher.house.api.config.DeviceName.combine;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.morher.house.api.config.DeviceName;
import net.morher.house.api.devicetypes.LampDevice;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.entity.EntityId;
import net.morher.house.api.entity.light.LightEntity;
import net.morher.house.api.entity.switches.SwitchDefinition;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.tasmota.config.TasmotaConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.DimmerChannel;
import net.morher.house.tasmota.config.TasmotaConfiguration.LampConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.NodeConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.SwitchConfiguration;

@RequiredArgsConstructor
public class TasmotaNodeController {
  private final HouseMqttClient client;
  private final DeviceManager deviceManager;

  public void configure(TasmotaConfiguration config) {
    for (LampConfiguration lampConfiguration : config.getLamps()) {
      configure(lampConfiguration);
    }
    for (Map.Entry<String, NodeConfiguration> node : config.getNodes().entrySet()) {
      configureNode(node.getKey(), node.getValue());
    }
  }

  private void configureNode(String nodeName, NodeConfiguration config) {
    DeviceName nodeDeviceName = config.getDevice();

    for (SwitchConfiguration switchConfig : config.getSwitches()) {
      configureSwitch(nodeName, switchConfig, nodeDeviceName);
    }
  }

  private void configureSwitch(
      String nodeName, SwitchConfiguration config, DeviceName nodeDeviceName) {
    EntityId entityId =
        combine(config.getDevice(), new DeviceName(null, null, config.getEntity()), nodeDeviceName)
            .toEntityId("Power");

    Device device = deviceManager.device(entityId.getDevice());

    new TasmotaSwitchDevice(
        nodeName,
        client,
        config.getRelay(),
        device.entity(new SwitchDefinition(entityId.getEntity())));
  }

  private void configure(LampConfiguration config) {
    DeviceId deviceId = config.getDevice().toDeviceId();

    LightEntity light = deviceManager.device(deviceId).entity(LampDevice.LIGHT);

    TasmotaLampDevice lamp = new TasmotaLampDevice(config.getNode(), light, client);
    for (DimmerChannel dimmer : config.getDimmers()) {
      lamp.addDimmerChannel(dimmer.getDimmer(), dimmer.getChannel());
    }
  }
}
