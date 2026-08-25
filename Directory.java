import java.util.ArrayList;

public class Directory extends FileSystemElement {
    ArrayList<FileSystemElement> contents;

    public Directory(String name, FileSystemElement parent) {

    }

    public boolean insert(String path, FileSystemElement newElement) {
        path = this.validPath(path);
        if (path == null) {
            return false;
        }
        Directory targetDir = this.getParentByPath(path);
        if (targetDir == null) {
            return false;
        }

        for (FileSystemElement element : targetDir.contents) {
            if (element.name.equals(newElement.name)) {
                return false;
            }
        }
        targetDir.contents.add(newElement);
        return true;

    }

    // this implementation does not account for removal of the root directory
    public FileSystemElement remove(String path) {
        path = this.validPath(path);
        if (path == null) {
            return null;
        }
        FileSystemElement target = this.getElementByPath(path);
        if (target == null) {
            return null;
        }

        Directory parentDir = (Directory) target.parent;
        parentDir.contents.remove(target);
        return target;
    }

    public boolean move(String srcPath, String destPath) {
        srcPath = this.validPath(srcPath);
        destPath = this.validPath(destPath);
        if (srcPath == null || destPath == null) {
            return false;
        }

        FileSystemElement src = this.getElementByPath(srcPath);
        FileSystemElement dest = this.getElementByPath(destPath);
        if (src == null || dest == null) {
            return false;
        }

        this.remove(destPath);
        this.insert(destPath, src);
        this.remove(srcPath);
        return true;
    }

    /*
     * returns true if the directory contains either a directory or file at the
     * specified relative path
     */
    public boolean exists(String relativePath) {
        relativePath = this.validPath(relativePath);
        if (relativePath == null) {
            return false;
        }

        FileSystemElement result = getElementByPath(relativePath);
        if (result == null) {
            return false;
        }
        return true;
    }

    // this method validates and normalizes a path. This is a helper function and
    // not part of the public api.
    private String validPath(String path) {

        // paths may not have leading or trailing slashes
        if (path.charAt(0) == '/' || path.charAt(path.length() - 1) == '/') {
            return null;
        }

        return path;
    }

    private FileSystemElement getElementByPath(String path) {
        if (path == null) {
            return null;
        }

        int idx = path.indexOf("/");
        // no slash case
        if (idx == -1) {
            for (FileSystemElement element : this.contents) {
                if (element.name.equals(path)) {
                    return element;
                }
            }
            return null;
        }

        // has at least one dir in path
        String dirName = path.substring(0, idx);
        String newPath = path.substring(idx + 1);
        FileSystemElement target = null;
        for (FileSystemElement element : this.contents) {
            if (element.name.equals(dirName)) {
                target = element;
                break;
            }
        }

        if (!(target instanceof Directory)) {
            return null;
        }
        Directory newDir = (Directory) target;
        return newDir.getElementByPath(newPath);
    }

    private Directory getParentByPath(String path) {
        int idx = path.lastIndexOf("/");
        if (idx == -1) {
            return this;
        }

        FileSystemElement result = getElementByPath(path.substring(0, idx));
        if (result == null) {
            return null;
        }

        if (!(result instanceof Directory)) {
            return null;
        }
        return (Directory) result;

    }

}
