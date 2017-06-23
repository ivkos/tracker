package com.ivkos.tracker.api;

import com.ivkos.tracker.core.models.ModelsPackageMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = { ModelsPackageMarker.class, Application.class })
public class Application
{
   public static void main(String[] args)
   {
      SpringApplication.run(Application.class, args);
   }
}
