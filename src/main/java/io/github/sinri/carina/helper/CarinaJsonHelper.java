package io.github.sinri.carina.helper;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @since 2.6
 */
public class CarinaJsonHelper {
    private static final CarinaJsonHelper instance = new CarinaJsonHelper();

    private CarinaJsonHelper() {

    }

    static CarinaJsonHelper getInstance() {
        return instance;
    }

    public JsonObject writeIntoJsonObject(JsonObject jsonObject, String key, Object value) {
        jsonObject.put(String.valueOf(key), value);
        return jsonObject;
    }

    public JsonArray writeIntoJsonArray(JsonArray jsonArray, int index, Object value) {
        if (index >= 0) {
            if (index >= jsonArray.size()) {
                for (int i = jsonArray.size(); i <= index; i++) {
                    jsonArray.add(null);
                }
            }
            jsonArray.set(index, value);
        } else {
            jsonArray.add(value);
        }
        return jsonArray;
    }

    public JsonObject writeIntoJsonObject(JsonObject jsonObject, List<Object> keychain, Object value) {
        if (keychain == null || keychain.size() <= 0) {
            throw new RuntimeException();
        }
        if (keychain.size() == 1) {
            writeIntoJsonObject(jsonObject, String.valueOf(keychain.get(0)), value);
            return jsonObject;
        }

        Object key = keychain.get(0);
        Object nextKey = keychain.get(1);
        List<Object> nestedKeychain = keychain.subList(1, keychain.size());

        if (jsonObject.containsKey(String.valueOf(key))) {
            Object nextObject = jsonObject.getValue(String.valueOf(key));
            if (nextObject instanceof JsonObject) {
                writeIntoJsonObject((JsonObject) nextObject, nestedKeychain, value);
            } else if (nextObject instanceof JsonArray) {
                writeIntoJsonArray((JsonArray) nextObject, nestedKeychain, value);
            } else {
                throw new RuntimeException();
            }
        } else {
            if (nextKey instanceof Long || nextKey instanceof Integer || nextKey instanceof Short) {
                JsonArray array = new JsonArray();
                jsonObject.put(String.valueOf(key), array);
                writeIntoJsonArray(array, nestedKeychain, value);
            } else {
                JsonObject object = new JsonObject();
                jsonObject.put(String.valueOf(key), object);
                writeIntoJsonObject(object, nestedKeychain, value);
            }
        }

        return jsonObject;
    }

    public JsonArray writeIntoJsonArray(JsonArray jsonArray, List<Object> keychain, Object value) {
        if (keychain == null || keychain.size() <= 0) {
            throw new RuntimeException();
        }
        if (keychain.size() == 1) {
            Object key = keychain.get(0);
            if (key instanceof Long || key instanceof Integer || key instanceof Short) {
                int index = ((Number) key).intValue();
                writeIntoJsonArray(jsonArray, index, value);
                return jsonArray;
            } else {
                throw new RuntimeException();
            }
        }

        Object key = keychain.get(0);
        Object nextKey = keychain.get(1);
        List<Object> nestedKeychain = keychain.subList(1, keychain.size());

        if (key instanceof Long || key instanceof Integer || key instanceof Short) {
            int index = ((Number) key).intValue();
            if (jsonArray.size() > index && index >= 0) {
                // contains
                Object existed = jsonArray.getValue(index);
                if (existed instanceof JsonArray) {
                    if (nextKey instanceof Long || nextKey instanceof Integer || nextKey instanceof Short) {
                        writeIntoJsonArray((JsonArray) existed, nestedKeychain, value);
                    } else {
                        throw new RuntimeException();
                    }
                } else if (existed instanceof JsonObject) {
                    writeIntoJsonObject((JsonObject) existed, nestedKeychain, value);
                } else {
                    throw new RuntimeException();
                }
            } else {
                // not contained
                if (nextKey instanceof Long || nextKey instanceof Integer || nextKey instanceof Short) {
                    writeIntoJsonArray(jsonArray, index, writeIntoJsonArray(new JsonArray(), nestedKeychain, value));
                } else {
                    writeIntoJsonArray(jsonArray, index, writeIntoJsonObject(new JsonObject(), nestedKeychain, value));
                }
            }
        } else {
            throw new RuntimeException();
        }

        return jsonArray;
    }

    public Object readFromJsonObject(JsonObject jsonObject, String key) {
        return jsonObject.getValue(key);
    }

    public Object readFromJsonArray(JsonArray jsonArray, int index) {
        return jsonArray.getValue(index);
    }

