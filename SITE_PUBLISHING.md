# Publishing the Maven Site to GitHub Pages

## The Problem

When trying to publish a large Maven site to GitHub Pages using `mvn scm-publish:publish-scm`, you may encounter the error:

```
error=7, Argument list too long
```

This occurs because the Maven SCM Publish plugin tries to run `git add` with hundreds or thousands of file paths as arguments, exceeding the system's `ARG_MAX` limit (typically ~262KB on macOS).

## The Solution

Use the provided `publish-site.sh` script instead of the Maven plugin. This script:

1. Clones the `gh-pages` branch into a temporary directory
2. Copies the staged site content using `rsync`
3. Uses `git add -A` to stage all changes (Git handles this internally without argument length issues)
4. Commits and pushes the changes

## Usage

### Step 1: Build and Stage the Site

```bash
mvn clean
mvn site site:stage
```

This builds the site for all modules and stages it in `target/staging/`.

### Step 2: Publish to GitHub Pages

```bash
./publish-site.sh
```

The script will:
- Clone the `gh-pages` branch
- Sync the staged content
- Commit and push the changes
- Clean up temporary files

### Step 3: View the Published Site

Visit: https://joyofcodingpdx.github.io/JoyOfCoding/

## Configuration

The POM is already configured with:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-scm-publish-plugin</artifactId>
  <version>3.3.0</version>
  <configuration>
    <scmBranch>gh-pages</scmBranch>
    <pubScmUrl>scm:git:https://github.com/JoyOfCodingPDX/JoyOfCoding.git</pubScmUrl>
    <tryUpdate>true</tryUpdate>
    <addUniqueDirectory>true</addUniqueDirectory>
    <skipDeletedFiles>true</skipDeletedFiles>
    <extraNormalizeExtensions>svg,rss</extraNormalizeExtensions>
  </configuration>
</plugin>
```

However, due to the large number of files, the Maven plugin approach doesn't work reliably on macOS. The shell script is the recommended approach.

## Troubleshooting

### "Staging directory not found"
Run `mvn site site:stage` first to build and stage the site.

### "Permission denied" when running the script
Make sure the script is executable:
```bash
chmod +x publish-site.sh
```

### SSH authentication issues
Ensure your SSH keys are set up correctly for GitHub:
```bash
ssh -T git@github.com
```

### The script hangs during rsync
The rsync operation can take several minutes for large sites. Be patient and watch for progress output.

