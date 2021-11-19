package example;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.FileCopyUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class FunctionConfiguration {

   private final ResourceLoader resourceLoader;
   private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

   /*
    * You need this main method (empty) or explicit <start-class>example.FunctionConfiguration</start-class>
    * in the POM to ensure boot plug-in makes the correct entry
    */
   public static void main(String[] args) {
      // empty unless using Custom runtime at which point it should include
      // SpringApplication.run(FunctionConfiguration.class, args);
   }

   @Bean
   public Function<Map<String,String>, Map<String,String>> reverse() {
      return value -> {
         if (value.containsValue("exception")) {
            throw new RuntimeException("Intentional exception which should result in HTTP 417");
         }
         return value.entrySet()
                     .stream()
                     .collect(LinkedHashMap::new,
                              (map, item) -> map.put(item.getKey(), new StringBuilder(item.getValue()).reverse().toString()),
                              Map::putAll
                     );
      };
   }

   @Bean
   public Function<Person, Person> insertPerson() {
      return person -> {
         if (person == null) {
            throw new RuntimeException("Value is null");
         }
         log.info("Value of person object: {}", person);
         return person;
      };
   }

   @SuppressWarnings({"unchecked", "unused"})
   public Function<S3EventNotification, Object> dataInput(final Function<Map<String,String>, Map<String,String>> theReverseFunction,
                                                          final Function<Person, Person> theInsertPersonFunction) {
      return event -> {
         if (event == null) {
            throw new RuntimeException("Event is null");
         }
         final var anEvent = event.getRecords().stream().filter(record -> StringUtils.isNotBlank(record.getS3().getObject().getKey()))
                                                        .findFirst()
                                                        .orElseThrow();
         final var anObject = anEvent.getS3().getObject();
         final var anObjectKey = anObject.getKey();
         final var anInput = this.resourceLoader.getResource(String.format("s3://%s/%s", anEvent.getS3().getBucket().getName(), anEvent.getS3().getObject().getKey()));
         try {
            final var anInputText = StringUtils.toEncodedString(FileCopyUtils.copyToByteArray(anInput.getInputStream()), UTF_8);
            if (anObjectKey.contains("reverse")) {
               final var aResult = theReverseFunction.apply(this.objectMapper.readValue(anInputText, Map.class));
               log.info("Result of reverse: {}", this.objectMapper.writeValueAsString(aResult));
               return HttpStatus.OK;
            }
            else if (anObjectKey.contains("insertPerson")) {
               final var aResult = theInsertPersonFunction.apply(this.objectMapper.readValue(anInputText, Person.class));
               log.info("Result of insertPerson: {}", aResult);
               return aResult;
            }
            else {
               return HttpStatus.NOT_FOUND;
            }
         }
         catch (IOException e) {
            throw new RuntimeException(e);
         }
      };
   }
}