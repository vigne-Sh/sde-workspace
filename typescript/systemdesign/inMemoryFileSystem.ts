/*
In-memory file system supporting mkdir, addFile/write, ls, and readFile
over a tree of directory and file nodes, addressed by absolute Unix-style
paths ("/a/b/c").

status - completed
*/

import { logp } from "../utils/logger";

type FsNode = DirectoryNode | FileNode;

class DirectoryNode {
  readonly type = "directory" as const;
  readonly children: Map<string, FsNode> = new Map();
}

class FileNode {
  readonly type = "file" as const;
  content = "";
}

function splitPath(path: string): string[] {
  return path.split("/").filter((segment) => segment.length > 0);
}

class InMemoryFileSystem {
  private readonly root = new DirectoryNode();

  private resolveDir(segments: string[], createMissing: boolean): DirectoryNode {
    let current = this.root;
    for (const segment of segments) {
      let next = current.children.get(segment);
      if (!next) {
        if (!createMissing) {
          throw new Error(`no such directory: ${segment}`);
        }
        next = new DirectoryNode();
        current.children.set(segment, next);
      }
      if (next.type !== "directory") {
        throw new Error(`not a directory: ${segment}`);
      }
      current = next;
    }
    return current;
  }

  mkdir(path: string): void {
    this.resolveDir(splitPath(path), true);
  }

  writeFile(path: string, content: string): void {
    const segments = splitPath(path);
    const fileName = segments.pop();
    if (!fileName) {
      throw new Error("path must include a file name");
    }
    const dir = this.resolveDir(segments, true);
    const existing = dir.children.get(fileName);
    if (existing && existing.type !== "file") {
      throw new Error(`not a file: ${fileName}`);
    }
    const file = existing ?? new FileNode();
    file.content = content;
    dir.children.set(fileName, file);
  }

  readFile(path: string): string {
    const segments = splitPath(path);
    const fileName = segments.pop();
    if (!fileName) {
      throw new Error("path must include a file name");
    }
    const dir = this.resolveDir(segments, false);
    const node = dir.children.get(fileName);
    if (!node || node.type !== "file") {
      throw new Error(`no such file: ${path}`);
    }
    return node.content;
  }

  ls(path: string): string[] {
    const dir = this.resolveDir(splitPath(path), false);
    return Array.from(dir.children.keys()).sort();
  }
}

// usage scenarios

const fs = new InMemoryFileSystem();
fs.mkdir("/home/user/docs");
fs.writeFile("/home/user/docs/notes.txt", "first draft");
fs.writeFile("/home/user/todo.txt", "buy milk");
fs.mkdir("/home/user/docs/archive");

logp(`ls("/home/user") -> ${JSON.stringify(fs.ls("/home/user"))}`);
logp(`ls("/home/user/docs") -> ${JSON.stringify(fs.ls("/home/user/docs"))}`);
logp(`readFile("/home/user/docs/notes.txt") -> "${fs.readFile("/home/user/docs/notes.txt")}"`);

fs.writeFile("/home/user/docs/notes.txt", "revised draft");
logp(`readFile after overwrite -> "${fs.readFile("/home/user/docs/notes.txt")}"`);

try {
  fs.readFile("/home/user/docs/missing.txt");
} catch (err) {
  logp(`readFile on missing file threw as expected: ${(err as Error).message}`);
}

try {
  fs.mkdir("/home/user/todo.txt/nested");
} catch (err) {
  logp(`mkdir under a file threw as expected: ${(err as Error).message}`);
}
