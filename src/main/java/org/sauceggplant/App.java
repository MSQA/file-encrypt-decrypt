package org.sauceggplant;

import org.sauceggplant.crypto.Crypto;
import org.sauceggplant.io.FileSplit;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class App {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        FileSplit.split("/Users/jacob/Workspaces/test", "/Users/jacob/Workspaces/123/", 8192);
        FileSplit.merge("/Users/jacob/Workspaces/123/", "test1");
        System.out.println(Crypto.summaryFile("/Users/jacob/Workspaces/123/test1", "MD5"));
        System.out.println(Crypto.summaryFile("/Users/jacob/Workspaces/test", "MD5"));
    }
}