#!/usr/bin/env bash
openssl aes-256-cbc -K $encrypted_7033b92483e5_key -iv $encrypted_7033b92483e5_iv -in releng/codesigning.asc.enc -out releng/codesigning.asc -d
gpg --fast-import releng/codesigningkey.asc
