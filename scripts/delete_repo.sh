#!/bin/bash

SVN=
GIT=
NAME=
TRAC=

while getopts ":sgtn:" optname ; do
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

if [[ -z $GIT && -z $SVN ]] ; then
    echo "Błąd: nie podano typu repozytorium" > /dev/stderr
    exit 1
fi

#########
#  GIT  #
#########
if [[ -n $GIT && $GIT -eq 1 ]] ; then
    REPO_DIR="/var/www/git/$NAME"

    if [[ ! -d $REPO_DIR ]] ; then
        echo "Błąd: Repozytorium git $REPO_DIR nie istnieje!" > /dev/stderr
    fi
    
    rm -rf $REPO_DIR
fi

#########
#  SVN  #
#########
if [[ -n $SVN && $SVN -eq 1 ]] ; then
    REPO_DIR="/var/www/svn/$NAME"

    if [[ ! -d $REPO_DIR ]] ; then
        echo "Błąd: Repozytorium svn $REPO_DIR nie istnieje!" > /dev/stderr
    fi

    rm -rf $REPO_DIR
   
fi

##########
#  TRAC  #
##########
if [[ -n $TRAC && $TRAC -eq 1 ]] ; then
    TRAC_DIR=/var/www/trac/$NAME

    if [[ ! -d $TRAC_DIR ]] ; then
        echo "Błąd: Repozytorium trac $TRAC_DIR nie istnieje!" > /dev/stderr
    fi

    rm -rf $TRAC_DIR
   
fi
