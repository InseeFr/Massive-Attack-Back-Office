package fr.insee.sabianedata.ws.utils;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputStreamUtil {
    private static final Logger logger = LoggerFactory.getLogger(InputStreamUtil.class);

    private InputStreamUtil() {
        // to hide public default constructor
    }

    /**
     * Generic file getter from classpath
     *
     * @return the file or null when not found.
     */
    public static InputStream getInputStreamFromPath(String path) {
        InputStream streamFromPath = InputStreamUtil.class.getResourceAsStream(path);

        if (streamFromPath == null) {
            String error = String.format("Can't load %s", path);
            logger.warn(error);
            throw new NullPointerException(error);
        }
        return streamFromPath;
    }


}
