package com.ivkos.tracker.api.history;

import com.ivkos.tracker.core.models.device.Device;
import com.ivkos.tracker.core.models.gps.GpsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.ivkos.tracker.core.constants.ApiEndpoints.HISTORY;
import static com.ivkos.tracker.core.constants.ApiEndpoints.HISTORY_ID;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class DeviceHistoryController
{
   private final DeviceHistoryRepository repository;

   @Autowired
   DeviceHistoryController(DeviceHistoryRepository repository)
   {
      this.repository = repository;
   }

   @GetMapping(HISTORY)
   HttpEntity getAll(@AuthenticationPrincipal Device device)
   {
      return ok(repository.getAllByDeviceOrderByDateCreatedDesc(device));
   }

   @GetMapping(HISTORY_ID)
   HttpEntity getById(@PathVariable UUID id, @AuthenticationPrincipal Device device)
   {
      return ok(repository.findOneByIdAndDevice(id, device)
            .orElseThrow(() -> new EntityNotFoundException("No such state")));
   }

   @PutMapping(HISTORY)
   HttpEntity save(@RequestBody @Valid @NotNull Collection<GpsState> states, @AuthenticationPrincipal Device device)
   {
      List<DeviceGpsState> items = states.stream()
            .map(state -> new DeviceGpsState(device, state))
            .collect(toList());

      repository.save(items);

      return new ResponseEntity(HttpStatus.CREATED);
   }
}
