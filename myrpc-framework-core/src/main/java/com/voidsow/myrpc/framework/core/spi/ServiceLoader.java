package com.voidsow.myrpc.framework.core.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI拓展，懒加载服务类
 */
public class ServiceLoader {
    private static final String LOADER_DIR_PREFIX = "META-INF/myrpc/services/";
    static Map<String, LinkedHashMap<String, Class<?>>> servicesOptions = new ConcurrentHashMap<>();

    public Class<?> getService(Class<?> clazz, String option, Object... args) throws IOException, ClassNotFoundException {
        var serviceOption = servicesOptions.get(clazz.getName());
        if (serviceOption == null)
            serviceOption = loadService(clazz.getName());
        return serviceOption.get(option);
    }

    public LinkedHashMap<String, Class<?>> loadService(String className) throws IOException, ClassNotFoundException {
        String path = LOADER_DIR_PREFIX + className;
        ClassLoader loader = this.getClass().getClassLoader();
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources(path);
        LinkedHashMap<String, Class<?>> options = new LinkedHashMap<>();
        if (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;
                String[] splits = line.split("=");
                options.put(splits[0], Class.forName(splits[1]));
            }
            bufferedReader.close();
            servicesOptions.put(className, options);
        }
        return options;
    }
}
