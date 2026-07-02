#!/bin/bash

echo "====================================="
echo "SportClub AI Production Validator"
echo "====================================="

ERRORS=0

echo "1. Checking for debug code..."
if grep -rn "Log.d" app/src/main/java; then
    echo "WARNING: Debug logs found in source code."
    ERRORS=$((ERRORS + 1))
fi

echo "2. Checking for TODOs..."
if grep -rn "TODO" app/src/main/java; then
    echo "WARNING: TODOs found in source code."
    ERRORS=$((ERRORS + 1))
fi

echo "3. Checking Release Build Configuration..."
if grep -q "isMinifyEnabled = true" app/build.gradle.kts; then
    echo "SUCCESS: Minify is enabled."
else
    echo "ERROR: Minify is NOT enabled."
    ERRORS=$((ERRORS + 1))
fi

echo "4. Checking Metadata..."
if [ -d "app/play_store_metadata" ]; then
    echo "SUCCESS: Play Store metadata found."
else
    echo "ERROR: Play Store metadata missing."
    ERRORS=$((ERRORS + 1))
fi

echo "====================================="
if [ $ERRORS -eq 0 ]; then
    echo "VALIDATION PASSED. Ready for release."
    exit 0
else
    echo "VALIDATION FAILED with $ERRORS warnings/errors."
    exit 1
fi
