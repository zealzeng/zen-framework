package org.zenframework.web.concat;

import org.apache.commons.io.FilenameUtils;

public class TestConcat {

    public static void main(String[] args) throws Exception {
        String string = "foo/../../bar";
        System.out.println(FilenameUtils.normalize(string, true) == null);
    }
}
