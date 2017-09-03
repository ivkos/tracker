package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.gps.GpsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

import static com.ivkos.tracker.core.constants.ApiEndpoints.*;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class DeviceHistoryController
{
   private final DeviceHistoryService service;

   @Autowired
   DeviceHistoryController(DeviceHistoryService service)
   {
      this.service = service;
   }

   @GetMapping(HISTORY)
   HttpEntity getAll(@AuthenticationPrincipal Device device)
   {
      return ok(service.getHistoryByDevice(device));
   }

   @GetMapping(HISTORY_RANGES)
   HttpEntity getAllInGroups(@AuthenticationPrincipal Device device)
   {
      return ok(service.getHistoryRanges(device));
   }

   @GetMapping(HISTORY_RANGES_FROM_TO)
   HttpEntity getHistoryInRange(@AuthenticationPrincipal Device device,
                                @PathVariable("from") String fromVar,
                                @PathVariable("to") String toVar)
   {
      OffsetDateTime from = OffsetDateTime.parse(fromVar);
      OffsetDateTime to = OffsetDateTime.parse(toVar);

      return ok(service.getHistoryInRange(device, from, to));
   }

   @GetMapping(HISTORY_ID)
   HttpEntity getById(@PathVariable UUID id, @AuthenticationPrincipal Device device)
   {
      return ok(service.getByIdAndDevice(id, device));
   }

   @PutMapping(HISTORY)
   HttpEntity save(@RequestBody @Valid @NotNull Collection<GpsState> states, @AuthenticationPrincipal Device device)
   {
      service.save(states, device);
      return new ResponseEntity(HttpStatus.CREATED);
   }
}
