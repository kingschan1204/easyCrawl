package com.github.kingschan1204.easycrawl.helper.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

public class JsonSchemaHelper {


    /**
     * 递归方法来生成 JSON schema
     * @param node
     * @return
     */
    public static JsonNode generateSchema(JsonNode node) {

        ObjectMapper objectMapper = new ObjectMapper();
        if (node.isObject()) {
            // 如果是对象，遍历其所有字段
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            JsonNode schemaNode = objectMapper.createObjectNode();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                ((ObjectNode) schemaNode).set(field.getKey(), generateSchema(field.getValue()));
            }

            return schemaNode;
        } else if (node.isArray()) {
            // 如果是数组，检查数组中的元素类型
            JsonNode schemaNode = objectMapper.createObjectNode();
            ((ObjectNode) schemaNode).put("type", "array");
            ((ObjectNode) schemaNode).set("items", generateSchema(node.get(0)));

            return schemaNode;
        } else if (node.isTextual()) {
            return objectMapper.createObjectNode().put("type", "string");
        } else if (node.isInt()) {
            return objectMapper.createObjectNode().put("type", "integer");
        } else if (node.isBoolean()) {
            return objectMapper.createObjectNode().put("type", "boolean");
        } else if (node.isNull()) {
            return objectMapper.createObjectNode().put("type", "null");
        } else {
            return objectMapper.createObjectNode().put("type", "unknown");
        }
    }

}
