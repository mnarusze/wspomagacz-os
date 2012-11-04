#!/bin/bash

SVN=
GIT=
NAME=
TRAC=
DESCRIPTION=
REPOSITORIES_DIR=
TRAC_DIR=
TEMPLATES_DIR=

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
        "a")
            TRAC_DIR="$OPTARG"
            ;;
        "n")
            NAME="$OPTARG"
            ;;
        "d")
            DESCRIPTION="$OPTARG"
            ;;
        "w")
            TEMPLATES_DIR="$OPTARG"
            ;;
        *)
            echo "Błąd: Nieznana opcja $OPTARG"
            exit 1
            ;;
    esac
done

if [[ -z "$NAME" ]] ; then
    echo "Błąd: pusta nazwa"
    exit 1
fi

if [[ -z "$REPOSITORIES_DIR" ]] ; then
    echo "Błąd: brak ścieżki do repozytoriów"
    exit 1
fi

if [[ -z "$GIT" || -z "$SVN" ]] ; then
    echo "Błąd: nie podano typu repozytorium"
    exit 1
fi

if [[ -n "$TRAC" && "$TRAC" -eq 1 && -z "$DESCRIPTION" ]] ; then
    echo "Błąd: dla TRAC trzeba podać opis!"
    exit 1
fi

if [[ -n "$TRAC" && "$TRAC" -eq 1 && -z "$TEMPLATES_DIR" ]] ; then
    echo "Błąd: dla TRAC trzeba podać ścieżkę do katalogu z szablonami!"
    exit 1
fi

if [[ -n "$TRAC" && "$TRAC" -eq 1 && -z "$TRAC_DIR" ]] ; then
    echo "Błąd: brak ścieżki do trac!"
    exit 1
fi

#########
#  GIT  #
#########
if [[ -n "$GIT" && "$GIT" -eq 1 ]] ; then
    REPO_DIR="$REPOSITORIES_DIR"/git/"$NAME"

    if [[ -d "$REPO_DIR" ]] ; then
        echo "Błąd: Repozytorium $REPO_DIR już istnieje!"
        exit 2
    fi
    git --git-dir="$REPO_DIR" init --bare

    chown apache:apache "$REPO_DIR" -R
    chmod o-rwx "$REPO_DIR" -R
fi

#########
#  SVN  #
#########
if [[ -n "$SVN" && "$SVN" -eq 1 ]] ; then
    REPO_DIR="$REPOSITORIES_DIR/svn/$NAME"
    REPO_DIR_FOR_INI=$(echo $REPO_DIR | sed 's/\//\\\//g')

    if [[ -d "$REPO_DIR" ]] ; then
        echo "Błąd: Repozytorium "$REPO_DIR" już istnieje!"
        exit 2
    fi
    svnadmin create "$REPO_DIR"
    
    chown apache:apache "$REPO_DIR" -R
    chmod o-rwx "$REPO_DIR" -R

    ############
    #   TRAC   #
    ############
    if [[ -n "$TRAC" && "$TRAC" -eq 1 ]] ; then
        TRAC_DIR="$TRAC_DIR"/"$NAME"
        TRAC_INI="$TRAC_DIR"/conf/trac.ini

        # Copy and prepare HOOKS
        cp "$TEMPLATES_DIR"/trac-post-commit-hook.py "$REPO_DIR"/hooks
        echo "#!/bin/sh" > "$REPO_DIR"/hooks/post-commit
        echo "REPOS=$1" >> "$REPO_DIR"/hooks/post-commit
        echo "REV=$2" >> "$REPO_DIR"/hooks/post-commit
        echo "TRAC_ENV=$REPO_DIR" >> "$REPO_DIR"/hooks/post-commit
        echo "/usr/bin/python $REPO_DIR /hooks/trac-post-commit-hook.py -p \"$TRAC_ENV\" -r \"$REV\"" >> "$REPO_DIR"/hooks/post-commit

        chmod +x "$REPO_DIR"/hooks/trac-post-commit-hook.py
        chmod +x "$REPO_DIR"/hooks/post-commit

        # Prepare TRAC
        cp -r "$TEMPLATES_DIR"/kask-template "$TRAC_DIR"
        chown apache:apache "$TRAC_DIR" -R
        chmod o-rwx "$TRAC_DIR" -R

        # Modify trac.ini
        /usr/bin/perl -p -i -e "s/repository_dir = \/var\/www\/svn\/test/repository_dir = $REPO_DIR_FOR_INI/g" "$TRAC_INI"
        /usr/bin/perl -p -i -e "s/descr = KASK project template/descr = $DESCRIPTION/g" "$TRAC_INI"
        NAME_UP=$(echo "$NAME" | tr "a-z" "A-Z")
        /usr/bin/perl -p -i -e "s/name = KASK_TEMPLATE_NOT_FOR_ACTUAL_USAGE/name = $NAME_UP/g" "$TRAC_INI"
        /usr/bin/perl -p -i -e "s/url = /url = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME\//g" "$TRAC_INI"
        /usr/bin/perl -p -i -e "s/folder_name = kask-template/folder_name = $NAME/g" "$TRAC_INI"
        /usr/bin/perl -p -i -e "s/link = https:\/\/lab527.eti.pg.gda.pl\/trac\/kask-template/link = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME/g" "$TRAC_INI"

        # zaczytanie aktualnego stanu repozytorium (track odwoluje sie bezposrednio po systemie plikow!)
        trac-admin "$TRAC_DIR" repository resync '(default)'
    fi
fi