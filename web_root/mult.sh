#!/bin/bash
if (($# == 2)); then
  echo $(($1 * $2))
else
  echo "Error: must have two arguments"
fi