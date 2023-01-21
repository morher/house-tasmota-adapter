package net.morher.house.tasmota.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import net.morher.house.api.config.DeviceName;

@Data
public class TasmotaConfiguration {
  private Map<String, NodeConfiguration> nodes = new HashMap<>();
  private List<SwitchConfiguration> switches = new ArrayList<>();
  private List<LampConfiguration> lamps = new ArrayList<>();
  private List<SensorConfiguration> sensors = new ArrayList<>();
  private Map<String, SensorTypeConfiguration> sensorTypes = new HashMap<>();

  @Data
  public static class NodeConfiguration {
    private DeviceName device;
    private List<SwitchConfiguration> switches = new ArrayList<>();
  }

  @Data
  public static class SwitchConfiguration {
    private String node;
    private DeviceName device;
    private String entity;
    private int relay = 1;
  }

  @Data
  public static class LampConfiguration {
    private String node;
    private DeviceName device;
    private List<DimmerChannel> dimmers = new ArrayList<>();
  }

  @Data
  public static class DimmerChannel {
    private int dimmer;
    private String channel;
  }

  @Data
  public static class SensorConfiguration {
    private DeviceName device;
    private String mac;
    private String name;
    private String type;
  }

  @Data
  public static class SensorTypeConfiguration {
    private String manufacturer;
    private String brand;
    private boolean humidity;
    private boolean illuminance;
    private boolean temperature;
    private boolean deviceBattery;
  }
}
