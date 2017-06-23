package com.ivkos.tracker.api.command;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commands")
class CommandController
{
   @GetMapping
   HttpEntity getPendingCommands()
   {
      return null;
   }
}
