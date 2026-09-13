/*
In-memory file system tree supporting mkdir, addFile/write, ls and readFile style
operations, common system design / OOP interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.dev.logger.basePrinter;

class InMemoryFileSystem extends basePrinter{

    private static class FSNode {
        String name;
        boolean isFile;
        StringBuilder fileContent;
        HashMap<String, FSNode> children;

        FSNode(String name, boolean isFile){
            this.name = name;
            this.isFile = isFile;
            if (isFile){
                this.fileContent = new StringBuilder();
            } else {
                this.children = new HashMap<>();
            }
        }
    }

    private final FSNode root;

    InMemoryFileSystem(){
        this.root = new FSNode("/", false);
    }

    private String[] splitPath(String path){
        String trimmed = path.startsWith("/") ? path.substring(1) : path;
        if (trimmed.isEmpty()){
            return new String[0];
        }
        return trimmed.split("/");
    }

    void mkdir(String path){
        String[] parts = splitPath(path);
        FSNode current = root;
        for (String part : parts){
            current = current.children.computeIfAbsent(part, k -> new FSNode(part, false));
            if (current.isFile){
                throw new IllegalStateException("path component is a file, not a directory: " + part);
            }
        }
    }

    void addFile(String path, String content){
        String[] parts = splitPath(path);
        if (parts.length == 0){
            throw new IllegalArgumentException("cannot create file at root path");
        }
        FSNode current = root;
        for (int i = 0; i < parts.length - 1; i++){
            String part = parts[i];
            current = current.children.computeIfAbsent(part, k -> new FSNode(part, false));
        }
        String fileName = parts[parts.length - 1];
        FSNode fileNode = current.children.computeIfAbsent(fileName, k -> new FSNode(fileName, true));
        fileNode.isFile = true;
        if (fileNode.fileContent == null){
            fileNode.fileContent = new StringBuilder();
        }
        fileNode.fileContent.append(content);
    }

    String readFile(String path){
        FSNode node = navigateTo(path);
        if (node == null || !node.isFile){
            throw new IllegalArgumentException("no such file: " + path);
        }
        return node.fileContent.toString();
    }

    List<String> ls(String path){
        FSNode node = navigateTo(path);
        if (node == null){
            throw new IllegalArgumentException("no such path: " + path);
        }
        if (node.isFile){
            return List.of(node.name);
        }
        List<String> names = new ArrayList<>(node.children.keySet());
        Collections.sort(names);
        return names;
    }

    private FSNode navigateTo(String path){
        String[] parts = splitPath(path);
        FSNode current = root;
        for (String part : parts){
            if (current.isFile || current.children == null){
                return null;
            }
            current = current.children.get(part);
            if (current == null){
                return null;
            }
        }
        return current;
    }

    public static void main(String[] args) {
        InMemoryFileSystem fs = new InMemoryFileSystem();

        fs.mkdir("/home/user");
        fs.mkdir("/home/user/docs");
        fs.addFile("/home/user/notes.txt", "hello ");
        fs.addFile("/home/user/notes.txt", "world");
        fs.addFile("/home/user/docs/readme.md", "# readme");

        logp("ls / -> " + fs.ls("/"));
        logp("ls /home/user -> " + fs.ls("/home/user"));
        logp("readFile /home/user/notes.txt -> " + fs.readFile("/home/user/notes.txt"));
        logp("ls /home/user/docs -> " + fs.ls("/home/user/docs"));
        logp("readFile /home/user/docs/readme.md -> " + fs.readFile("/home/user/docs/readme.md"));

        try {
            fs.readFile("/home/user/missing.txt");
        } catch (IllegalArgumentException e) {
            logp("expected error reading missing file -> " + e.getMessage());
        }
    }
}
