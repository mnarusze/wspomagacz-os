#!/bin/bash

# Project-specific params
SVN= #s
GIT= #g
TRAC= #t
PUBLIC= #p
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

while getopts ":sgtpn:d:o:T:R:G:S:C:A:" optname ; do
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
        "p")
            PUBLIC=1
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

#################
# Global checks #
#################

if [[ -z "$NAME" ]] ; then
    echo "Błąd: pusta nazwa" > /dev/stderr
    exit 1
fi

if [[ -z "$GIT" && -z "$SVN" ]] ; then
    echo "Błąd: nie podano typu repozytorium!" > /dev/stderr
    exit 1
fi

if [[ -z "$PROJECTS_ARCHIVE_DIR" ]] ; then
    echo "Błąd: nie podano sciezki do archiwum!" > /dev/stderr
    exit 1    
fi

#########
#  GIT  #
#########

if [[ -n "$GIT" && "$GIT" -eq 1 ]] ; then
    if [[ -z "$GIT_REPO_DIR" ]] ; then
        echo "Błąd: nie podano sciezki do repozytorium GIT" > /dev/stderr
        exit 1
    else
        GIT_REPO_DIR=$GIT_REPO_DIR/$NAME
    fi

    if [[ ! -d $GIT_REPO_DIR ]] ; then
        echo "Błąd: Repozytorium git $GIT_REPO_DIR nie istnieje!" > /dev/stderr
        exit 1
    fi

    if [[ -d $PROJECTS_ARCHIVE_DIR/git/$NAME ]] ; then
        echo "Błąd: Repozytorium $NAME znajduje się już w archiwum!" > /dev/stderr
        exit 1
    fi

    mv $GIT_REPO_DIR $PROJECTS_ARCHIVE_DIR/git
fi

#########
#  SVN  #
#########

if [[ -n "$SVN" && "$SVN" -eq 1 ]] ; then
    if [[ -z "$SVN_REPO_DIR" ]] ; then
        echo "Błąd: nie podano sciezki do repozytorium SVN" > /dev/stderr
        exit 1
    else
        SVN_REPO_DIR=$SVN_REPO_DIR/$NAME
    fi

    if [[ ! -d $SVN_REPO_DIR ]] ; then
        echo "Błąd: Katalog $SVN_REPO_DIR nie istnieje!" > /dev/stderr
        exit 1
    fi

    if [[ -d $PROJECTS_ARCHIVE_DIR/svn/$NAME ]] ; then
        echo "Błąd: Repozytorium $NAME znajduje się już w archiwum!" > /dev/stderr
        exit 1
    fi

    if [[ -z "$SVN_ACCESS_CONTROL_FILE" ]] ; then
        echo "Błąd: nie podano sciezki do repozytorium SVN" > /dev/stderr
        exit 1
    fi
    
    sed "/#${NAME}_SECTION_BEGIN/,/#${NAME}_SECTION_END/d" ${SVN_ACCESS_CONTROL_FILE} | grep -Ev "$NAME" | cat -s > ${SVN_ACCESS_CONTROL_FILE}.tmp
    if [[ -n "$(cat ${SVN_ACCESS_CONTROL_FILE}.tmp)" || -z "$(cat ${SVN_ACCESS_CONTROL_FILE}.tmp | grep $NAME)" ]] ; then
        mv ${SVN_ACCESS_CONTROL_FILE}.tmp $SVN_ACCESS_CONTROL_FILE
    else
        echo "Błąd: nie udało się usunąć praw dostępu. Sprawdź plik $(cat ${SVN_ACCESS_CONTROL_FILE}.tmp | grep $NAME)" > /dev/stderr
        exit 1
    fi

    mv $SVN_REPO_DIR $PROJECTS_ARCHIVE_DIR/svn
fi

##########
#  TRAC  #
##########
if [[ -n $TRAC && $TRAC -eq 1 ]] ; then
    if [[ -z "$TRAC_DIR" ]] ; then
        echo "Błąd: nie podano sciezki do TRAC" > /dev/stderr
        exit 1
    else
        TRAC_DIR=$TRAC_DIR/$NAME
    fi

    if [[ ! -d $TRAC_DIR ]] ; then
        echo "Błąd: Katalog $TRAC_DIR nie istnieje!" > /dev/stderr
        exit 1
    fi

    if [[ -d $PROJECTS_ARCHIVE_DIR/trac/$NAME ]] ; then
        echo "Błąd: TRAC $NAME znajduje się już w archiwum!" > /dev/stderr
        exit 1
    fi
    mv $SVN_REPO_DIR $PROJECTS_ARCHIVE_DIR/trac
fi
