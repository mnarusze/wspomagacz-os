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
    echo "Błąd: nie podano typu repozytorium" > /dev/stderr
    exit 1
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

    if [[ -z "$SVN_ACCESS_CONTROL_FILE" ]] ; then
        echo "Błąd: nie podano sciezki do repozytorium SVN" > /dev/stderr
        exit 1
    fi

    if [[ -d "$SVN_REPO_DIR" ]] ; then
        echo "Błąd: Repozytorium $SVN_REPO_DIR już istnieje!" > /dev/stderr
        exit 2
    fi

    LINE_NUMBER_FOR_USERS=$(cat $SVN_ACCESS_CONTROL_FILE | grep  ^$ -m 1 -n | sed 's/[^0-9].*//g')
    sed -i "$LINE_NUMBER_FOR_USERS i $NAME = $PROJECT_OWNER," $SVN_ACCESS_CONTROL_FILE
    echo "#${NAME}_SECTION_BEGIN" >> $SVN_ACCESS_CONTROL_FILE
    echo "[$NAME:/]" >> $SVN_ACCESS_CONTROL_FILE
    echo "@$NAME = rw" >> $SVN_ACCESS_CONTROL_FILE
    if [[ -n $PUBLIC && $PUBLIC -eq 1 ]] ; then
        echo "* = r" >> $SVN_ACCESS_CONTROL_FILE
    fi
    echo "#${NAME}_SECTION_END" >> $SVN_ACCESS_CONTROL_FILE
    echo "" >> $SVN_ACCESS_CONTROL_FILE

    svnadmin create "$SVN_REPO_DIR"
    
    #chown apache:apache "$SVN_REPO_DIR" -R
    chmod o-rwx "$SVN_REPO_DIR" -R
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

    if [[ -d "$GIT_REPO_DIR" ]] ; then
        echo "Błąd: Repozytorium $GIT_REPO_DIR już istnieje!" > /dev/stderr
        exit 2
    fi
    git --git-dir="$GIT_REPO_DIR" init --bare

    if [[ ! -d "$GIT_REPO_DIR" ]] ; then
        echo "Błąd: Nie udało się utworzyć repozytorium $GIT_REPO_DIR!" > /dev/stderr
        exit 3
    fi

    #chown apache:apache "$GIT_REPO_DIR" -R
    chmod o-rwx "$GIT_REPO_DIR" -R
fi

##########
#  TRAC  #
##########

if [[ -n "$TRAC" && "$TRAC" -eq 1 ]] ; then
    if [[ -z "$SVN" || "$SVN" != 1 ]] ; then
        echo "Błąd: aby korzystać z TRAC'a, trzeba używać SVN!" > /dev/stderr
        exit 1
    else
        TRAC_DIR=$TRAC_DIR/$NAME
    fi
    #
    # Give up on description for now, it messes the arguments
    # Use a constant 
    #if [[ -z "$DESCRIPTION" ]] ; then
    #    echo "Błąd: dla TRAC trzeba podać opis!" > /dev/stderr
    #    exit 1
    #fi
    DESCRIPTION="Tymczasowe description dla $NAME"
    if [[ -z "$TEMPLATES_DIR" ]] ; then
        echo "Błąd: dla TRAC trzeba podać ścieżkę do katalogu z szablonami!" > /dev/stderr
        exit 1
    fi

    SVN_REPO_DIR_FOR_INI=$(echo $SVN_REPO_DIR | sed 's/\//\\\//g')
    TRAC_INI="$TRAC_DIR"/conf/trac.ini

    # Copy and prepare HOOKS
    cp "$TEMPLATES_DIR"/trac-post-commit-hook.py "$SVN_REPO_DIR"/hooks
    echo "#!/bin/sh" > "$SVN_REPO_DIR"/hooks/post-commit
    echo "REPOS=\$1" >> "$SVN_REPO_DIR"/hooks/post-commit
    echo "REV=\$2" >> "$SVN_REPO_DIR"/hooks/post-commit
    echo "TRAC_ENV=$SVN_REPO_DIR" >> "$SVN_REPO_DIR"/hooks/post-commit
    echo "/usr/bin/python $SVN_REPO_DIR /hooks/trac-post-commit-hook.py -p \$TRAC_ENV -r \$REV" >> "$SVN_REPO_DIR"/hooks/post-commit

    chmod +x "$SVN_REPO_DIR"/hooks/trac-post-commit-hook.py
    chmod +x "$SVN_REPO_DIR"/hooks/post-commit

    # Prepare TRAC
    cp -r "$TEMPLATES_DIR"/kask-template "$TRAC_DIR"
    #chown apache:apache "$TRAC_DIR" -R
    chmod o-rwx "$TRAC_DIR" -R

    NAME_UP=$(echo "$NAME" | tr "a-z" "A-Z")

    # Modify trac.ini
    /usr/bin/perl -p -i -e "s/repository_dir = \/var\/www\/svn\/test/repository_dir = $SVN_REPO_DIR_FOR_INI/g" "$TRAC_INI"
    /usr/bin/perl -p -i -e "s/descr = KASK project template/descr = $DESCRIPTION/g" "$TRAC_INI"
    /usr/bin/perl -p -i -e "s/name = KASK_TEMPLATE_NOT_FOR_ACTUAL_USAGE/name = $NAME_UP/g" "$TRAC_INI"
    /usr/bin/perl -p -i -e "s/url = /url = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME\//g" "$TRAC_INI"
    /usr/bin/perl -p -i -e "s/folder_name = kask-template/folder_name = $NAME/g" "$TRAC_INI"
    /usr/bin/perl -p -i -e "s/link = https:\/\/lab527.eti.pg.gda.pl\/trac\/kask-template/link = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME/g" "$TRAC_INI"

    # zaczytanie aktualnego stanu repozytorium (track odwoluje sie bezposrednio po systemie plikow!)
    trac-admin "$TRAC_DIR" repository resync '(default)'
fi
