# plusminus-replacer
Search and replace filenames, folder names and text file's content at once

## How to run
1. Download jar from [Releases](https://github.com/plusminus-software/plusminus-replacer/releases)
and put in the working folder
2. Add replacer.yml file among plusminus-replacer.jar
3. Run ```java -jar plusminus-replacer.jar```

## Example of replacer.yml file
```
- from: foo
  to: bar
  if: "${USER} == 'Taras'" #optional
  scopes: [CONTENT, FILE_NAME, FOLDER_NAME] #optional, [CONTENT] is default
- from: foo2
  to: |-
    multi
    line
    string
...
```