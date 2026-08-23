import java.util.ArrayList;

public class directory extends FileSystemElement {
    ArrayList<FileSystemElement> contents;

    public void add(String relativePath) {
        if (relativePath == null) {
            return;
        }

        if (relativePath.strip() == "") {
            return;
        }

        int idx = relativePath.indexOf("/");

        if(idx == -1) {
            this.add(new File)
        }

    }

    public void add(FileSystemElement element) {
        this.contents.add(element);
    }

    public void removeElement(File)
}
