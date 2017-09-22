#!/bin/bash
#set -ex

SCRIPTS=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
ROOT=$(dirname "$SCRIPTS")

cd "$ROOT/ios"
SCHEME="ElectrodeReactNativeBridgeTests"
SDK="iphonesimulator"
DESTINATION="platform=iOS Simulator,name=iPhone 6,OS=10.3.1"
PROJECT="ElectrodeReactNativeBridge.xcodeproj"
xcodebuild \
  -project "$PROJECT" \
  -scheme "$SCHEME" \
  -destination "$DESTINATION" \
  test
