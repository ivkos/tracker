package com.ivkos.tracker.api.spring;

import com.ivkos.tracker.api.device.DeviceService;
import com.ivkos.tracker.api.device.DeviceVerifier;
import com.ivkos.tracker.core.models.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import static com.ivkos.tracker.api.spring.DeviceAuthentication.ADMIN;
import static com.ivkos.tracker.api.spring.DeviceAuthentication.ANONYMOUS;

@Component
public class DeviceAuthenticationFilter extends GenericFilterBean
{
   public static final String HEADER_HARDWARE_ID = "X-Hardware-Id";
   public static final String HEADER_DEVICE_ID = "X-Device-Id";
   public static final String HEADER_API_SECRET = "X-Api-Secret";

   private final DeviceService service;
   private final DeviceVerifier verifier;
   private final String apiSecret;

   @Autowired
   DeviceAuthenticationFilter(DeviceService service, DeviceVerifier verifier, @Value("${app.secret}") String apiSecret)
   {
      this.service = service;
      this.verifier = verifier;
      this.apiSecret = apiSecret;
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      String headerHardwareId = httpRequest.getHeader(HEADER_HARDWARE_ID);
      String headerDeviceId = httpRequest.getHeader(HEADER_DEVICE_ID);
      String headerApiSecret = httpRequest.getHeader(HEADER_API_SECRET);

      boolean isAdmin = false;
      if (headerApiSecret != null) {
         if (!this.apiSecret.equals(headerApiSecret)) {
            throw new BadCredentialsException("Incorrect API secret");
         } else {
            isAdmin = true;
         }
      }

      if (headerDeviceId != null) {
         UUID id;
         try {
            id = UUID.fromString(headerDeviceId);
         } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid device UUID", e);
         }

         Device device;

         boolean isHardwareIdProvided = false;
         if (headerHardwareId != null) {
            long hardwareId;
            try {
               hardwareId = Long.parseLong(headerHardwareId);
            } catch (NumberFormatException e) {
               throw new BadCredentialsException("Invalid device hardware id", e);
            }

            device = new Device(id, hardwareId);
            if (!verifier.verify(device)) {
               throw new BadCredentialsException("Invalid device definition");
            }

            isHardwareIdProvided = true;

            if (!service.isRegistered(id)) {
               device = service.register(device);
            } else {
               device = service.findById(id);
            }

            device = service.updateLastSeen(device);
         } else {
            device = service.findById(id);
         }

         SecurityContextHolder
               .getContext()
               .setAuthentication(new DeviceAuthentication(device, isHardwareIdProvided, isAdmin));
      } else {
         SecurityContextHolder.getContext()
               .setAuthentication(isAdmin ? ADMIN : ANONYMOUS);
      }

      chain.doFilter(request, response);
   }
}
