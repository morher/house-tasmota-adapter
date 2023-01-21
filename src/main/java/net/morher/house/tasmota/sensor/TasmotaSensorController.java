package net.morher.house.tasmota.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceId;
import net.morher.house.api.entity.DeviceManager;
import net.morher.house.api.mqtt.client.HouseMqttClient;
import net.morher.house.api.mqtt.payload.JsonMessage;
import net.morher.house.tasmota.config.TasmotaConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.SensorConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.SensorTypeConfiguration;

@Slf4j
public class TasmotaSensorController {
  private static final SensorTypeConfiguration DEFAULT_SENSOR_TYPE = new SensorTypeConfiguration();
  private final ObjectMapper mapper = new ObjectMapper();
  private final DeviceManager deviceManager;
  private Map<String, TasmotaSensorDevice> sensorsByMac = new HashMap<>();
  private Map<String, TasmotaSensorDevice> sensorsByName = new HashMap<>();

  public TasmotaSensorController(HouseMqttClient client, DeviceManager deviceManager) {
    this.deviceManager = deviceManager;
    client.topic("tele/+/SENSOR", JsonMessage.toJsonNode()).subscribe(this::onMessage);
  }

  public void configure(TasmotaConfiguration config) {
    for (SensorConfiguration sensorConfiguration : config.getSensors()) {
      SensorTypeConfiguration sensorType =
          config.getSensorTypes().getOrDefault(sensorConfiguration.getType(), DEFAULT_SENSOR_TYPE);
      configureSensor(sensorConfiguration, sensorType);
    }
  }

  private void configureSensor(
      SensorConfiguration sensorConfiguration, SensorTypeConfiguration sensorType) {
    DeviceId deviceId = sensorConfiguration.getDevice().toDeviceId();
    Device device = deviceManager.device(deviceId);

    TasmotaSensorDevice tasmotaSensor =
        new TasmotaSensorDevice(device, sensorConfiguration, sensorType);
    sensorsByMac.put(sensorConfiguration.getMac(), tasmotaSensor);
    sensorsByName.put(sensorConfiguration.getName(), tasmotaSensor);
  }

  private void onMessage(JsonNode data) {
    Iterable<Entry<String, JsonNode>> fields = () -> data.fields();
    for (Entry<String, JsonNode> field : fields) {
      String key = field.getKey();
      JsonNode value = field.getValue();
      try {
        TasmotaSensorDevice tasmotaSensor = sensorsByName.get(key);
        if (tasmotaSensor != null && value instanceof ObjectNode) {
          DeviceReport report;
          report = mapper.treeToValue(value, DeviceReport.class);
          tasmotaSensor.onReport(report);
        }
      } catch (Exception e) {
        log.warn("Failed to read report for key {} and value {}", key, value);
      }
    }
  }
}
