package com.ivkos.tracker.api.device;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(HeartbeatController.HEARTBEAT)
public class HeartbeatController
{
   public static final String HEARTBEAT = "/heartbeat";

   @PostMapping
   HttpEntity heartbeat(@AuthenticationPrincipal Device device)
   {
      return ok().build();
   }
}
