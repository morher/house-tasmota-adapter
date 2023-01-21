package net.morher.house.tasmota.sensor;

import static java.util.Optional.ofNullable;
import static net.morher.house.api.devicetypes.ClimateAndWeatherSensorDevice.HUMIDITY;
import static net.morher.house.api.devicetypes.ClimateAndWeatherSensorDevice.ILLUMINANCE;
import static net.morher.house.api.devicetypes.ClimateAndWeatherSensorDevice.TEMPERATURE;
import static net.morher.house.api.devicetypes.GeneralDevice.DEVICE_BATTERY;
import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.DeviceInfo;
import net.morher.house.api.entity.EntityCategory;
import net.morher.house.api.entity.sensor.SensorOptions;
import net.morher.house.api.entity.sensor.SensorType;
import net.morher.house.tasmota.config.TasmotaConfiguration.SensorConfiguration;
import net.morher.house.tasmota.config.TasmotaConfiguration.SensorTypeConfiguration;

public class TasmotaSensorDevice {
  private final OnDemandSensorEntity<Double> humidity;
  private final OnDemandSensorEntity<Double> illuminance;
  private final OnDemandSensorEntity<Double> temperature;
  private final OnDemandSensorEntity<Double> deviceBattery;

  public TasmotaSensorDevice(
      Device device, SensorConfiguration sensorConfig, SensorTypeConfiguration sensorType) {
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setManufacturer(sensorType.getManufacturer());
    deviceInfo.setModel(sensorType.getBrand());
    device.setDeviceInfo(deviceInfo);

    this.humidity =
        new OnDemandSensorEntity<>(device, HUMIDITY)
            .withOptions(new SensorOptions(SensorType.HUMIDITY))
            .autoInitialized(sensorType.isHumidity());

    this.illuminance =
        new OnDemandSensorEntity<>(device, ILLUMINANCE)
            .withOptions(new SensorOptions(SensorType.ILLUMINANCE_LX))
            .autoInitialized(sensorType.isIlluminance());

    this.temperature =
        new OnDemandSensorEntity<>(device, TEMPERATURE)
            .withOptions(new SensorOptions(SensorType.TEMPERATURE_C))
            .autoInitialized(sensorType.isTemperature());

    this.deviceBattery =
        new OnDemandSensorEntity<>(device, DEVICE_BATTERY)
            .withOptions(new SensorOptions(SensorType.BATTERY, EntityCategory.DIAGNOSTIC))
            .autoInitialized(sensorType.isDeviceBattery());
  }

  public void onReport(DeviceReport report) {
    ofNullable(report.getTemperature()).ifPresent(temperature::publishState);
    ofNullable(report.getHumidity()).ifPresent(humidity::publishState);
    ofNullable(report.getIlluminance()).ifPresent(illuminance::publishState);
    ofNullable(report.getBattery()).ifPresent(deviceBattery::publishState);
  }
}
