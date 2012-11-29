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
    if [[ -z "$GIT_REPOS_DIR" ]] ; then
        echo "Błąd: nie podano sciezki do repozytoriów GIT" > /dev/stderr
        exit 1
    else
        GIT_REPO_DIR=$GIT_REPOS_DIR/${NAME}.git
    fi

    if [[ ! -d $GIT_REPO_DIR ]] ; then
        echo "Błąd: Repozytorium git $GIT_REPO_DIR nie istnieje!" > /dev/stderr
        exit 2
    fi

    if [[ -d $PROJECTS_ARCHIVE_DIR/git/$NAME ]] ; then
        echo "Błąd: Repozytorium $NAME znajduje się już w archiwum!" > /dev/stderr
        exit 3
    fi

    if [[ -z "$GITOLITE_ADMIN_DIR" || ! -d "$GITOLITE_ADMIN_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do repozytorium Gitolite: $GITOLITE_ADMIN_DIR" > /dev/stderr
        exit 4
    fi

    if [[ ! -f "$GITOLITE_CONFIG_FILE" ]] ; then
        echo "Błąd: brak pliku konfiguracyjnego Gitolite: $GITOLITE_CONFIG_FILE" > /dev/stderr
        exit 5
    fi

    if [[ -z $(cat $GITOLITE_CONFIG_FILE | grep "repo ${NAME}$") ]] ; then
        echo "Błąd: repozytorium o podanej nazwie $NAME nie istnieje!" > /dev/stderr
        exit 6
    fi

    # Przenosimy do archiwum
    mv $GIT_REPO_DIR $PROJECTS_ARCHIVE_DIR/git
    # Kopiujemy config - na wszelki wypadek
    cp $GITOLITE_CONFIG_FILE ${GITOLITE_CONFIG_FILE}.swp
    # Kasujemy grupę
    LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep -P "@$NAME\t=\t" -m 1 -n | sed 's/[^0-9].*//g')
    sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
    # Kasujemy uprawnienia do repo
    LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep "repo $NAME$" -m 1 -n | sed 's/[^0-9].*//g')
    sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
    # Sprawdzamy, czy linia zawiera "repo [...]" lub czy jest pusta - jeśli tak, 
    # to przestajemy kasować
    while [[ -z $(sed -n "${LINE_NUMBER_TO_DELETE}p" $GITOLITE_CONFIG_FILE | grep "^repo ") \
        && -n $(sed -n "${LINE_NUMBER_TO_DELETE}p" $GITOLITE_CONFIG_FILE | grep ".*") ]] ; do
        sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
    done
    
    # Sprawdźmy, czy w configu nie ma żadnej linii z naszym repo  czy zawiera
    # standardowe info o wszystkich repozytoriach (repo @all)
    if [[ -n $(cat $GITOLITE_CONFIG_FILE | grep "repo ${NAME}$") \
        || -n $(cat $GITOLITE_CONFIG_FILE | grep "@${NAME}\t=\t") \
        || -z $(cat $GITOLITE_CONFIG_FILE | grep "repo @all$") ]] ; then
        echo "Błąd: nie udało się usunąć informacji o repo lub usunięto zbyt dużo." > /dev/stderr
        # No to przywracamy...
        mv $PROJECTS_ARCHIVE_DIR/git/${NAME}.git $GIT_REPO_DIR
        mv ${GITOLITE_CONFIG_FILE}.swp ${GITOLITE_CONFIG_FILE}
        exit 7
    fi

    # OK - pushujemy zmiany
    cd $GITOLITE_ADMIN_DIR
    git add $GITOLITE_CONFIG_FILE
    git commit $GITOLITE_CONFIG_FILE -m "Usuwam repozytorium $NAME"
    git push origin master
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
        echo "Błąd: nie udało się usunąć praw dostępu. Sprawdź plik ${SVN_ACCESS_CONTROL_FILE}.tmp" > /dev/stderr
        exit 1
    fi

    sudo mv $SVN_REPO_DIR $PROJECTS_ARCHIVE_DIR/svn
    if [[ -d $SVN_REPO_DIR ]] ; then
        echo "Błąd: nie udało się usunąć repozytorium" > /dev/stderr
        exit 2
    fi
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
