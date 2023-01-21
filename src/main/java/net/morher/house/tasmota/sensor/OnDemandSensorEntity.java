package net.morher.house.tasmota.sensor;

import net.morher.house.api.entity.Device;
import net.morher.house.api.entity.EntityDefinition;
import net.morher.house.api.entity.sensor.SensorEntity;
import net.morher.house.api.entity.sensor.SensorOptions;

public class OnDemandSensorEntity<T> {
  private final Device device;
  private final EntityDefinition<SensorEntity<T>> entityDefinition;
  private SensorOptions options;
  private SensorEntity<T> entity;

  public OnDemandSensorEntity(Device device, EntityDefinition<SensorEntity<T>> entityDefinition) {
    this.device = device;
    this.entityDefinition = entityDefinition;
  }

  public OnDemandSensorEntity<T> withOptions(SensorOptions options) {
    this.options = options;
    if (entity != null) {
      entity.setOptions(options);
    }
    return this;
  }

  public OnDemandSensorEntity<T> autoInitialized(boolean create) {
    if (create) {
      entity();
    }
    return this;
  }

  public SensorEntity<T> entity() {
    if (entity == null) {
      entity = device.entity(entityDefinition, options);
    }
    return entity;
  }

  public void publishState(T state) {
    entity().state().publish(state);
  }
}
