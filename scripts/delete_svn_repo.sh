#!/bin/bash

. $SCRIPTS_MASTER_DIR/common_functions.sh

get_input "$@"
check_input_delete_svn_repo

sed "/#${PROJECT_NAME}_SECTION_BEGIN/,/#${PROJECT_NAME}_SECTION_END/d" ${SVN_ACCESS_CONTROL_FILE} | grep -Ev "^${PROJECT_NAME}_R" | cat -s > ${SVN_ACCESS_CONTROL_FILE}.tmp
if [[ -n "$(cat ${SVN_ACCESS_CONTROL_FILE}.tmp)" || -z "$(cat ${SVN_ACCESS_CONTROL_FILE}.tmp | grep ^${PROJECT_NAME}_R)" ]] ; then
    mv ${SVN_ACCESS_CONTROL_FILE}.tmp $SVN_ACCESS_CONTROL_FILE
else
    echo "Błąd: nie udało się usunąć praw dostępu. Sprawdź plik ${SVN_ACCESS_CONTROL_FILE}.tmp" > /dev/stderr
    exit 1
fi

mv $SVN_REPO_DIR $PROJECTS_ARCHIVE_DIR/svn
if [[ -d $SVN_REPO_DIR ]] ; then
    echo "Błąd: nie udało się usunąć repozytorium" > /dev/stderr
    exit 2
fi