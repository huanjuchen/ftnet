package xyz.huanju.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.File;

@SpringBootTest
class FtnetApplicationTests {

    static char concat = '/';


    @Test
    void contextLoads() {


    }

    @Test
    void mut(){
    }



//    public void showFile(File file) {
//        String path = file.getAbsolutePath().replace('\\', '/');
//        System.out.println(path);
//        if (!file.isDirectory()){
//            return;
//        }
//        String[] fileList = file.list();
//        if (fileList == null) {
//            return;
//        }
//        for (String str : fileList) {
//            StringBuilder subPath = new StringBuilder();
//            subPath.append(path).append(concat).append(str);
//            File subFile = new File(subPath.toString());
//            if (!subFile.canRead()) {
//                break;
//            }
//            showFile(subFile);
//        }
//
//
//    }

}
