public class File extends FileSystemElement {

    public File(String name, FileSystemElement parent) {
        this.name = "";
        this.setName(name);
        this.setParent(parent);
    }
}
