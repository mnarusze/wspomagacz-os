#!/bin/bash

function get_input()
{
    # Boolean variables
    SVN_ENABLED= #s
    GIT_ENABLED= #g
    TRAC_ENABLED= #t
    REDMINE_ENABLED= #r
    
    # Project variables
    PROJECT_TYPE= #T
    PROJECT_NAME= #N
    PROJECT_OWNER= #O
    PROJECT_DESCRIPTION= #D

    # User variables
    USER_NAME= #L
    USER_ACCESS_RIGHTS= #A
    USER_SSH_KEY= #S

    # Constants
    TEMPLATES_DIR=/home/maryl/NetBeansProjects/wspomagacz-os/templates
    TRAC_MASTER_DIR=/var/www/trac
    GITOLITE_ADMIN_DIR=/home/maryl/repositories/gitolite-admin
    GITOLITE_CONFIG_FILE=$GITOLITE_ADMIN_DIR/conf/gitolite.conf
    GITOLITE_KEYS_DIR=$GITOLITE_ADMIN_DIR/keydir
    GIT_REPOS_DIR=/var/lib/git/repositories
    SVN_REPOS_DIR=/var/www/svn
    SVN_PUB_REPOS_DIR=$SVN_REPOS_DIR/pub
    SVN_ACCESS_CONTROL_FILE=$SVN_REPOS_DIR/.access
    SVN_PUB_ACCESS_CONTROL_FILE=$SVN_PUB_REPOS_DIR/.access
    PROJECTS_ARCHIVE_DIR=/home/maryl/Archive

    while getopts ":sgtrT:N:O:D:L:A:S:" optname ; do
        echo "Opcja : $optname arg: $OPTARG" > /dev/stderr
        case "$optname" in
            "s")
                SVN_ENABLED=1
                ;;
            "g")
                GIT_ENABLED=1
                ;;
            "t")
                TRAC_ENABLED=1
                ;;
            "r")
                REDMINE_ENABLED=1
                ;;
            "T")
                PROJECT_TYPE="$OPTARG"
                ;;
            "N")
                PROJECT_NAME="$OPTARG"
                ;;
            "O")
                PROJECT_OWNER="$OPTARG"
                ;;
            "D")
                PROJECT_DESCRIPTION="$OPTARG"
                ;;    
            "L")
                USER_NAME="$OPTARG"
                ;;
            "A")
                USER_ACCESS_RIGHTS="$OPTARG"
                ;;
            "S")
                USER_SSH_KEY="$OPTARG"
                echo "USER_SSH_KEY : $OPTARG" > /dev/stderr
                ;;    
            *)
                echo "Błąd: Nieznana opcja $OPTARG" > /dev/stderr
                exit 1
                ;;
        esac
    done
}

