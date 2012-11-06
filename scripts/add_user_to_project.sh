#!/bin/bash

# Project-specific params
SVN= #s
GIT= #g
TRAC= #t
NAME= #n
PROJECT_OWNER= #o
DESCRIPTION= #d

# Global params
TEMPLATES_DIR= #T
TRAC_DIR= #R
GIT_REPO_DIR= #G
SVN_REPO_DIR= #S
SVN_ACCESS_CONTROL_FILE= #C
PROJECTS_ARCHIVE_DIR= #A

while getopts ":sgtn:d:o:T:R:G:S:C:A:" optname ; do
    case "$optname" in
        "s")
            SVN=1;
            ;;
        "g")
            GIT=1;
            ;;
        "t")
            TRAC=1
            ;;
        "n")
            NAME="$OPTARG"
            ;;
        "d")
            DESCRIPTION="$OPTARG"
            ;;
        "o")
            PROJECT_OWNER="$OPTARG"
            ;;
        "T")
            TEMPLATES_DIR="$OPTARG"
            ;;
        "R")
            TRAC_DIR="$OPTARG"
            ;;
        "G")
            GIT_REPO_DIR="$OPTARG"
            ;;
        "S")
            SVN_REPO_DIR="$OPTARG"
            ;;
        "C")
            SVN_ACCESS_CONTROL_FILE="$OPTARG"
            ;;
        "A")
            PROJECTS_ARCHIVE_DIR="$OPTARG"
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
