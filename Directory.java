import java.util.ArrayList;

public class Directory extends FileSystemElement {
    ArrayList<FileSystemElement> contents;

    public Directory(String name, FileSystemElement parent) {
        this.name = "";
        this.contents = new ArrayList<>();
        this.setName(name);
        this.setParent(parent);
    }

    public boolean mkdir(String path) {
        return this.insert(path, new Directory("", null));
    }

    public boolean touch(String path) {
        return this.insert(path, new File("", "", null));
    }

    private boolean insert(String path, FileSystemElement newElement) {
        if (!this.validPath(path)) {
            return false;
        }

        name = "";
        int idx = path.lastIndexOf("/");
        if (idx == -1) {
            name = path;
        }
        name = path.substring(idx + 1);

        Directory targetDir;
        if (idx == -1) {
            targetDir = this;
        } else {
            FileSystemElement result = this.getElementByPath(path.substring(0, idx));
            if (!(result instanceof Directory)) {
                return false;
            }
            targetDir = (Directory) result;
        }

        for (FileSystemElement element : targetDir.contents) {
            if (element.name.equals(name)) {
                return false;
            }
        }

        newElement.setName(name);
        newElement.setParent(targetDir);
        targetDir.contents.add(newElement);
        return true;
    }

    // this implementation does not account for removal of the root directory
    public FileSystemElement remove(String path) {
        if (!this.validPath(path)) {
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
        if (!this.validPath(srcPath) || !this.validPath(destPath)) {
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
        src.setName(dest.name);
        return true;
    }

    /*
     * returns true if the directory contains either a directory or file at the
     * specified relative path
     */
    public boolean exists(String path) {
        if (!this.validPath(path)) {
            return false;
        }

        FileSystemElement result = getElementByPath(path);
        if (result == null) {
            return false;
        }
        return true;
    }

    public int size() {
        if (this.contents.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (FileSystemElement element : this.contents) {
            if (element instanceof File) {
                sum += ((File) element).size();
            } else {
                sum += ((Directory) element).size();
            }
        }
        return sum;
    }

    // this method validates and normalizes a path. This is a helper function and
    // not part of the public api.
    private boolean validPath(String path) {
        if (path == null) {
            return false;
        }
        // paths may not have leading or trailing slashes
        if (path.charAt(0) == '/' || path.charAt(path.length() - 1) == '/') {
            return false;
        }

        return true;
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

}
