public class File extends FileSystemElement {
    String content;

    public File(String name, FileSystemElement parent) {
        this.name = "";
        this.setName(name);
        this.setParent(parent);
    }

    public void setContent(String newContent) {
        if (newContent == null) {
            return;
        }
        this.content = newContent;
    }

    public String getContent() {
        return content;
    }
}
