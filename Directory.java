import java.util.ArrayList;

public class Directory extends FileSystemElement {
    ArrayList<FileSystemElement> contents;

    /**
     * Constructs an empty directory with the given name and parent.
     * <p>
     * If {@code name} is invalid (null, empty, or contains {@code /}),
     * {@link #setName(String)} no-ops and the name falls back to a single
     * space ({@code " "}) rather than being left empty — so a directory's
     * name can never be an empty string.
     *
     * @param name   the directory's name
     * @param parent the directory's parent
     */
    public Directory(String name, FileSystemElement parent) {
        this.name = " ";
        this.contents = new ArrayList<>();
        this.setName(name);
        this.setParent(parent);
    }

    /**
     * Creates a new, empty directory at {@code path}. Fails and makes no
     * changes if {@code path} is invalid, the directory that would contain
     * it doesn't already exist, or something already exists at {@code path}.
     * Never overwrites an existing entry and never creates missing
     * intermediate directories.
     *
     * @param path the relative path at which to create the new directory
     * @return {@code true} on success, {@code false} on failure
     */
    public boolean mkdir(String path) {
        return this.insert(path, new Directory(" ", null));
    }

    /**
     * Creates a new, empty file at {@code path}. Same failure conditions as
     * {@link #mkdir(String)}: no intermediate-directory creation, and fails
     * on any name collision at {@code path} rather than overwriting or
     * truncating an existing file.
     *
     * @param path the relative path at which to create the new file
     * @return {@code true} on success, {@code false} on failure
     */
    public boolean touch(String path) {
        return this.insert(path, new File(" ", "", null));
    }

    /**
     * Inserts {@code newElement} at {@code path}, renaming it to match
     * {@code path}'s last segment and attaching it to the directory that
     * would contain it. Fails if {@code path} is invalid, the containing
     * directory doesn't exist, or a sibling with the same name already
     * exists there.
     *
     * @param path       the relative path at which to insert the element
     * @param newElement the element to insert
     * @return {@code true} on success, {@code false} on failure
     */
    private boolean insert(String path, FileSystemElement newElement) {
        if (!this.validPath(path)) {
            return false;
        }

        int idx = path.lastIndexOf("/");
        String newName = path.substring(idx + 1);

        Directory targetDir = this.getParentDirectory(path);
        if (targetDir == null) {
            return false;
        }

        for (FileSystemElement element : targetDir.contents) {
            if (element.name.equals(newName)) {
                return false;
            }
        }

        newElement.setName(newName);
        newElement.setParent(targetDir);
        targetDir.contents.add(newElement);
        return true;
    }

    /**
     * Removes and returns the element currently at {@code path}, or
     * {@code null} if {@code path} is invalid or nothing exists there. On
     * success, the returned element is fully detached from the tree: it is
     * no longer part of any directory's contents, and it has no parent.
     *
     * this implementation does not account for removal of the root directory
     *
     * @param path the relative path of the element to remove
     * @return the removed element, or {@code null} if it could not be removed
     */
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
        target.setParent(null);
        return target;
    }

    /**
     * Moves the element at {@code srcPath} to {@code destPath}, renaming it
     * to match {@code destPath}'s last segment. Both {@code srcPath} and
     * {@code destPath} must be valid paths, and {@code srcPath} must resolve
     * to an existing element. {@code destPath} does not need to already
     * exist — only the directory that would contain it does; if something
     * already exists at {@code destPath}, it is silently overwritten by the
     * moved element. Both preconditions are confirmed before any change is
     * made to the tree — if either fails, nothing is modified.
     *
     * @param srcPath  the relative path of the element to move
     * @param destPath the relative path to move the element to
     * @return {@code true} on success, {@code false} on failure
     */
    public boolean move(String srcPath, String destPath) {
        if (!this.validPath(srcPath) || !this.validPath(destPath)) {
            return false;
        }

        FileSystemElement src = this.getElementByPath(srcPath);
        if (src == null) {
            return false;
        }

        Directory destParent = this.getParentDirectory(destPath);
        if (destParent == null) {
            return false;
        }

        this.remove(srcPath);
        this.remove(destPath);
        return this.insert(destPath, src);
    }

    /**
     * returns true if the directory contains either a directory or file at the
     * specified relative path
     *
     * @param path the relative path to check
     * @return {@code true} if an element exists at {@code path}, {@code false}
     *         otherwise
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

    /**
     * Returns the total size of this directory: the sum of {@code size()}
     * over all of its contents, recursively (a subdirectory contributes the
     * sum of everything inside it).
     *
     * @return the total size of this directory
     */
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
    /**
     * Validates that {@code path} is non-null, non-empty, and does not start
     * or end with {@code /}.
     *
     * @param path the path to validate
     * @return {@code true} if {@code path} is valid, {@code false} otherwise
     */
    private boolean validPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        // paths may not have leading or trailing slashes
        if (path.charAt(0) == '/' || path.charAt(path.length() - 1) == '/') {
            return false;
        }
        return true;
    }

    /**
     * Resolves {@code path} relative to this directory and returns the
     * element found there, or {@code null} if no such element exists.
     *
     * @param path the relative path to resolve
     * @return the element at {@code path}, or {@code null} if none exists
     */
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

    /**
     * Resolves and returns the directory that would contain the element at
     * {@code path} (i.e. everything in {@code path} before the last
     * {@code /}), or {@code null} if that directory doesn't exist.
     *
     * @param path the relative path whose containing directory is resolved
     * @return the containing directory, or {@code null} if it doesn't exist
     */
    private Directory getParentDirectory(String path) {
        int idx = path.lastIndexOf("/");
        if (idx == -1) {
            return this;
        }
        FileSystemElement result = this.getElementByPath(path.substring(0, idx));
        if (!(result instanceof Directory)) {
            return null;
        }
        return (Directory) result;
    }

}