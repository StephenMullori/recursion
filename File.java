public class File extends FileSystemElement {
    String content;

    /**
     * Constructs a file with the given name, content, and parent.
     * <p>
     * If {@code name} is invalid (null, empty, or contains {@code /}),
     * {@link #setName(String)} no-ops and the name falls back to a single
     * space ({@code " "}) rather than being left empty — so a file's name
     * can never be an empty string.
     *
     * @param name    the file's name
     * @param content the file's initial content
     * @param parent  the file's parent
     */
    public File(String name, String content, FileSystemElement parent) {
        this.name = " ";
        this.content = "";
        this.setName(name);
        this.setParent(parent);

    }

    /**
     * Returns the file's content.
     *
     * @return the current content of this file
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the file's content, provided {@code content} is non-null. A
     * {@code null} argument is a no-op.
     *
     * @param content the new content for this file
     */
    public void setContent(String content) {
        if (content == null) {
            return;
        }
        this.content = content;
    }

    /**
     * Returns {@code getContent().length()} — the number of characters
     * currently stored in the file.
     *
     * @return the size of this file, in characters
     */
    public int size() {
        return this.getContent().length();
    }

}