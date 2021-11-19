/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Throwables;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FunctionConfigurationTest {

   private static final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

   @Autowired
   private Function<Map<String,String>, Map<String,String>> function;

	@SuppressWarnings("unchecked")
   @Test
	public void testReverseFunction() {
      try {
         final var anInput = mapper.readValue(Files.newInputStream(Paths.get("src/test/resources/reverse.json")), Map.class);
         final var aResult = this.function.apply(anInput);
         assertThat(aResult).isNotEmpty();
         assertThat(aResult.size()).isEqualTo(20);
         assertThat(aResult).containsValues("tenet", "avaj", "lived");
      }
      catch (IOException e) {
         fail("Test file read issue", Throwables.getRootCause(e));
      }
	}
}
