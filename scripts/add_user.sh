#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input "$@"
check_input_add_user

if [[ "$SVN_ENABLED" -eq 1 ]] ; then
	if [[ "$USER_ACCESS_RIGHTS" == "administrator" || 
		"$USER_ACCESS_RIGHTS" == "developer" ]] ; then
		ACCESS_TYPE=RW
	else
		ACCESS_TYPE=R
	fi
    USERS_LIST="$(cat $SVN_ACCESS_CONTROL_FILE | grep -m 1 "^${PROJECT_NAME}_${ACCESS_TYPE} = ")"
    sed -i "s/$USERS_LIST/$USERS_LIST $USER_NAME,/" "$SVN_ACCESS_CONTROL_FILE"
    if [[ -z $(cat $SVN_ACCESS_CONTROL_FILE | grep "$USERS_LIST $USER_NAME,") ]] ; then
    	echo "Błąd: nie udało się dodać użytkownika!"
    	exit 6
    fi
fi

if [[ "$GIT_ENABLED" -eq 1 ]] ; then
	cd "$GITOLITE_ADMIN_DIR"
    if [[ "$USER_ACCESS_RIGHTS" == "administrator" ]] ; then
		ACCESS_TYPE="RW\+"
	elif [[ "$USER_ACCESS_RIGHTS" == "developer" ]] ; then
		ACCESS_TYPE="RW"
	else
		ACCESS_TYPE="R"
	fi
	USERS_LIST=$(cat $GITOLITE_CONFIG_FILE | grep -P "^@${PROJECT_NAME}_${ACCESS_TYPE}\t")
	sed -i "s/${USERS_LIST}/${USERS_LIST}${USER_NAME} /" "$GITOLITE_CONFIG_FILE"
	if [[ -z $(cat $GITOLITE_CONFIG_FILE | grep "${USERS_LIST}${USER_NAME}") ]] ; then
    	echo "Błąd: nie udało się dodać użytkownika!"
        git reset --hard
    	exit 7
    fi

    git add "$GITOLITE_CONFIG_FILE"
    git commit "$GITOLITE_CONFIG_FILE" -m "Dodaję użytkownika $USER_NAME do projektu $PROJECT_NAME, \
    	prawa $USER_ACCESS_RIGHTS"
    git push origin master

    if [[ -z $(git log -n 1 | grep "Dodaję użytkownika $USER_NAME do projektu $PROJECT_NAME") ]] ; then
    	echo "Błąd: nie udało się dodać użytkownika!"
    	git reset --hard
    	exit 8
    fi	
fi