#!/usr/bin/env bash

VERBOSE=$([ "$1" == "-v" ])

rm base-etudiant.zip

if [ "${VERBOSE}" ]; then
  echo "copying examples to base-etudiant/"
fi
rm -r base-etudiant/examples/project*
for n in "00" "01" "02" "03" "04" "05"; do
  mkdir "base-etudiant/examples/project${n}/"
  cp -r "examples/project${n}/src" "base-etudiant/examples/project${n}/"
  sed "s/XX/${n}/g" base-etudiant/examples/build.gradle.kts.template > "base-etudiant/examples/project${n}/build.gradle.kts"
done

if [ "${VERBOSE}" ]; then
  echo "creating the zip"
fi

zip -r base-etudiant.zip base-etudiant
