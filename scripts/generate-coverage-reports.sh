#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

mvn clean test

mkdir -p docs/reports/unit-coverage/gateway-service docs/reports/unit-coverage/account-service

if [ -d gateway-service/target/site/jacoco ]; then
  cp -R gateway-service/target/site/jacoco/. docs/reports/unit-coverage/gateway-service/
fi

if [ -d account-service/target/site/jacoco ]; then
  cp -R account-service/target/site/jacoco/. docs/reports/unit-coverage/account-service/
fi

echo "Coverage reports copied to docs/reports/unit-coverage"
