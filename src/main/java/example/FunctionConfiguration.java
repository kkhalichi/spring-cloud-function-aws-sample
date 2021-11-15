package example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FunctionConfiguration {

   /*
    * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
    * in the POM to ensure boot plug-in makes the correct entry
    */
   public static void main(String[] args) {
      // empty unless using Custom runtime at which point it should include
      // SpringApplication.run(FunctionConfiguration.class, args);
   }

   @Bean
   public Function<LinkedHashMap<String,String>, LinkedHashMap<String,String>> uppercase() {
      return value -> {
         if (value.containsValue("exception")) {
            throw new RuntimeException("Intentional exception which should result in HTTP 417");
         }
         else {
            return value.entrySet()
                        .stream()
                        .collect(LinkedHashMap::new,
                                 (map, item) -> map.put(item.getKey(), new StringBuilder(item.getValue()).reverse().toString()),
                                 Map::putAll
                        );
         }
      };
   }
}
