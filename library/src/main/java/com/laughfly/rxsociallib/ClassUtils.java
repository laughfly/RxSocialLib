package com.laughfly.rxsociallib;

import android.support.v4.util.ArraySet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * Created by HASEE on 2017/5/11 17:25
 */

public class ClassUtils {
    /**
     * @param c 接口
     * @return List<Class>    实现接口的所有类
     * @Description: 根据一个接口返回该接口的所有类
     */
    @SuppressWarnings("unchecked")
    public static<T> Set<Class<? extends T>> getAllClassByInterface(Class<T> c, String packageName) {
        Set returnClassList = new ArraySet();
        try {
            List<Class> allClass = getClasses(packageName, c);//获得当前包以及子包下的所有类(android中用)

            //判断是否是一个接口
            for (int i = 0; i < allClass.size(); i++) {
                if (c.isAssignableFrom(allClass.get(i))) {
                    if (!c.equals(allClass.get(i))) {
                        returnClassList.add(allClass.get(i));
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
//        }
        return returnClassList;
    }

    /**
     * @return List<Class>    包下所有类
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     *               获得当前包以及子包下的所有类(android中用)
     */
    private static List<Class> getClasses(String packageName, Class<?> clazz) throws ClassNotFoundException,
        IOException {

        ArrayList<Class> classes = new ArrayList<Class>();
        List<DexFile> dexFiles = new ArrayList<>();
        try {
            BaseDexClassLoader classLoader = ((BaseDexClassLoader) Thread.currentThread().getContextClassLoader());
            Field pathListField = classLoader.getClass().getSuperclass().getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(classLoader);

            Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object dexElements = dexElementsField.get(pathList);
            int dexLength = Array.getLength(dexElements);
            Field dexFileField = null;

            for (int i = 0; i < dexLength; i++) {
                Object dexElement = Array.get(dexElements, i);
                if (dexFileField == null) {
                    dexFileField = dexElement.getClass().getDeclaredField("dexFile");
                    dexFileField.setAccessible(true);
                }
                DexFile dexFile = (DexFile) dexFileField.get(dexElement);
                if (dexFile != null) {
                    dexFiles.add(dexFile);
                }
            }

            for (DexFile file : dexFiles) {
                for (Enumeration<String> entries = file.entries(); entries.hasMoreElements(); ) {
                    final String s1 = entries.nextElement();
                    if (s1.contains(packageName)) {
                        if (clazz.isAssignableFrom(Class.forName(s1))) {
                            classes.add(Class.forName(s1));
                        }
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return classes;
    }


    /**
     * @return List<Class>    包下所有类
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     */
    private static List<Class> getClasses1(String packageName) throws ClassNotFoundException,
        IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String newPath = resource.getFile().replace("%20", " ");
            dirs.add(new File(newPath));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    private static List<Class> findClass(File directory, String packageName)
        throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() -
                    6)));
            }
        }
        return classes;
    }
}

