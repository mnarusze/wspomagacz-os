#!/bin/bash

#SCRIPTS_MASTER_DIR should be exported!
if [[ -z $SCRIPTS_MASTER_DIR || ! -d $SCRIPTS_MASTER_DIR ]] ; then
    echo "Błąd: nie podano ścieżki do skryptów!" > /dev/stderr
    exit -1
fi

. $SCRIPTS_MASTER_DIR/common_functions.sh
get_input $*

if [[ -z "$NAME" ]] ; then
    echo "Błąd: pusta nazwa" > /dev/stderr > /dev/stderr
    exit 1
fi

if [[ -z "$GIT" && -z "$SVN" ]] ; then
    echo "Błąd: nie podano typu repozytorium" > /dev/stderr
    exit 1
fi

if [[ -z "$USER" ]] ; then
    echo "Błąd: nie podano nazwy użytkownika" > /dev/stderr
    exit 1    
fi

#########
#  GIT  #
#########
if [[ -n "$GIT" && "$GIT" -eq 1 ]] ; then
    if [[ -z "$GITOLITE_ADMIN_DIR" ]] ; then
        echo "Błąd: nie podano sciezki do katalogu administatora Gitolite" > /dev/stderr
        exit 2
    fi
    if [[ ! -d "$GITOLITE_ADMIN_DIR" ]] ; then
        echo "Błąd: nie istnieje katalog administatora Gitolite $GITOLITE_ADMIN_DIR" > /dev/stderr
        exit 3
    fi
    
fi

#########
#  SVN  #
#########
if [[ -n "$SVN" && "$SVN" -eq 1 ]] ; then
    if [[ -z "$SVN_ACCESS_CONTROL_FILE" ]] ; then
        echo "Błąd: nie podano sciezki do repozytorium SVN" > /dev/stderr
        exit 1
    fi
    CURRENT_LIST=$(cat $SVN_ACCESS_CONTROL_FILE | grep -m 1 "$NAME = ")
    sed -i "s/$CURRENT_LIST/$CURRENT_LIST $USER,/" "$SVN_ACCESS_CONTROL_FILE"
fi
