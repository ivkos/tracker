package com.ivkos.tracker.api.spring;

import com.ivkos.tracker.core.models.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

public class DeviceAuthentication implements Authentication
{
   public static final DeviceAuthentication ANONYMOUS = new DeviceAuthentication(Device.ANONYMOUS, false);
   public static final DeviceAuthentication ADMIN = new DeviceAuthentication(Device.ANONYMOUS, false, true);

   private final Device device;
   private final boolean isHardwareIdProvided;

   private boolean isAdmin = false;
   private boolean isAuthenticated = true;

   public DeviceAuthentication(Device device, boolean isHardwareIdProvided)
   {
      this.device = device;
      this.isHardwareIdProvided = isHardwareIdProvided;
   }

   public DeviceAuthentication(Device device, boolean isHardwareIdProvided, boolean isAdmin)
   {
      this(device, isHardwareIdProvided);
      this.isAdmin = isAdmin;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities()
   {
      Collection<GrantedAuthority> authorities = new HashSet<>();

      authorities.add(new SimpleGrantedAuthority(Roles.CLIENT));

      if (isHardwareIdProvided) {
         authorities.add(new SimpleGrantedAuthority(Roles.DEVICE));
      }

      if (isAdmin) {
         authorities.add(new SimpleGrantedAuthority(Roles.ADMIN));
      }

      return authorities;
   }

   @Override
   public Object getCredentials()
   {
      return device.getId();
   }

   @Override
   public Object getDetails()
   {
      return device;
   }

   @Override
   public Object getPrincipal()
   {
      return device;
   }

   @Override
   public boolean isAuthenticated()
   {
      return isAuthenticated;
   }

   @Override
   public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
   {
      this.isAuthenticated = isAuthenticated;
   }

   @Override
   public String getName()
   {
      return device.getId().toString();
   }
}
