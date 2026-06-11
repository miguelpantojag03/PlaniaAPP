#!/bin/sh
set -e

if [ -z "$PLANIA_DB_URL" ] && [ -n "$PLANIA_DB_HOST" ] && [ -n "$PLANIA_DB_PORT" ] && [ -n "$PLANIA_DB_NAME" ]; then
  export PLANIA_DB_URL="jdbc:postgresql://${PLANIA_DB_HOST}:${PLANIA_DB_PORT}/${PLANIA_DB_NAME}"
fi

exec java -jar app.jar
