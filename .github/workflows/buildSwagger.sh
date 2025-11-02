#!/bin/bash

echo "Building redocCLI"

mkdir -p docs

npx @redocly/cli build-docs src/main/resources/swagger.yml -o docs/index.html
