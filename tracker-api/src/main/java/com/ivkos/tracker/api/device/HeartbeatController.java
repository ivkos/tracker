package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ivkos.tracker.core.constants.ApiEndpoints.HEARTBEAT;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class HeartbeatController
{
   @PostMapping(HEARTBEAT)
   HttpEntity heartbeat(@AuthenticationPrincipal Device device)
   {
      return ok().build();
   }
}
