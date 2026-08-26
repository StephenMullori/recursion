
public abstract class FileSystemElement {
    String name;
    FileSystemElement parent;

    public String getName() {
        return this.name;
    }

    public FileSystemElement getParent() {
        return this.parent;
    }

    public void setParent(FileSystemElement newParent) {
        this.parent = newParent;
    }

    public void setName(String newName) {
        if (newName == null) {
            return;
        }
        if (newName.contains("/")) {
            return;
        }
        this.name = newName;
    }

    public String getPath() {

        if (this.getParent() == null) {
            return this.getName();
        }
        return this.getParent().getPath() + "/" + this.getName();
    }

    abstract public int size();

}