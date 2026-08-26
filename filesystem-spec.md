# In-Memory File System — Public API Specification

This document specifies the **public** interface only. Each class may implement whatever private
helper methods it needs internally — those are implementation details and are not part of this spec.

---

## `FileSystemElement` (abstract base class)

**`getName(): String`**
Returns this element's name.

**`getParent(): FileSystemElement`**
Returns this element's parent, or `null` if it has none (either because it's the root, or because it
was removed from a directory and not yet reinserted anywhere).

**`setParent(FileSystemElement newParent): void`**
Sets this element's parent.

**`setName(String newName): void`**
Sets this element's name if and only if `newName` is non-null and does not contain the `/` character
(since `/` is reserved as the path separator and a name may not span directories). If `newName` is
`null` or contains `/`, this call is a no-op — the current name is left unchanged. This method does not
check for uniqueness among siblings.

**`getPath(): String`**
Returns the full path from the root to this element, with segments joined by `/`. For an element with
no parent, this is just its own name; otherwise it's `parent.getPath() + "/" + getName()`.

**`size(): int`** *(abstract)*
Returns the total size of this element. Subclasses define what this means (see below).

---

## `File extends FileSystemElement`

**`File(String name, String content, FileSystemElement parent)`**
Constructs a file with the given name, content, and parent.

**`getContent(): String`**
Returns the file's content.

**`setContent(String content): void`**
Sets the file's content, provided `content` is non-null. A `null` argument is a no-op.

**`size(): int`**
Returns `getContent().length()` — the number of characters currently stored in the file.

---

## `Directory extends FileSystemElement`

**Path conventions**, applying to every method below that takes a `path` argument:
- A path is relative to the directory the method is called on.
- A valid path is never `null` or empty, and never starts or ends with `/`.
- Path segments are separated by `/`; each segment names a child at that level of the tree.
- An invalid path causes the method to fail gracefully (return `false`/`null`), never throw.

**`Directory(String name, FileSystemElement parent)`**
Constructs an empty directory with the given name and parent.

### `mkdir(String path): boolean`
Creates a new, empty directory at `path`. Returns `true` on success. Fails and makes no changes if:
- `path` is invalid,
- the directory that would contain it (i.e. everything in `path` before the last `/`) doesn't already
  exist, or
- something (a file or a directory) already exists at `path`.

`mkdir` never overwrites an existing entry and never creates missing intermediate directories.

### `touch(String path): boolean`
Creates a new, empty file at `path`. Same failure conditions as `mkdir`: no intermediate-directory
creation, and fails on any name collision at `path` rather than overwriting or truncating an existing
file.

### `remove(String path): FileSystemElement`
Removes and returns the element currently at `path`, or `null` if `path` is invalid or nothing exists
there. On success, the returned element is fully detached from the tree: it is no longer part of any
directory's contents, and it has no parent. (Not expected to support removing the root directory
itself.)

### `move(String srcPath, String destPath): boolean`
Moves the element at `srcPath` to `destPath`, renaming it to match `destPath`'s last segment. Returns
`true` on success.

- Both `srcPath` and `destPath` must be valid paths.
- `srcPath` must resolve to an existing element.
- `destPath` does **not** need to already exist — only the directory that would contain it does. Moving
  to a brand-new path is the normal case.
- If something already exists at `destPath`, it is silently overwritten (replaced) by the moved element.
- Both of the above conditions (source exists, destination's parent directory exists) are confirmed
  before any change is made to the tree — if either fails, `move` returns `false` and nothing is
  modified.
- Moving something to its own current path is not specially handled but behaves correctly.
- Not guarded against: moving a directory into one of its own descendants.

### `exists(String path): boolean`
Returns `true` if `path` resolves to an existing file or directory relative to this directory, `false`
otherwise (including for invalid paths).

### `size(): int`
Returns the total size of this directory: the sum of `size()` over all of its contents, recursively (a
subdirectory contributes the sum of everything inside it).
