public class File extends FileSystemElement {
    String content;

    public File(String contents, p) {
        this.content = "";
        
        this.setContent(contents);
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
