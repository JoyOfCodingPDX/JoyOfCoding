# Publishing the Maven Site to GitHub Pages

## The Problems

### 1. Argument List Too Long Error

When trying to publish a large Maven site to GitHub Pages using `mvn scm-publish:publish-scm`, you may encounter the error:

```
error=7, Argument list too long
```

This occurs because the Maven SCM Publish plugin tries to run `git add` with hundreds or thousands of file paths as arguments, exceeding the system's `ARG_MAX` limit (typically ~262KB on macOS).

### 2. Child Module Overwrites Parent Index.html

When running `mvn site site:stage`, Maven generates sites for all modules and stages them. However, the last child module processed overwrites the parent project's `index.html` file at the root of the staging directory. This results in visitors seeing a child module's site (like kata-archetype) instead of the parent project's overview.

This is a known limitation of the Maven Site Plugin - child modules are staged to the root directory during the staging process.

## The Solution

Use the provided `publish-site.sh` script instead of the Maven plugin. This script:

1. Restores the parent project's `index.html` from `target/site/` (before it was overwritten)
2. Clones the `gh-pages` branch into a temporary directory
3. Copies the staged site content using `rsync`
4. Uses `git add -A` to stage all changes (Git handles this internally without argument length issues)
5. Commits and pushes the changes

## Usage

### Step 1: Build and Stage the Site

```bash
mvn clean
mvn site site:stage
```

This builds the site for all modules and stages it in `target/staging/scm:git:git@github.com:JoyOfCodingPDX/JoyOfCoding.git/`.

Note: Archetype projects (maven-archetype packaging) are excluded from site generation to prevent them from overwriting parent project pages.

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

