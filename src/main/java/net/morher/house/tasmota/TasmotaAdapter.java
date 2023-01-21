package net.morher.house.tasmota;

import net.morher.house.api.context.HouseMqttContext;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.tasmota.config.TasmotaAdapterConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration;
import net.morher.house.tasmota.node.TasmotaNodeController;
import net.morher.house.tasmota.sensor.TasmotaSensorController;

public class TasmotaAdapter {
  public static void main(String[] args) throws Exception {
    new TasmotaAdapter().run(new HouseMqttContext("tasmota-adapter"));
  }

  public void run(HouseMqttContext ctx) {
    HouseMqttClient client = ctx.client();
    DeviceManager deviceManager = ctx.deviceManager();
    TasmotaConfiguration config =
        ctx.loadAdapterConfig(TasmotaAdapterConfiguration.class).getTasmota();

    new TasmotaSensorController(client, deviceManager).configure(config);

    new TasmotaNodeController(client, deviceManager).configure(config);
  }
}
