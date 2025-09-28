#!/bin/bash

if [ ! -d "venv" ]; then
    python3 -m venv venv
fi
venv/bin/pip3 install texttest
venv/bin/texttest -d . -con "$@"
