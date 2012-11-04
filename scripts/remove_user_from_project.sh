#!/bin/bash

SVN=
GIT=
NAME=
SVN_ACCESS_FILE=
GIT_ACCESS_FILE=

while getopts ":sgn:f:p:" optname ; do
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
	"f")
	    SVN_ACCESS_FILE="$OPTARG"
	    ;;
	"p")
	    GIT_ACCESS_FILE="$OPTARG"
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

if [[ -n "$SVN" && -z "$SVN_ACCESS_FILE" ]] ; then
    echo "Błąd: nie podano ścieżki do svn.access!" > /dev/stderr
    exit 1
fi

if [[ -n "$GIT" && -z "$GIT_ACCESS_FILE" ]] ; then
    echo "Błąd: nie podano ścieżki do git.access!" > /dev/stderr
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
