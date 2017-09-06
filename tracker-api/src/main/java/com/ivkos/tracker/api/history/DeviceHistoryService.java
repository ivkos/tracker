package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.core.support.GpsStateComparator;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

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

   @Transactional
   public List<GpsStateRange> getHistoryRanges(Device device)
   {
      final GpsState[] pivot = { null };

      List<GpsStateRange> ranges = repository.getAllByDeviceOrderByStateSatelliteTimeAsc(device)
            .map(DeviceGpsState::getState)
            .map(new Function<GpsState, Pair<GpsState, GpsState>>()
            {
               GpsState prev;

               @Override
               public Pair<GpsState, GpsState> apply(GpsState state)
               {
                  Pair<GpsState, GpsState> pair = null;
                  if (prev != null) pair = new Pair<>(prev, state);
                  prev = state;
                  return pair;
               }
            })
            .skip(1) // skip first null
            .map(x -> {
               GpsState first = x.getKey();
               GpsState second = x.getValue();

               if (pivot[0] == null) pivot[0] = first;

               OffsetDateTime firstTime = getTimeOfGpsState(first);
               OffsetDateTime secondTime = getTimeOfGpsState(second);

               if (Duration.between(firstTime, secondTime).toMinutes() >= 5) {
                  GpsState pivotCopy = pivot[0];
                  pivot[0] = second;

                  return new GpsStateRange(
                        getTimeOfGpsState(pivotCopy),
                        firstTime
                  );
               } else {
                  return null;
               }
            })
            .filter(Objects::nonNull)
            .collect(toList());

      if (ranges.size() == 0) {
         return Collections.emptyList();
      }

      GpsState latest = getLatestLocationByDevice(device);

      ranges.add(new GpsStateRange(
            getTimeOfGpsState(pivot[0]),
            getTimeOfGpsState(latest)
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
