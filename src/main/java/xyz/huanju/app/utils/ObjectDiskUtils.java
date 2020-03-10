package xyz.huanju.app.utils;

import java.io.*;

/**
 * @author HuanJu
 * @date 2020/3/4 23:53
 */
public class ObjectDiskUtils {

    private static String objectFolder;

    static {
        String baseFolder = System.getProperty("user.home").replace('\\', '/') + "/ftnet/";
        objectFolder = baseFolder + "object/";
        File file = new File(objectFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

    }


    public static void objectToFile(Object object, String fileName) {
        ObjectOutputStream objectOutputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(objectFolder + fileName);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object fileToObject(String fileName) {
        File file = new File(objectFolder + fileName);
        if (!file.exists()) {
            return null;
        }

        InputStream inputStream = null;
        ObjectInputStream ois = null;
        Object object = null;

        try {
            inputStream = new FileInputStream(file);
            ois = new ObjectInputStream(inputStream);
            object = ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

}
