public class File extends FileSystemElement {
    String content;

    public File(String name, String content, FileSystemElement parent) {
        this.name = "";
        this.content = "";
        this.setName(name);
        this.setParent(parent);

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            return;
        }
        this.content = content;
    }

    public int size() {
        return this.getContent().length();
    }

}
