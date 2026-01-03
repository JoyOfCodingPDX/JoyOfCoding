#!/bin/bash

# Script to publish Maven site to GitHub Pages
# This script works around the "Argument list too long" error by batching git operations

set -e  # Exit on error

REPO_URL="git@github.com:JoyOfCodingPDX/JoyOfCoding.git"
STAGING_DIR="target/staging/scm:git:git@github.com:JoyOfCodingPDX/JoyOfCoding.git"
WORK_DIR="target/gh-pages-publish"
BRANCH="gh-pages"
PARENT_SITE_DIR="target/site"

echo "Publishing Maven site to GitHub Pages..."

# Check if staging directory exists
if [ ! -d "$STAGING_DIR" ]; then
    echo "Error: Staging directory not found at $STAGING_DIR"
    echo "Please run 'mvn site site:stage' first"
    exit 1
fi

# Fix the index.html overwrite issue: child modules overwrite parent's index.html during staging
# We need to restore the parent's index.html from target/site
if [ -f "$PARENT_SITE_DIR/index.html" ]; then
    echo "Restoring parent project's index.html (was overwritten by child modules)..."
    cp "$PARENT_SITE_DIR/index.html" "$STAGING_DIR/index.html"
else
    echo "Warning: Parent site's index.html not found at $PARENT_SITE_DIR/index.html"
    echo "Continuing anyway, but the top-level site may show the wrong project..."
fi

# Clean up any existing work directory
if [ -d "$WORK_DIR" ]; then
    echo "Cleaning up existing work directory..."
    rm -rf "$WORK_DIR"
fi

# Clone the gh-pages branch
echo "Cloning gh-pages branch..."
git clone --branch "$BRANCH" --single-branch --depth 1 "$REPO_URL" "$WORK_DIR"

# Copy staged site to work directory
echo "Copying staged site (this may take a while)..."
rsync -a --delete --exclude='.git' --info=progress2 "$STAGING_DIR/" "$WORK_DIR/"

# Change to work directory
cd "$WORK_DIR"

# Add files in batches to avoid "Argument list too long" error
echo "Staging changes to git..."

# Add all changes using git add with pathspec
# This handles new, modified files
echo "Adding new and modified files..."
git add -A

# Check if there are changes to commit
if git diff --cached --quiet; then
    echo "No changes to commit"
    cd - > /dev/null
else
    # Commit and push
    echo "Committing changes..."
    git commit -m "Update site: $(date '+%Y-%m-%d %H:%M:%S')"

    echo "Pushing to GitHub (this may take a while)..."
    git push origin "$BRANCH"

    cd - > /dev/null
    echo "Site published successfully!"
    echo "View at: https://joyofcodingpdx.github.io/JoyOfCoding/"
fi

echo "Done!"

