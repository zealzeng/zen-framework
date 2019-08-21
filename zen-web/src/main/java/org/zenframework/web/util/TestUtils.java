package org.zenframework.web.util;

import org.zenframework.security.annotation.Logical;
import org.zenframework.security.util.AuthUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2019/4/8 0008.
 */
public class TestUtils {

    public static void testFiles() throws Exception {
        String rootDir = "D:/devops";
        Path root = Paths.get(rootDir);
        Files.walkFileTree(Paths.get(rootDir, "\\sql"), new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println(root.relativize(dir));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void main(String[] args) throws Exception {
//        String str = "/a/b/c.txt";
//        str = "/a/b/c.txt";
//        System.out.println(FilenameUtils.getPath(str));
//        System.out.println(FilenameUtils.getFullPath(str));
//        System.out.println(FilenameUtils.getFullPathNoEndSeparator(FilenameUtils.normalize(str, true)));
//        System.out.println("=====================================");
//        TestUtils.testFiles();

        Set<String> userValues = new HashSet<>();
        userValues.add("boss.abc");
        userValues.add("boss.123");
        String[] requiredValues = new String[] {"boss.abc", "boss.1*"};
        boolean ret = AuthUtils.logicalContain(userValues, requiredValues, Logical.AND);
        System.out.println(ret);


    }


}
