# ♲ Ssync ♲
## One Way Folder Synchronisation

### Description
Used to love [Synkron](https://synkron.sourceforge.net/) back in the day and thought I would replicate a small feature set of what I needed.
Ssync works by moving files from one directory to another dependent on location, file extensions, and excluded files.

## Usage
Either run from the sbt .jar or docker container

#### SBT 
``sbt run``

#### .jar ☕️
`sbt ssync.jar`

#### Docker 🐳
``Not quite figured out yet``

## Configuration

Example configuration
```
settings {
"Source": "/source",
"Archive": "/archive",
"Extensions": ["*"],
"IgnoredExtensions": ["ds_store"]
items = [
            {
                "Name": "Test 1",
                "Path": "Test1",
                "ProtectedDirectories": [
                    "Test1a", "Test1c", "Test1e", "Test1g", "Test1i"
                ]
            },
            {
                "Name": "Test 2",
                "Path": "Test2",
                "ProtectedDirectories": []
            },
            {
                "Name": "Test 3",
                "Path": "Test3",
                "ProtectedDirectories": []
            }
        ]
}
```

``"Extensions": ["*"]`` file extensions that will be moved in the sync.

``"IgnoredExtensions": ["ds_store"]`` file extension that will be ignored and left at the source.

``"Path": "Test1"`` sub directory of source where you would like to preserve the source folder structure.

``""ProtectedDirectories": [
"Test1a", "Test1c", "Test1e", "Test1g", "Test1i"
]"`` sub directories that will synced but the source subdirectory will be kept in place instead of deleted.
