#!/bin/bash


function get_input()
{
    # Variables
    SVN= #s
    GIT= #g
    TRAC= #t
    PUBLIC= #p
    NAME= #n
    PROJECT_OWNER= #o
    DESCRIPTION= #d
    USER= #u
    ACCESS_RIGHTS= #a

    # Constants
    TEMPLATES_DIR= #T
    TRAC_DIR= #R
    GITOLITE_ADMIN_DIR= #G
    GIT_REPOS_DIR= #L
    SVN_REPO_DIR= #S
    SVN_ACCESS_CONTROL_FILE= #C
    PROJECTS_ARCHIVE_DIR= #A  

    while getopts ":sgtpn:o:d:u:a:T:R:G:L:S:C:A:" optname ; do
        echo "Opcja : $optname arg: $OPTARG" > /dev/stderr
        case "$optname" in
            "s")
                SVN=1
                ;;
            "g")
                GIT=1
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
            "u")
                USER="$OPTARG"
                ;;    
            "o")
                PROJECT_OWNER="$OPTARG"
                ;;
            "a")
                ACCESS_RIGHTS="$OPTARG"
                ;;
            "T")
                TEMPLATES_DIR="$OPTARG"
                ;;
            "R")
                TRAC_DIR="$OPTARG"
                ;;
            "G")
                GITOLITE_ADMIN_DIR="$OPTARG"
                GITOLITE_CONFIG_FILE="$GITOLITE_ADMIN_DIR/conf/gitolite.conf"
                GITOLITE_KEYS_DIR="$GITOLITE_ADMIN_DIR/keydir"
                ;;
            "L")
                GIT_REPOS_DIR="$OPTARG"
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
}