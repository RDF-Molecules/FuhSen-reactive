#!/bin/bash
# Remove RUNNING_PID
find . -type f -name RUNNING_PID -exec rm -f {} \;

# Start Minte
./minte-1.1.0/bin/minte

