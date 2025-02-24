package com.github.kingschan1204.easycrawl.helper.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.SneakyThrows;

/**
 * @author kings.chan 2024-6-27
 */
public class EasyJson implements JsonHelper {

  private static final ObjectMapper objectMapper;
  JsonNode root;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    // 支持解析单引号
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    // 遇到不存在的属性不报错
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 所有的日期格式统一样式： yyyy-MM-dd HH:mm:ss
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
  }

  private EasyJson() {}

  public EasyJson(Object obj) {
    if (obj instanceof String) {
      try {
        root = objectMapper.readTree(String.valueOf(obj));
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else if (obj instanceof JsonNode) {
      root = (JsonNode) obj;
    } else {
      root = objectMapper.valueToTree(obj);
    }
  }

  public static EasyJson of(Object obj) {
    return new EasyJson(obj);
  }

  @Override
  @SneakyThrows
  public <T> T toJavaObj(Class<T> clazz) {
    if (clazz.equals(String.class)) {
      return (T) root.toString();
    }
    return objectMapper.readValue(root.traverse(), clazz);
  }

  @Override
  @SneakyThrows
  public <T> List<T> toListObj(Class<T> clazz) {
    TypeFactory typeFactory = objectMapper.getTypeFactory();
    Type listType = typeFactory.constructCollectionType(List.class, clazz);
    return objectMapper.readValue(root.traverse(), typeFactory.constructType(listType));
  }

  @SneakyThrows
  @Override
  public List<Map<String, Object>> toListMap() {
    if (root.isArray()) {
      TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<>() {};
      return objectMapper.readValue(root.traverse(), typeReference);
    }
    return null;
  }

  @Override
  public Iterator<JsonNode> iterator() {
    ArrayNode arrayNode = (ArrayNode) root;
    return arrayNode.iterator();
  }

  @Override
  public EasyJson op(String expression) {
    JsonNode result = get(expression, JsonNode.class);
    return of(result);
  }

  /**
   * 通过
   *
   * <p>属性. 的方式取 支持多层
   *
   * <p>
   *
   * <p>$first 返回jsonArray的第一个对象
   *
   * <p>
   *
   * <p>$last 返回jsonArray的最后一个对象
   *
   * <p>
   *
   * <p>* 返回jsonArray的所有对象
   *
   * <p>
   *
   * <p>,逗号分隔可获取jsonArray的多个字段组成新对象返回
   *
   * <p>
   *
   * <p>->arrayList抽取单个字段转成 arrayList类似 [1,2,3,4]
   *
   * <p>
   *
   * @param expression 表达式 属性.属性
   * @param clazz 返回类型
   * @return 表达式的值
   */
  @Override
  public <T> T get(String expression, Class<T> clazz) {
    String[] depth = expression.split("\\.");
    Object object = getValByExpression(root, depth[0]);
    for (int i = 1; i < depth.length; i++) {
      object = getValByExpression(object, depth[i]);
    }
    return (T) object;
  }

  @Override
  public JsonNode get(String expression) {
    String[] depth = expression.split("\\.");
    Object object = getValByExpression(root, depth[0]);
    for (int i = 1; i < depth.length; i++) {
      object = getValByExpression(object, depth[i]);
    }
    return (JsonNode) object;
  }

  @Override
  public JsonNode root() {
    return this.root;
  }

  private Object getValByExpression(Object object, String expression) {
    // jsonObject
    if (object instanceof ObjectNode) {
      return ((ObjectNode) object).get(expression);
    } else if (object instanceof ArrayNode js) {
      // jsonArray模式
      // 如果是数字直接取下标，保留关键字：$first第一条 $last最后一条
      if (expression.matches("\\d+")) {
        return js.get(Integer.parseInt(expression));
      } else if (expression.matches("\\$first")) {
        return js.get(0);
      } else if (expression.matches("\\$last")) {
        return js.get(js.size() - 1);
      }
      // 抽取单个字段转成 arrayList类似 [1,2,3,4]
      else if (expression.matches("\\w+(->)arrayList$")) {
        String key = expression.replace("->arrayList", "");
        ArrayNode list = objectMapper.createArrayNode();
        for (int i = 0; i < js.size(); i++) {
          list.add(js.get(i).get(key));
        }
        return list;
      } else if (expression.contains(",")) {
        // 从集合里抽 支持多字段以,逗号分隔
        String[] fields = expression.split(",");
        ArrayNode result = objectMapper.createArrayNode();
        for (int i = 0; i < js.size(); i++) {
          ObjectNode json = objectMapper.createObjectNode();
          for (String key : fields) {
            json.put(key, js.get(i).get(key));
          }
          result.add(json);
        }
        return result;
      }
    }
    return null;
  }

  public Set<String> keySet() {
    return getAllKeys(this.root);
  }

  Set<String> getAllKeys(JsonNode jsonNode) {
    Set<String> keys = new LinkedHashSet<>();
    //        if(jsonNode.isObject()){
    //
    //        }
    for (Iterator<String> it = jsonNode.fieldNames(); it.hasNext(); ) {
      String key = it.next();
      Object value = jsonNode.get(key);
      if (value instanceof ObjectNode) {
        Set<String> subKeys = getAllKeys((JsonNode) value);
        for (String subKey : subKeys) {
          keys.add(key + "." + subKey);
        }
      } else {
        keys.add(key);
      }
    }
    return keys;
  }

  @Override
  public JsonHelper put(String exp, String key, Object value) {
    JsonNode result = get(exp, JsonNode.class);
    return _put(result, key, value);
  }

  @Override
  public JsonHelper put(String key, Object value) {
    if (root.isArray()) {
      for (JsonNode node : root) {
        _put(node, key, value);
      }
      return this;
    } else {
      return _put(root, key, value);
    }
  }

  @Override
  public JsonHelper toTimeStamp(String key) {
    return updateDateValue(key, val -> DateHelper.of(val).timeStamp());
  }

  @Override
  public JsonHelper toIntDate(String key) {
    return updateDateValue(key, val -> Long.valueOf(DateHelper.of(val).dateInt()));
  }

  private JsonHelper updateDateValue(String key, Function<String, Long> dateConverter) {
    if (root.isObject()) {
      ObjectNode objectNode = (ObjectNode) root;
      if (objectNode.has(key)) {
        String val = objectNode.get(key).textValue();
        objectNode.put(key, dateConverter.apply(val));
      }
    } else if (root.isArray()) {
      for (JsonNode node : root) {
        ObjectNode objectNode = (ObjectNode) node;
        if (objectNode.has(key)) {
          String val = objectNode.get(key).asText();
          if (String.valueOf(val).matches("\\d+")) {
            objectNode.put(key, dateConverter.apply(val));
          }
        }
      }
    }
    return this;
  }

  @Override
  public void forEach(Consumer<? super JsonNode> consumer) {
    root.forEach(consumer);
  }

  @Override
  public String pretty() {
    return root.toPrettyString();
  }

  private EasyJson _put(JsonNode node, String key, Object value) {
    Assert.isTrue(node.isObject(), "不是jsonObject无法添加元素！");
    ObjectNode objectNode = (ObjectNode) node;
    // 添加新的元素
    if (value instanceof JsonNode) {
      objectNode.putPOJO(key, value);
    } else if (value instanceof String) {
      objectNode.put(key, String.valueOf(value));
    } else if (value instanceof Integer) {
      objectNode.put(key, (Integer) value);
    } else if (value instanceof Long) {
      objectNode.put(key, (Long) value);
    } else if (value instanceof Boolean) {
      objectNode.put(key, (Boolean) value);
    } else if (value instanceof Double) {
      objectNode.put(key, (Double) value);
    }
    /* else if(value instanceof List<?>){
        ArrayNode newArray = objectMapper.createArrayNode();
        List<?> list = (List<?>) value;
        list.forEach(r-> newArray.addAll(r));
    }*/
    else {
      throw new RuntimeException("不支持的类型：" + value.getClass().getName());
    }
    return this;
  }

  public EasyJson put(String key, List<?> list) {
    Assert.isTrue(root.isObject(), "不是jsonObject无法添加元素！");
    ObjectNode objectNode = (ObjectNode) root;

    ArrayNode nodes = objectMapper.createArrayNode();
    list.forEach(
        r -> {
          nodes.add((JsonNode) r);
        });
    return this;
  }

  @Override
  public String toString() {
    return root.toString();
  }
}
