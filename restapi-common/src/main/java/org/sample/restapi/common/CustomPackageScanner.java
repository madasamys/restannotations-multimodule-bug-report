package org.sample.restapi.common;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.ResourcePath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Madasamy
 * @since x.x
 */
public class CustomPackageScanner
{
    private static Logger logger = LoggerFactory.getLogger(CustomPackageScanner.class);

    public static void scanPackage(String... packageNames)
    {
        Args.notNull(packageNames, "packageNames");

        scanPackage(WebApplication.get(), packageNames);
    }

    public static void scanPackage(WebApplication application, String... packageNames)
    {
        Args.notNull(application, "application");
        Args.notNull(packageNames, "packageNames");

        for (String packageName : packageNames) {
            scanPackage(application, packageName);
        }
    }

    public static void scanPackage(WebApplication application, String packageName)
    {
        Args.notNull(application, "application");
        Args.notNull(packageName, "packageName");

        try {
            Class<?>[] packageClasses = getClasses(packageName);
            logger.info("Number of  classes: {}", packageClasses.length);
            for (Class<?> clazz : packageClasses) {
                mountAnnotatedResource(application, clazz);
            }

        } catch (Exception ex) {
            logger.error("Error occurred while scanning package", ex);
        }
    }

    private static void mountAnnotatedResource(WebApplication application, Class<?> clazz)
            throws InstantiationException, IllegalAccessException
    {
        ResourcePath mountAnnotation = clazz.getAnnotation(ResourcePath.class);

        if (mountAnnotation == null || !IResource.class.isAssignableFrom(clazz)) {
            return;
        }

        String path = mountAnnotation.value();
        final IResource resourceInstance = (IResource) clazz.newInstance();
        logger.info("Class name: {}", clazz.getSimpleName());
        application.mountResource(path, new ResourceReference(clazz.getSimpleName())
        {
            @Override
            public IResource getResource()
            {
                return resourceInstance;
            }
        });

        logger.info("Resource '" + clazz.getSimpleName() + "' has been mounted to path '" + path
                + "'");
    }

    private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException,
            IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Args.notNull(classLoader, "classLoader");
        logger.info("Package name: {}", packageName);
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        logger.info("Number of directory: {}", dirs.size());
        for (File directory : dirs) {
            logger.info("Directory name: {}", directory);
            classes.addAll(findClasses(directory, packageName));
        }

        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName)
            throws ClassNotFoundException
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!directory.exists()) {
            logger.info("Directory does not exists: {}", directory);
            return classes;
        }

        File[] files = directory.listFiles();
        logger.info("No of files: {}", files.length);
        for (File file : files) {
            logger.info("File name: {}", file);
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.'
                        + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }

}
