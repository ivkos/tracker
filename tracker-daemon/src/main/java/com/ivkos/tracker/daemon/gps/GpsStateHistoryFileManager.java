package com.ivkos.tracker.daemon.gps;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ivkos.tracker.core.models.gps.GpsState;
import com.ivkos.tracker.daemon.support.logging.InjectLogger;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.zip.GZIPOutputStream;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoField.*;
import static java.util.zip.Deflater.BEST_COMPRESSION;

class GpsStateHistoryFileManager
{
   private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
         .parseCaseInsensitive()
         .appendValue(YEAR, 4)
         .appendValue(MONTH_OF_YEAR, 2)
         .appendValue(DAY_OF_MONTH, 2)
         .appendLiteral('-')
         .appendValue(HOUR_OF_DAY, 2)
         .appendValue(MINUTE_OF_HOUR, 2)
         .appendValue(SECOND_OF_MINUTE, 2)
         .toFormatter();

   private final LocalDateTime now = now();
   private File file;

   @InjectLogger
   private Logger logger;

   @Inject
   GpsStateHistoryFileManager(@Named("daemon.gpsStateHistoryDirectory") String gpsStateHistoryDirectory)
   {
      File dir = new File(gpsStateHistoryDirectory);

      if (!dir.exists()) {
         dir.mkdirs();
      }

      this.file = new File(dir, "gps_state_" + now.format(formatter) + ".json.gz");
   }

   public void write(GpsState state)
   {
      try {
         FileOutputStream fos = new FileOutputStream(this.file, true);
         try (GZIPOutputStream gz = new LevelConfigurableGZIPOutputStream(fos, BEST_COMPRESSION)) {
            byte[] data = Json.mapper.writeValueAsString(state).getBytes();
            gz.write(data);
            gz.write(System.lineSeparator().getBytes());
         }
      } catch (IOException e) {
         logger.warn("Cannot write GPS state to log file " + file.getAbsolutePath(), e);
      }
   }

   private static class LevelConfigurableGZIPOutputStream extends GZIPOutputStream
   {
      LevelConfigurableGZIPOutputStream(OutputStream out, int level) throws IOException
      {
         super(out);
         def.setLevel(level);
      }
   }
}
