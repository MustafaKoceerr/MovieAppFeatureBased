#!/usr/bin/env bash
# Sanity check of the R8 output of the release build.
#
# Usage:  ./gradlew :app:assembleRelease && scripts/check-r8-release.sh [path/to/usage.txt]
#
# R8 full mode removes classes that no code references directly. For Retrofit DTOs that is a bug:
# Retrofit reads the response type from the generic signature (Response<Dto>), and when R8 removes
# the DTO it rewrites the type argument to Object, which fails at runtime (see app/proguard-rules.pro).
# usage.txt lists fully removed classes as lines without a trailing colon.
set -euo pipefail

USAGE_FILE="${1:-app/build/outputs/mapping/release/usage.txt}"
if [[ ! -f "$USAGE_FILE" ]]; then
  echo "usage.txt not found: $USAGE_FILE (run ./gradlew :app:assembleRelease first)" >&2
  exit 2
fi

BASE='com\.mustafakocer\.movieappfeaturebasedclean'
# DTOs, Room entities and navigation routes must survive shrinking.
PATTERN="^${BASE}\.(feature\..*\.data\.model\.|feature\..*\.data\.local\.entity\.|navigation\.)[A-Za-z0-9_.\$]*$"

removed="$(tr -d '\r' < "$USAGE_FILE" | grep -E "$PATTERN" || true)"
if [[ -n "$removed" ]]; then
  echo "FAIL: R8 removed classes that must be kept:" >&2
  echo "$removed" | sed 's/^/  /' >&2
  exit 1
fi
echo "OK: no DTO, entity or route class was removed by R8 ($USAGE_FILE)"
