package example;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class Person implements Serializable {

   private UUID id;
   private String firstName;
   private String lastName;
   private Address address;

   @Data
   public static class Address implements Serializable {
      private String address1;
      private String address2;
      private String city;
      private String stateProvince;
      private String postalCode;
   }
}