# plusminus-replacer
Search and replace filenames, folder names and text file's content at once

## How to run
1. Download jar from [Releases](https://github.com/plusminus-software/plusminus-replacer/releases)
and put in the working folder
2. Add replacer.yml file among plusminus-replacer.jar
3. Run ```java -jar plusminus-replacer.jar```

## Example of replacer.yml file
```
- from: foo1
  to: |-
    foo1
    bar1
  replaceFileContent: true #replaces content of all textual files inside working folder, true is the default value
  replaceFileName: true #replaces file names of all files inside working folder, true is the default value
  replaceFolderName: false #replaces folder names of all folders inside working folder, false is the default value
  useEnvVariables: true #replaces text inside 'from' and 'to' with environment variables with format ${MY_ENV_VARIABLE:defaultValue}, false is the default value
- from: foo2
  to: foo2bar2
```