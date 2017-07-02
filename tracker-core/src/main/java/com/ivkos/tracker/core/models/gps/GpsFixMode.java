package com.ivkos.tracker.core.models.gps;

public enum GpsFixMode
{
   NOT_SEEN(false),
   NO_FIX(false),

   FIX_2D(true),
   FIX_3D(true);

   private transient final boolean hasFix;

   GpsFixMode(boolean hasFix)
   {
      this.hasFix = hasFix;
   }

   public boolean hasFix()
   {
      return hasFix;
   }
}
