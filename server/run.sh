#!/usr/bin/env bash
set -euo pipefail

# =========================
# Bistro Server runner
# =========================

# ---- Defaults (change if you want) ----
DEFAULT_PORT="8080"
DEFAULT_DB_URL="jdbc:mysql://db-bistro-g16.cbe862egq27l.eu-north-1.rds.amazonaws.com:3306"
DEFAULT_DB_USER="admin"
DEFAULT_DB_PASSWORD="quvxid-zupHas-7hobqi"

# ---- Export env vars (use existing values if already set) ----
export PORT="${PORT:-$DEFAULT_PORT}"
export DB_URL="${DB_URL:-$DEFAULT_DB_URL}"
export DB_USER="${DB_USER:-$DEFAULT_DB_USER}"
export DB_PASSWORD="${DB_PASSWORD:-$DEFAULT_DB_PASSWORD}"

# ---- Jar path (allow override) ----
JAR_PATH="${1:-./target/server-1.0-SNAPSHOT.jar}"

if [[ ! -f "$JAR_PATH" ]]; then
  echo "ERROR: JAR not found: $JAR_PATH"
  echo "Usage: $0 /path/to/server.jar"
  exit 1
fi

echo "Starting server..."
echo "PORT=$PORT"
echo "DB_URL=$DB_URL"
echo "DB_USER=$DB_USER"
echo "DB_PASSWORD=(hidden)"
echo "JAR=$JAR_PATH"

# Optional JVM opts via JAVA_OPTS env var (e.g. export JAVA_OPTS="-Xms256m -Xmx512m")
exec java ${JAVA_OPTS:-} -jar "$JAR_PATH"