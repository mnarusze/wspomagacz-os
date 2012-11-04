#!/bin/bash

SVN=
GIT=
NAME=
TRAC=
TRAC_DIR=
REPOSITORIES_DIR=

while getopts ":sgta:d:r:n:w:" optname ; do
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
        "r")
            REPOSITORIES_DIR="$OPTARG"
            ;;
        "n")
            NAME="$OPTARG"
            ;;
        "a")
            TRAC_DIR="$OPTARG"
            ;;
        *)
            echo "Błąd: Nieznana opcja $OPTARG" > /dev/stderr
            exit 1
            ;;
    esac
done

if [[ -z $NAME ]] ; then
    echo "Błąd: pusta nazwa" > /dev/stderr
    exit 1
fi

if [[ -z $REPOSITORIES_DIR ]] ; then
    echo "Błąd: brak ścieżki do repozytoriów" > /dev/stderr
    exit 1
fi

if [[ -z $GIT && -z $SVN ]] ; then
    echo "Błąd: nie podano typu repozytorium" > /dev/stderr
    exit 1
fi

if [[ -n $TRAC && $TRAC -eq 1 && -z $TRAC_DIR ]] ; then
    echo "Błąd: brak ścieżki do trac" > /dev/stderr
    exit 1
fi

#########
#  GIT  #
#########
if [[ -n $GIT && $GIT -eq 1 ]] ; then
    REPO_DIR=$REPOSITORIES_DIR/git/$NAME

    if [[ ! -d $REPO_DIR ]] ; then
        echo "Błąd: Repozytorium git $REPO_DIR nie istnieje!" > /dev/stderr
    fi
    
    rm -rf $REPO_DIR
fi

#########
#  SVN  #
#########
if [[ -n $SVN && $SVN -eq 1 ]] ; then
    REPO_DIR=$REPOSITORIES_DIR/svn/$NAME

    if [[ ! -d $REPO_DIR ]] ; then
        echo "Błąd: Repozytorium svn $REPO_DIR nie istnieje!" > /dev/stderr
    fi

    rm -rf $REPO_DIR
   
fi

##########
#  TRAC  #
##########
if [[ -n $TRAC && $TRAC -eq 1 ]] ; then
    TRAC_DIR=$TRAC_DIR/$NAME

    if [[ ! -d $TRAC_DIR ]] ; then
        echo "Błąd: Repozytorium trac $TRAC_DIR nie istnieje!" > /dev/stderr
    fi

    rm -rf $TRAC_DIR
   
fi
