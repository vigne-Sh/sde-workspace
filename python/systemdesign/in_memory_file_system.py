"""
In-memory file system supporting mkdir, write file, ls, and read,
backed by a tree of directory nodes each holding child directories
and files, similar to the classic "design in-memory file system"
interview problem.

status - completed
"""

from base_logger.logging_event import create_logger


class DirectoryNode:

    def __init__(self):
        self.directories = {}
        self.files = {}


class InMemoryFileSystem:

    log = create_logger(__name__)

    def __init__(self):
        self.root = DirectoryNode()

    def _split(self, path):
        return [part for part in path.strip("/").split("/") if part]

    def _resolve_dir(self, parts, create=False):
        node = self.root
        for part in parts:
            if part not in node.directories:
                if not create:
                    raise FileNotFoundError(f"directory not found: {part}")
                node.directories[part] = DirectoryNode()
            node = node.directories[part]
        return node

    def mkdir(self, path):
        parts = self._split(path)
        self._resolve_dir(parts, create=True)

    def write_file(self, path, content):
        parts = self._split(path)
        if not parts:
            raise ValueError("path must include a file name")
        *dir_parts, file_name = parts
        directory = self._resolve_dir(dir_parts, create=True)
        directory.files[file_name] = content

    def read_file(self, path):
        parts = self._split(path)
        *dir_parts, file_name = parts
        directory = self._resolve_dir(dir_parts, create=False)
        if file_name not in directory.files:
            raise FileNotFoundError(f"file not found: {path}")
        return directory.files[file_name]

    def ls(self, path):
        parts = self._split(path)
        if parts and parts[-1] in self._resolve_dir(parts[:-1], create=False).files:
            return [parts[-1]]
        directory = self._resolve_dir(parts, create=False)
        return sorted(list(directory.directories.keys()) + list(directory.files.keys()))


if __name__ == "__main__":
    log = create_logger("in_memory_fs_demo")

    fs = InMemoryFileSystem()
    fs.mkdir("/a/b/c")
    fs.write_file("/a/b/c/hello.txt", "hello world")
    fs.write_file("/a/readme.md", "top level readme")

    log.info("ls('/a') -> %s", fs.ls("/a"))
    log.info("ls('/a/b/c') -> %s", fs.ls("/a/b/c"))
    log.info("read('/a/b/c/hello.txt') -> %s", fs.read_file("/a/b/c/hello.txt"))
    log.info("ls('/a/readme.md') -> %s (ls on a file returns just that file)",
             fs.ls("/a/readme.md"))

    try:
        fs.read_file("/a/b/c/missing.txt")
    except FileNotFoundError as exc:
        log.info("reading missing file raised: %s", exc)

    try:
        fs.read_file("/no/such/dir/file.txt")
    except FileNotFoundError as exc:
        log.info("reading from missing directory raised: %s", exc)
