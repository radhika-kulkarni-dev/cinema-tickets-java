package uk.gov.dwp.uc.pairtest.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationProvider {

    private static final String CONFIG_FILE = "application.properties";

    public static final int MAX_TICKETS;
    public static final int ADULT_PRICE;
    public static final int CHILD_PRICE;
    public static final int INFANT_PRICE;

    static {
        Properties props = loadFromClasspath();
        MAX_TICKETS  = requirePositiveInt(props, "ticket.max.tickets");
        ADULT_PRICE  = requireNonNegativeInt(props, "ticket.price.adult");
        CHILD_PRICE  = requireNonNegativeInt(props, "ticket.price.child");
        INFANT_PRICE = requireNonNegativeInt(props, "ticket.price.infant");
    }

    private ConfigurationProvider() {}

    private static Properties loadFromClasspath() {
        Properties props = new Properties();
        try (InputStream is = ConfigurationProvider.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new IllegalStateException(
                        "Configuration file not found on classpath: " + CONFIG_FILE);
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load configuration file: " + CONFIG_FILE, e);
        }
        return props;
    }

    private static int requirePositiveInt(Properties props, String key) {
        int value = parseIntProperty(props, key);
        if (value <= 0) {
            throw new IllegalStateException(
                    "Configuration property '" + key + "' must be a positive integer, got: " + value);
        }
        return value;
    }

    private static int requireNonNegativeInt(Properties props, String key) {
        int value = parseIntProperty(props, key);
        if (value < 0) {
            throw new IllegalStateException(
                    "Configuration property '" + key + "' must be non-negative, got: " + value);
        }
        return value;
    }

    private static int parseIntProperty(Properties props, String key) {
        String raw = props.getProperty(key);
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException(
                    "Required configuration property is missing: " + key);
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Configuration property '" + key + "' is not a valid integer: " + raw);
        }
    }
}