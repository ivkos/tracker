package com.ivkos.tracker.api.spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

import static com.ivkos.tracker.api.spring.ApiExceptionHandler.ConciseExceptionMessage.of;

@Component
@ControllerAdvice
class ApiExceptionHandler
{
   @ExceptionHandler(Exception.class)
   ResponseEntity exception(Exception e)
   {
      return of(500, e, "Something went wrong");
   }

   @ExceptionHandler(IllegalArgumentException.class)
   ResponseEntity illegalArgument(IllegalArgumentException e)
   {
      return of(400, e);
   }

   @ExceptionHandler(HttpMessageNotReadableException.class)
   ResponseEntity notReadable(HttpMessageNotReadableException e)
   {
      return of(400, e, "Your message is unreadable");
   }

   @ExceptionHandler(BadCredentialsException.class)
   ResponseEntity badCredentials(BadCredentialsException e)
   {
      return of(401, e);
   }

   @ExceptionHandler(EntityNotFoundException.class)
   ResponseEntity notFound(EntityNotFoundException e)
   {
      return of(404, e);
   }

   @ExceptionHandler(EntityExistsException.class)
   ResponseEntity entityExists(EntityExistsException e)
   {
      return of(409, e);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   ResponseEntity validationException(MethodArgumentNotValidException e)
   {
      String result = e.getBindingResult().getAllErrors().stream()
            .filter(x -> x instanceof FieldError)
            .map(fe -> ((FieldError) fe).getField() + " " + fe.getDefaultMessage())
            .collect(Collectors.joining("; "));

      return of(400, e, result);
   }

   static class ConciseExceptionMessage
   {
      public final int status;
      public final String message;
      public final long timestamp = System.currentTimeMillis();

      public ConciseExceptionMessage(int status, Throwable t)
      {
         this.status = status;
         this.message = t.getMessage();
      }

      public ConciseExceptionMessage(int status, String message)
      {
         this.status = status;
         this.message = message;
      }

      public static ResponseEntity of(int code, Throwable t)
      {
         t.printStackTrace();

         ConciseExceptionMessage msg = new ConciseExceptionMessage(code, t);

         return new ResponseEntity<>(
               msg,
               HttpStatus.valueOf(code)
         );
      }

      public static ResponseEntity of(int code, Throwable t, String message)
      {
         t.printStackTrace();

         ConciseExceptionMessage msg = new ConciseExceptionMessage(code, message);

         return new ResponseEntity<>(
               msg,
               HttpStatus.valueOf(code)
         );
      }
   }
}
