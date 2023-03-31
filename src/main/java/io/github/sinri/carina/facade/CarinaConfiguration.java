package io.github.sinri.carina.facade;

import io.github.sinri.carina.core.json.JsonifiableEntity;
import io.github.sinri.carina.helper.CarinaHelpers;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @since 3.0.0
 */
public class CarinaConfiguration implements JsonifiableEntity<CarinaConfiguration> {

    private JsonObject data = new JsonObject();

    public CarinaConfiguration() {
    }

    public CarinaConfiguration(@Nonnull JsonObject jsonObject) {
        this.data = jsonObject;
    }

    public CarinaConfiguration(@Nonnull Properties properties) {
        this.data = CarinaConfiguration.transformPropertiesToJsonObject(properties);
    }

    static @Nonnull JsonObject transformPropertiesToJsonObject(Properties properties) {
        JsonObject jsonObject = new JsonObject();

        Set<String> plainKeySet = new HashSet<>();
        properties.forEach((key, value) -> plainKeySet.add(key.toString()));

        for (var plainKey : plainKeySet) {
            String[] components = plainKey.split("\\.");
            List<Object> keychain = Arrays.asList(components);
            CarinaHelpers.jsonHelper()
                    .writeIntoJsonObject(jsonObject, keychain, properties.getProperty(plainKey));
        }
        return jsonObject;
    }

    static CarinaConfiguration createFromPropertiesFile(String propertiesFileName) {
        CarinaConfiguration p = new CarinaConfiguration();
        p.loadPropertiesFile(propertiesFileName);
        return p;
    }

    static CarinaConfiguration createFromProperties(Properties properties) {
        CarinaConfiguration p = new CarinaConfiguration();
        p.putAll(properties);
        return p;
    }

    static CarinaConfiguration createFromJsonObject(JsonObject jsonObject) {
        CarinaConfiguration p = new CarinaConfiguration();
        p.putAll(jsonObject);
        return p;
    }

    public CarinaConfiguration putAll(CarinaConfiguration CarinaConfiguration) {
        return putAll(CarinaConfiguration.toJsonObject());
    }

    public CarinaConfiguration putAll(Properties properties) {
        return putAll(CarinaConfiguration.transformPropertiesToJsonObject(properties));
    }

    public CarinaConfiguration putAll(JsonObject jsonObject) {
        data.mergeIn(jsonObject);
        return this;
    }

    public CarinaConfiguration loadPropertiesFile(String propertiesFileName) {
        Properties properties = new Properties();
        try {
            // here, the file named as `propertiesFileName` should be put along with JAR
            properties.load(new FileReader(propertiesFileName));
        } catch (IOException e) {
            System.err.println("Cannot find the file config.properties. Use the embedded one.");
            try {
                properties.load(CarinaConfiguration.class.getClassLoader().getResourceAsStream(propertiesFileName));
            } catch (IOException ex) {
                throw new RuntimeException("Cannot find the embedded file config.properties.", ex);
            }
        }

        return putAll(properties);
    }

    public CarinaConfiguration extract(String... keychain) {
        JsonObject entries = readJsonObject(keychain);
        if (entries == null) {
            entries = new JsonObject();
        }
        JsonObject jsonObject = entries;
        return new CarinaConfiguration(jsonObject);
    }

    public @Nullable Long readAsLong(String... keychain) {
        String s = readString(keychain);
        return s == null ? null : Long.valueOf(s);
    }

    public @Nullable Integer readAsInteger(String... keychain) {
        String s = readString(keychain);
        return s == null ? null : Integer.valueOf(s);
    }

    /**
     * Parse TRUE/FALSE to boolean ignoring case.
     */
    public @Nullable Boolean readAsBoolean(String... keychain) {
        String s = readString(keychain);
        return s == null ? null : Boolean.valueOf(s);
    }

    @Override
    public @NotNull JsonObject toJsonObject() {
        return this.data;
    }

    @Override
    public @NotNull CarinaConfiguration reloadDataFromJsonObject(JsonObject data) {
        this.data = data;
        return this;
    }
}