function check_input_create_git_repo()
{
    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa" > /dev/stderr
        exit 1
    fi

    if [[ -z "$PROJECT_OWNER" ]] ; then
        echo "Błąd: brakuje właściciela projektu PROJECT_OWNER!!" > /dev/stderr
        exit 2
    fi

    if [[ -z "$PROJECT_TYPE" ]] ; then
        echo "Błąd: brakuje typu projektu PROJECT_TYPE!!" > /dev/stderr
        exit 3
    fi

    if [[ -z "$GITOLITE_ADMIN_DIR" || ! -d "$GITOLITE_ADMIN_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do repozytorium Gitolite: $GITOLITE_ADMIN_DIR" > /dev/stderr
        exit 4
    else
        OWNER_SSH_KEY="$GITOLITE_KEYS_DIR/${PROJECT_OWNER}.pub"
    fi

    if [[ ! -f "$GITOLITE_CONFIG_FILE" ]] ; then
        echo "Błąd: brak pliku konfiguracyjnego Gitolite: $GITOLITE_CONFIG_FILE" > /dev/stderr
        exit 5
    fi

    if [[ -n $(cat $GITOLITE_CONFIG_FILE | grep "repo ${PROJECT_NAME}$") ]] ; then
        echo "Błąd: repozytorium o podanej nazwie $PROJECT_NAME już istnieje!" > /dev/stderr
        exit 6
    fi

    if [[ ! -f "$OWNER_SSH_KEY" ]] ; then
        echo "Błąd: brakuje klucza ssh $OWNER_SSH_KEY dla właściciela $PROJECT_OWNER!!" > /dev/stderr
        exit 7
    fi
}

function check_input_create_svn_repo()
{
    if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
        SVN_ACCESS_CONTROL_FILE=$SVN_PUB_ACCESS_CONTROL_FILE
        SVN_REPO_DIR="$SVN_PUB_REPOS_DIR/$PROJECT_NAME"
    else
        SVN_REPO_DIR="$SVN_REPOS_DIR/$PROJECT_NAME"
    fi

    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa" > /dev/stderr
        exit 1
    fi

    if [[ -z "$PROJECT_OWNER" ]] ; then
        echo "Błąd: brakuje właściciela projektu PROJECT_OWNER!!" > /dev/stderr
        exit 2
    fi

    if [[ -z "$PROJECT_TYPE" ]] ; then
        echo "Błąd: brakuje typu projektu PROJECT_TYPE!!" > /dev/stderr
        exit 3
    fi

    if [[ -z "$SVN_REPOS_DIR" || ! -d "$SVN_REPOS_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do repozytoriów SVN : $SVN_REPOS_DIR" > /dev/stderr
        exit 4
    fi

    if [[ -z "$SVN_ACCESS_CONTROL_FILE" || ! -f "$SVN_ACCESS_CONTROL_FILE" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do pliku kontroli dostępu SVN_ACCESS_CONTROL_FILE \
        : $SVN_ACCESS_CONTROL_FILE" > /dev/stderr
        exit 5
    fi

    if [[ -d "$SVN_REPO_DIR" ]] ; then
        echo "Błąd: Repozytorium $SVN_REPO_DIR już istnieje!" > /dev/stderr
        exit 6
    fi
}

function check_input_add_trac()
{
    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa" > /dev/stderr
        exit 1
    fi

    if [[ -z "$TRAC_MASTER_DIR" || ! -d "$TRAC_MASTER_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do traca TRAC_MASTER_DIR : $TRAC_MASTER_DIR" > /dev/stderr
        exit 2
    else
        TRAC_DIR="$TRAC_MASTER_DIR/$PROJECT_NAME"
    fi

    if [[ -z "$TEMPLATES_DIR" || ! -d "$TEMPLATES_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do szablonów TEMPLATES_DIR : $TEMPLATES_DIR" > /dev/stderr
        exit 3
    fi

    # Na razie description sypie resztę - oszukujemy system...
    #if [[ -z "$PROJECT_DESCRIPTION" ]] ; then
    #    echo "Błąd: nie podano opisu projektu PROJECT_DESCRIPTION" > /dev/stderr
    #    exit 4
    #fi

    SVN_REPO_DIR=$SVN_REPOS_DIR/$PROJECT_NAME

    if [[ -z "$SVN_REPO_DIR" || ! -d "$SVN_REPO_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do repozytorium SVN_REPO_DIR : $SVN_REPO_DIR" > /dev/stderr
        exit 5
    fi    
}

function check_input_delete_git_repo()
{
    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa projektu" > /dev/stderr
        exit 1
    fi

    if [[ -z "$GIT_REPOS_DIR" || ! -d "$GIT_REPOS_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do repozytoriów GIT GIT_REPOS_DIR:\
         $GIT_REPOS_DIR" > /dev/stderr
        exit 2
    else
        GIT_REPO_DIR="$GIT_REPOS_DIR/${PROJECT_NAME}.git"
    fi

    if [[ ! -d "$GIT_REPO_DIR" ]] ; then
        echo "Błąd: nieprawidłowa ścieżka do repozytorium GIT GIT_REPO_DIR:\
         $GIT_REPO_DIR" > /dev/stderr
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

    if [[ -z $(cat $GITOLITE_CONFIG_FILE | grep "repo ${PROJECT_NAME}$") ]] ; then
        echo "Błąd: repozytorium o podanej nazwie $PROJECT_NAME nie istnieje!" > /dev/stderr
        exit 6
    fi

    if [[ -z "$PROJECTS_ARCHIVE_DIR" || ! -d "$PROJECTS_ARCHIVE_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do archiwum PROJECTS_ARCHIVE_DIR: $PROJECTS_ARCHIVE_DIR" > /dev/stderr
        exit 7
    fi

    TODAY=$(date +%F)

    if [[ ! -d "$PROJECTS_ARCHIVE_DIR/$TODAY/git" ]] ; then
        mkdir -p "$PROJECTS_ARCHIVE_DIR/$TODAY/git"
    elif [[ -d "$PROJECTS_ARCHIVE_DIR/$TODAY/git/$PROJECT_NAME" ]] ; then
        echo "Błąd: Repozytorium $PROJECT_NAME zostało już dzisiaj ($TODAY) umieszczone w archiwum!" > /dev/stderr
        exit 8
    fi
}

function check_input_add_user()
{
    if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
        SVN_ACCESS_CONTROL_FILE=$SVN_PUB_ACCESS_CONTROL_FILE
    fi

    if [[ -z "$USER_NAME" ]] ; then
        echo "Błąd: nie podano nazwy uzytkownika USER_NAME" > /dev/stderr
        exit 1
    fi

    if [[ -z "$USER_ACCESS_RIGHTS" ]] ; then
        echo "Błąd: nie podano uprawnień uzytkownika USER_ACCESS_RIGHTS" > /dev/stderr
        exit 2
    fi

    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa projektu" > /dev/stderr
        exit 3
    fi

    if [[ "$SVN_ENABLED" -eq 1 ]] ; then
        if [[ -z "$SVN_ACCESS_CONTROL_FILE" || ! -f "$SVN_ACCESS_CONTROL_FILE" ]] ; then
            echo "Błąd: nie podano lub nieprawidłowa ścieżka do pliku kontroli dostępu SVN_ACCESS_CONTROL_FILE \
                : $SVN_ACCESS_CONTROL_FILE" > /dev/stderr
            exit 4
        fi
    fi

    if [[ "$GIT_ENABLED" -eq 1 ]] ; then
        if [[ ! -f "$GITOLITE_CONFIG_FILE" ]] ; then
            echo "Błąd: brak pliku konfiguracyjnego Gitolite: $GITOLITE_CONFIG_FILE" > /dev/stderr
            exit 5
        fi
    fi
}

function check_input_remove_user()
{
    if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
        SVN_ACCESS_CONTROL_FILE=$SVN_PUB_ACCESS_CONTROL_FILE
    fi

    if [[ -z "$USER_NAME" ]] ; then
        echo "Błąd: nie podano nazwy uzytkownika USER_NAME" > /dev/stderr
        exit 1
    fi

    if [[ -z "$PROJECT_NAME" ]] ; then
        echo "Błąd: pusta nazwa projektu" > /dev/stderr
        exit 2
    fi

    if [[ "$SVN_ENABLED" -eq 1 ]] ; then
        if [[ -z "$SVN_ACCESS_CONTROL_FILE" || ! -f "$SVN_ACCESS_CONTROL_FILE" ]] ; then
            echo "Błąd: nie podano lub nieprawidłowa ścieżka do pliku kontroli dostępu SVN_ACCESS_CONTROL_FILE \
                : $SVN_ACCESS_CONTROL_FILE" > /dev/stderr
            exit 3
        fi
    fi

    if [[ "$GIT_ENABLED" -eq 1 ]] ; then
        if [[ ! -f "$GITOLITE_CONFIG_FILE" ]] ; then
            echo "Błąd: brak pliku konfiguracyjnego Gitolite: $GITOLITE_CONFIG_FILE" > /dev/stderr
            exit 4
        fi
    fi
}

function check_input_change_ssh_key()
{
    if [[ -z "$USER_NAME" ]] ; then
        echo "Błąd: nie podano nazwy uzytkownika USER_NAME" > /dev/stderr
        exit 1
    fi

    if [[ -z "$GITOLITE_KEYS_DIR" || ! -d "$GITOLITE_KEYS_DIR" ]] ; then
        echo "Błąd: nie podano lub nieprawidłowa ścieżka do katalogu kluczy SSH GITOLITE_KEYS_DIR:\
         $GITOLITE_KEYS_DIR" > /dev/stderr
        exit 2 
    fi

    if [[ ! -f "$GITOLITE_CONFIG_FILE" ]] ; then
        echo "Błąd: brak pliku konfiguracyjnego Gitolite: $GITOLITE_CONFIG_FILE" > /dev/stderr
        exit 3
    fi

    GITOLITE_KEY_FILE=$GITOLITE_KEYS_DIR/${USER_NAME}.pub

    if [[ -z "$USER_SSH_KEY" ]] ; then
        echo "Uwaga: nie podano nowego klucza USER_SSH_KEY" > /dev/stderr
    fi
}