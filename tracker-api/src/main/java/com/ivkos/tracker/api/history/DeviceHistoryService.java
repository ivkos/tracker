package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.core.support.GpsStateComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.ivkos.tracker.core.support.GpsStateComparator.getTimeOfGpsState;
import static java.util.stream.Collectors.toList;

@Service
class DeviceHistoryService
{
   private final DeviceHistoryRepository repository;

   @Autowired
   DeviceHistoryService(DeviceHistoryRepository repository)
   {
      this.repository = repository;
   }

   public List<GpsState> getHistoryByDevice(Device device)
   {
      return repository.getAllByDeviceOrderByStateSatelliteTimeDesc(device)
            .stream()
            .map(DeviceGpsState::getState)
            .collect(toList());
   }

   public GpsState getLatestLocationByDevice(Device device)
   {
      return repository.findFirstByDeviceOrderByStateSatelliteTimeDesc(device)
            .orElseThrow(() -> new EntityNotFoundException("No history"))
            .getState();
   }

   public List<GpsStateRange> getHistoryRanges(Device device)
   {
      List<GpsState> fullHistory = repository.getAllByDeviceOrderByStateSatelliteTimeAsc(device)
            .stream()
            .map(DeviceGpsState::getState)
            .collect(toList());

      List<GpsStateRange> ranges = new LinkedList<>();

      GpsState pivot = null;
      for (int i = 1; i < fullHistory.size(); i++) {
         GpsState first = fullHistory.get(i - 1);
         GpsState second = fullHistory.get(i);

         if (pivot == null) pivot = first;

         OffsetDateTime firstTime = getTimeOfGpsState(first);
         OffsetDateTime secondTime = getTimeOfGpsState(second);

         if (Duration.between(firstTime, secondTime).toMinutes() >= 5) {
            ranges.add(new GpsStateRange(
                  getTimeOfGpsState(pivot),
                  firstTime
            ));

            pivot = second;
         }
      }

      ranges.add(new GpsStateRange(
            getTimeOfGpsState(pivot),
            getTimeOfGpsState(fullHistory.get(fullHistory.size() - 1))
      ));

      return ranges;
   }

   public GpsState getByIdAndDevice(UUID id, Device device)
   {
      return repository.findOneByIdAndDevice(id, device)
            .orElseThrow(() -> new EntityNotFoundException("No such state"))
            .getState();
   }

   public List<GpsState> getHistoryInRange(Device device, OffsetDateTime from, OffsetDateTime to)
   {
      return repository.getAllByDeviceAndStateSatelliteTimeGreaterThanEqualAndStateSatelliteTimeLessThanEqual(
            device, from, to).stream()
            .map(DeviceGpsState::getState)
            .collect(toList());
   }

   public void save(Collection<GpsState> states, Device device)
   {
      List<DeviceGpsState> items = states.stream()
            .sorted(GpsStateComparator.getInstance())
            .map(state -> new DeviceGpsState(device, state))
            .collect(toList());

      repository.save(items);
   }

   static class GpsStateRange
   {
      public final OffsetDateTime from;
      public final OffsetDateTime to;

      public GpsStateRange(OffsetDateTime from, OffsetDateTime to)
      {
         this.from = from;
         this.to = to;
      }
   }
}
