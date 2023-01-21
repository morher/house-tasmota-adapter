package net.morher.house.tasmota.sensor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceReport {

  private String mac;

  @JsonProperty("Humidity")
  private Double humidity;

  @JsonProperty("Illuminance")
  private Double illuminance;

  @JsonProperty("Temperature")
  private Double temperature;

  @JsonProperty("Battery")
  private Double battery;
}
