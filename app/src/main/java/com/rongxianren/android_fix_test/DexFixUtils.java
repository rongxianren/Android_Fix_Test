package com.rongxianren.android_fix_test;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by wty on 2017/3/28.
 */

public class DexFixUtils {

    private static HashSet<File> loadDexFile = new HashSet<>();

    static{
        loadDexFile.clear();
    }

    public static void loadFixedDex(Context context) {
        File fileDir = context.getDir(MyConstant.DEX_DIR, Context.MODE_PRIVATE);
        File[] fileList = fileDir.listFiles();
        for (File value : fileList) {
            if (value.getName().startsWith("class") && value.getName().endsWith(".dex")) {
                loadDexFile.add(value);
            }
        }
        try {
            doDexInject(context, fileDir, loadDexFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doDexInject( Context context, File fileDir, HashSet<File> dexFileList) throws Exception {
        String optimized_dir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        File opt_file = new File(optimized_dir);
        if (opt_file.exists()) {
            opt_file.delete();
        }
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        for (File value : dexFileList) {
            DexClassLoader dexClassLoader = new DexClassLoader(value.getAbsolutePath(), optimized_dir, null, pathClassLoader);
            Object dexObj = getPathList(dexClassLoader);
            Object pathobj = getPathList(pathClassLoader);

            Object dexObjElements = getDexElements(dexObj);
            Object pathObjElements = getDexElements(pathobj);

            Object result = combineArray(dexObjElements, pathObjElements);

            setField(pathClassLoader,"dexElements", result);
        }
    }


    private static Object getPathList(Object obj) throws Exception{
        Class clazz = obj.getClass();
        return getField(obj, clazz, "pathList");
    }

    private static Object getDexElements(Object obj) throws Exception {
        Class clazz = obj.getClass();
        return getField(obj, clazz, "dexElements");
    }

    private static Object getField(Object obj, Class<?> clazz, String filedName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getField(filedName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.set(obj, value);
    }

    private static Object combineArray(Object dexElements, Object pathElements) {
        Class<?> clazz = dexElements.getClass().getComponentType();
        int i = Array.getLength(dexElements);
        int j = Array.getLength(pathElements);
        Object result = Array.newInstance(clazz, i + j);
        for( int k=0; k<i+j;k++) {
            if (k < i) {
                Array.set(result, k, Array.get(dexElements, k));
            } else {
                Array.set(result, k, Array.get(dexElements, k-i));
            }

        }
        return result;
    }
}
