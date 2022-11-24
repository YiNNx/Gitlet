# Gitlet

## Internal Structures

- **blobs**: The saved contents of files. Since Gitlet saves many versions of files, a single file might correspond to multiple blobs: each being tracked in a different commit.
- **trees**: Directory structures mapping names to references to blobs and other trees (subdirectories).
- **commits**: Combinations of log messages, other metadata (commit date, author, etc.), a reference to a tree, and references to parent commits. The repository also maintains a mapping from *branch heads* to references to commits, so that certain important commits have symbolic names.
  - **metadata** - log message & timestamp
  - **a mapping of file names to blob references**
  - **a parent reference** ( and a second parent reference


![1](https://cdn.just-plain.fun/img/1.png)



![img](https://sp21.datastructur.es/materials/proj/proj2/image/commits-and-blobs.png)



```
.gitlet
├── HEAD						// points at HEAD e.g."ref: refs/heads/master"
├── logs
│   ├── HEAD
│   └── refs
├── index						// staging area
│  	├── addition				// for addition
│   └── removal					// for removal
├── objects						// stores blobs & commits
└── refs
    └── heads					// branches' headers
```

## Command

- [init](https://sp21.datastructur.es/materials/proj/proj2/proj2#init)
- [add](https://sp21.datastructur.es/materials/proj/proj2/proj2#add)
- [commit](https://sp21.datastructur.es/materials/proj/proj2/proj2#commit)
- [rm](https://sp21.datastructur.es/materials/proj/proj2/proj2#rm)
- [log](https://sp21.datastructur.es/materials/proj/proj2/proj2#log)
- [global-log](https://sp21.datastructur.es/materials/proj/proj2/proj2#global-log)
- [find](https://sp21.datastructur.es/materials/proj/proj2/proj2#find)
- [status](https://sp21.datastructur.es/materials/proj/proj2/proj2#status)
- [checkout](https://sp21.datastructur.es/materials/proj/proj2/proj2#checkout)
- [branch](https://sp21.datastructur.es/materials/proj/proj2/proj2#branch)
- [rm-branch](https://sp21.datastructur.es/materials/proj/proj2/proj2#rm-branch)
- [reset](https://sp21.datastructur.es/materials/proj/proj2/proj2#reset)
- [merge](https://sp21.datastructur.es/materials/proj/proj2/proj2#merge)