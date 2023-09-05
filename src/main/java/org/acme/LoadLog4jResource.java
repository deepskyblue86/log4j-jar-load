package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Path("/load-jars")
public class LoadLog4jResource {

    @ConfigProperty(name = "packages.location")
    String PACKAGES_LOCATION;

    public static final String DEFAULT_RESPONSE_STRING = "Loaded and used vulnerable log4j";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String load() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Paths of log4j jars
        var jarPaths = new java.nio.file.Path[] {
                Paths.get(PACKAGES_LOCATION + "log4j-api-2.14.1.jar"),
                Paths.get(PACKAGES_LOCATION + "log4j-core-2.14.1.jar"),
        };

        // Check them and put them into a list of URLs
        ArrayList<URL> urls = new ArrayList<>();
        for (var jarPath : jarPaths) {
            var existsAndIsReadable = Files.isReadable(jarPath) && Files.isRegularFile(jarPath);
            if (!existsAndIsReadable) {
                throw new IOException();
            }
            urls.add(jarPath.toUri().toURL());
        }

        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]));

        // This is the equivalent of logger = LogManager.getRootLogger()
        Class<?> logManagerClass = Class.forName("org.apache.logging.log4j.LogManager", true, loader);
        Object logger = logManagerClass.getMethod("getRootLogger")
                .invoke(null);

        // logger.error("Hello from vulnerable log4j!")
        // using error level to make it appear in the console
        logger.getClass().getMethod("error", Object.class)
                .invoke(logger, "Hello from vulnerable log4j!");

        loader.close();

        return DEFAULT_RESPONSE_STRING;
    }
}