    public Object readFromJsonObject(JsonObject jsonObject, List<Object> keychain) {
        if (keychain == null || keychain.isEmpty()) {
            throw new RuntimeException();
        }
        Object key = keychain.get(0);
        Object x = readFromJsonObject(jsonObject, String.valueOf(key));
        if (keychain.size() == 1) {
            return x;
        }
        List<Object> nextKeychain = keychain.subList(1, keychain.size());
        if (x instanceof JsonObject) {
            return readFromJsonObject((JsonObject) x, nextKeychain);
        } else if (x instanceof JsonArray) {
            return readFromJsonArray((JsonArray) x, nextKeychain);
        }
        throw new RuntimeException();
    }

    public Object readFromJsonArray(JsonArray jsonArray, List<Object> keychain) {
        if (keychain == null || keychain.isEmpty()) {
            throw new RuntimeException();
        }
        Object key = keychain.get(0);
        if (key instanceof Long || key instanceof Integer || key instanceof Short) {
            Object x = readFromJsonArray(jsonArray, ((Number) key).intValue());
            if (keychain.size() == 1) {
                return x;
            }
            List<Object> nextKeychain = keychain.subList(1, keychain.size());
            if (x instanceof JsonObject) {
                return readFromJsonObject((JsonObject) x, nextKeychain);
            } else if (x instanceof JsonArray) {
                return readFromJsonArray((JsonArray) x, nextKeychain);
            }
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }

    /**
     * @since 2.4
     */
    private JsonArray getSortedJsonArray(JsonArray array) {
        List<Object> list = new ArrayList<>();
        array.forEach(list::add);
        list.sort(Comparator.comparing(Object::toString));
        return new JsonArray(list);
    }

    /**
     * @since 2.4
     */
    public String getJsonForArrayWhoseItemsSorted(JsonArray array) {
        return getSortedJsonArray(array).toString();
    }

    /**
     * @since 2.4
     */
    private JsonObject getSortedJsonObject(JsonObject object) {
        JsonObject result = new JsonObject();
        List<String> keyList = new ArrayList<>(object.getMap().keySet());
        keyList.sort(Comparator.naturalOrder());
        keyList.forEach(key -> {
            Object value = object.getValue(key);
            if (value instanceof JsonObject) {
                result.put(key, getSortedJsonObject((JsonObject) value));
            } else if (value instanceof JsonArray) {
                result.put(key, getSortedJsonArray((JsonArray) value));
            } else {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * @since 2.4
     */
    public String getJsonForObjectWhoseItemKeysSorted(JsonObject object) {
        return getSortedJsonObject(object).toString();
    }

    /**
     * @since 3.0.0
     */
    public JsonObject renderThrowableChain(Throwable throwable) {
        return renderThrowableChain(throwable, new HashSet<>());
    }

    /**
     * @since 2.9
     */
    public JsonObject renderThrowableChain(Throwable throwable, Set<String> ignorableStackPackageSet) {
        if (throwable == null) return null;

        Throwable cause = throwable.getCause();
        JsonObject x = new JsonObject()
                .put("class", throwable.getClass().getName())
                .put("message", throwable.getMessage())
                .put("stack", filterStackTraceToJsonArray(throwable.getStackTrace(), ignorableStackPackageSet))
                .put("cause", null);

        JsonObject upper = x;
        while (cause != null) {
            JsonObject current = new JsonObject()
                    .put("class", cause.getClass().getName())
                    .put("message", cause.getMessage())
                    .put("stack", filterStackTraceToJsonArray(cause.getStackTrace(), ignorableStackPackageSet))
                    .put("cause", null);
            upper.put("cause", current);
            upper = current;

            cause = cause.getCause();
        }
        return x;
    }

    public void filterStackTrace(
            StackTraceElement[] stackTrace,
            Set<String> ignorableStackPackageSet,
            BiConsumer<String, Integer> ignoredStackTraceItemsConsumer,
            Consumer<StackTraceElement> stackTraceItemConsumer
    ) {
        if (stackTrace != null) {
            String ignoringClassPackage = null;
            int ignoringCount = 0;
            for (StackTraceElement stackTranceItem : stackTrace) {
                String className = stackTranceItem.getClassName();
                String matchedClassPackage = null;
                for (String cp : ignorableStackPackageSet) {
                    if (className.startsWith(cp)) {
                        matchedClassPackage = cp;
                        break;
                    }
                }
                if (matchedClassPackage == null) {
                    if (ignoringCount > 0) {
                        ignoredStackTraceItemsConsumer.accept(ignoringClassPackage, ignoringCount);
                        ignoringClassPackage = null;
                        ignoringCount = 0;
                    }

                    stackTraceItemConsumer.accept(stackTranceItem);
                } else {
                    if (ignoringCount > 0) {
                        if (ignoringClassPackage.equals(matchedClassPackage)) {
                            ignoringCount += 1;
                        } else {
                            ignoredStackTraceItemsConsumer.accept(ignoringClassPackage, ignoringCount);
                            ignoringClassPackage = matchedClassPackage;
                            ignoringCount = 1;
                        }
                    } else {
                        ignoringClassPackage = matchedClassPackage;
                        ignoringCount = 1;
                    }
                }
            }
            if (ignoringCount > 0) {
                ignoredStackTraceItemsConsumer.accept(ignoringClassPackage, ignoringCount);
            }
        }
    }

    /**
     * @since 2.9 original name: buildStackChainText
     * @since 3.0.0 become private and renamed to filterStackTraceToJsonArray
     */
    private JsonArray filterStackTraceToJsonArray(StackTraceElement[] stackTrace, Set<String> ignorableStackPackageSet) {
        JsonArray array = new JsonArray();

        filterStackTrace(stackTrace, ignorableStackPackageSet, new BiConsumer<String, Integer>() {
            @Override
            public void accept(String ignoringClassPackage, Integer ignoringCount) {
                array.add(new JsonObject()
                        .put("type", "ignored")
                        .put("package", ignoringClassPackage)
                        .put("count", ignoringCount)
                );
            }
        }, new Consumer<StackTraceElement>() {
            @Override
            public void accept(StackTraceElement stackTranceItem) {
                array.add(new JsonObject()
                        .put("type", "call")
                        .put("class", stackTranceItem.getClassName())
                        .put("method", stackTranceItem.getMethodName())
                        .put("file", stackTranceItem.getFileName())
                        .put("line", stackTranceItem.getLineNumber())
                );
            }
        });

//        if (stackTrace != null) {
//            String ignoringClassPackage = null;
//            int ignoringCount = 0;
//            for (StackTraceElement stackTranceItem : stackTrace) {
//                String className = stackTranceItem.getClassName();
//                String matchedClassPackage = null;
//                for (var cp : ignorableStackPackageSet) {
//                    if (className.startsWith(cp)) {
//                        matchedClassPackage = cp;
//                        break;
//                    }
//                }
//                if (matchedClassPackage == null) {
//                    if (ignoringCount > 0) {
//                        array.add(new JsonObject()
//                                .put("type", "ignored")
//                                .put("package", ignoringClassPackage)
//                                .put("count", ignoringCount)
//                        );
//
//                        ignoringClassPackage = null;
//                        ignoringCount = 0;
//                    }
//
//                    array.add(new JsonObject()
//                            .put("type", "call")
//                            .put("class", stackTranceItem.getClassName())
//                            .put("method", stackTranceItem.getMethodName())
//                            .put("file", stackTranceItem.getFileName())
//                            .put("line", stackTranceItem.getLineNumber())
//                    );
//                } else {
//                    if (ignoringCount > 0) {
//                        if (ignoringClassPackage.equals(matchedClassPackage)) {
//                            ignoringCount += 1;
//                        } else {
//                            array.add(new JsonObject()
//                                    .put("type", "ignored")
//                                    .put("package", ignoringClassPackage)
//                                    .put("count", ignoringCount)
//                            );
//
//                            ignoringClassPackage = matchedClassPackage;
//                            ignoringCount = 1;
//                        }
//                    } else {
//                        ignoringClassPackage = matchedClassPackage;
//                        ignoringCount = 1;
//                    }
//                }
//            }
//            if (ignoringCount > 0) {
//                array.add(new JsonObject()
//                        .put("type", "ignored")
//                        .put("package", ignoringClassPackage)
//                        .put("count", ignoringCount)
//                );
//            }
//        }
        return array;
    }

    /**
     * @since 3.0.0
     */
    public String renderJsonToStringBlock(String name, Object object) {
        if (object == null) {
            return "null";
        }
        return renderJsonItem(name, object, 0, null);
    }

    /**
     * @param key    Key of entry amongst the entries, or the index amongst the array.
     * @param object Value of entry amongst the entries, or the item amongst the array.
     * @return rendered string block ended with NEW_LINE.
     */
    private String renderJsonItem(String key, Object object, int indentation, String typeMark) {
        StringBuilder subBlock = new StringBuilder();
        if (indentation > 1) {
            for (int i = 0; i < indentation - 2; i++) {
                subBlock.append(" ");
            }
            subBlock.append(typeMark).append(" ");
        } else {
            for (int i = 0; i < Math.max(0, indentation); i++) {
                subBlock.append(" ");
            }
        }

        if (key != null) {
            subBlock.append(key).append(": ");
        }
        if (object instanceof JsonObject) {
            subBlock.append("\n");
            ((JsonObject) object).forEach(entry -> {
                subBlock.append(
                        renderJsonItem(entry.getKey(), entry.getValue(), indentation + 2, "+")
                );
            });
        } else if (object instanceof JsonArray) {
            subBlock.append("\n");
            for (int i = 0; i < ((JsonArray) object).size(); i++) {
                subBlock.append(
                        renderJsonItem(String.valueOf(i), ((JsonArray) object).getValue(i), indentation + 2, "-")
                );
            }
        } else {
            subBlock.append(object).append("\n");
        }
        return subBlock.toString();
    }
}
