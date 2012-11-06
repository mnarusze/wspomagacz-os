#!/bin/bash

SVN=
GIT=
NAME=

while getopts ":sgn:" optname ; do
    case "$optname" in
        "s")
            SVN=1;
            ;;
        "g")
            GIT=1;
            ;;
        "n")
            NAME="$OPTARG"
            ;;
        *)
            echo "Błąd: Nieznana opcja $OPTARG" > /dev/stderr
            exit 1
            ;;
    esac
done

if [[ -z "$NAME" ]] ; then
    echo "Błąd: pusta nazwa" > /dev/stderr > /dev/stderr
    exit 1
fi

if [[ -z "$GIT" && -z "$SVN" ]] ; then
    echo "Błąd: nie podano typu repozytorium" > /dev/stderr
    exit 1
fi

#########
#  GIT  #
#########
if [[ -n "$GIT" && "$GIT" -eq 1 ]] ; then

fi

#########
#  SVN  #
#########
if [[ -n "$SVN" && "$SVN" -eq 1 ]] ; then
    
fi
