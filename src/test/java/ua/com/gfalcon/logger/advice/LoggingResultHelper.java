/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
 * Copyright (c) 2021 Oleksii V. KHALIKOV, PE.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ua.com.gfalcon.logger.advice;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ua.com.gfalcon.logger.parameters.loggabletype.exception.LoggerException;

public class LoggingResultHelper {
    public static final String PARAM_STR = "STR_PARAM";
    public static final Long PARAM_LONG = 1L;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static class JsonLogAssertion {
        private ByteArrayOutputStream baos;
        private final Queue<String> nestedProperties = new LinkedList<>();
        private final List<String> propertyValuesIgnored = new ArrayList<>();

        public JsonLogAssertion givenSource(ByteArrayOutputStream byteArrayOutputStream) {
            this.baos = byteArrayOutputStream;
            return this;
        }

        public JsonLogAssertion fromCtx() {
            return fromProperty("context").fromProperty("ctx");
        }

        public JsonLogAssertion fromProperty(String propertyName) {
            nestedProperties.offer(propertyName);
            return this;
        }

        public JsonLogAssertion propertyValueIgnored(String property) {
            propertyValuesIgnored.add(property);
            return this;
        }

        public void shouldBeEqualTo(JsonNode jsonNode) {
            try {
                JsonNode nodeToTraverse = OBJECT_MAPPER.readTree(baos.toString());

                for (String property : nestedProperties) {
                    nodeToTraverse = nodeToTraverse.get(property);
                }

                for (String property : propertyValuesIgnored) {
                    nullifyField(jsonNode, property);
                    nullifyField(nodeToTraverse, property);
                }

                Assertions.assertEquals(jsonNode, nodeToTraverse);

            } catch (Exception ex) {
                throw new LoggerException(ex);
            }
        }

        private void nullifyField(JsonNode node, String property) {
            String[] props = property.split("\\.");

            ObjectNode objectNode = new ObjectNode(null, null);

            for (int i = 0; i < props.length - 1; i++) {
                objectNode = (ObjectNode) node.get(props[i]);
            }

            objectNode.put(props[props.length - 1], "");

        }
    }

    public static JsonLogAssertion supposeThat() {
        return new JsonLogAssertion();
    }
}
