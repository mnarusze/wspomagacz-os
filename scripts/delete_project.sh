#!/bin/bash

#SCRIPTS_MASTER_DIR should be exported!
if [[ -z $SCRIPTS_MASTER_DIR || ! -d $SCRIPTS_MASTER_DIR ]] ; then
    echo "Błąd: nie podano ścieżki do skryptów!" > /dev/stderr
    exit -1
fi

. $SCRIPTS_MASTER_DIR/common_functions.sh
get_input $*

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
