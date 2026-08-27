public abstract class FileSystemElement {
    String name;
    FileSystemElement parent;

    /**
     * Returns this element's name.
     *
     * @return the current name of this element
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this element's parent, or {@code null} if it has none (either
     * because it's the root, or because it was removed from a directory and
     * not yet reinserted anywhere).
     *
     * @return the parent of this element, or {@code null} if it has none
     */
    public FileSystemElement getParent() {
        return this.parent;
    }

    /**
     * Sets this element's parent.
     *
     * @param newParent the new parent to assign to this element
     */
    public void setParent(FileSystemElement newParent) {
        this.parent = newParent;
    }

    /**
     * Sets this element's name if and only if {@code newName} is non-null,
     * non-empty, and does not contain the {@code /} character (since
     * {@code /} is reserved as the path separator and a name may not span
     * directories). If {@code newName} is null, empty, or contains
     * {@code /}, this call is a no-op — the current name is left unchanged.
     * This method does not check for uniqueness among siblings.
     *
     * @param newName the proposed new name for this element
     */
    public void setName(String newName) {
        if (newName == null) {
            return;
        }
        if (newName.length() == 0) {
            return;
        }
        if (newName.contains("/")) {
            return;
        }
        this.name = newName;
    }

    /**
     * Returns the full path from the root to this element, with segments
     * joined by {@code /}. For an element with no parent, this is just its
     * own name; otherwise it's {@code parent.getPath() + "/" + getName()}.
     *
     * @return the full path to this element
     */
    public String getPath() {

        if (this.getParent() == null) {
            return this.getName();
        }
        return this.getParent().getPath() + "/" + this.getName();
    }

    /**
     * Returns the total size of this element. Subclasses define what this
     * means.
     *
     * @return the size of this element
     */
    abstract public int size();

}