#!/bin/bash

. $SCRIPTS_MASTER_DIR/common_functions.sh

get_input $*
check_input_remove_user

if [[ "$SVN_ENABLED" -eq 1 ]] ; then
    USERS_LIST=$(cat $SVN_ACCESS_CONTROL_FILE | grep "${PROJECT_NAME}_R" | grep $USER_NAME)
    NEW_USERS_LIST=$(echo -e "$USERS_LIST" | sed "s/ $USER_NAME,//")
    sed -i "s/$USERS_LIST/$NEW_USERS_LIST/" "$SVN_ACCESS_CONTROL_FILE"
    if [[ -n $(cat $SVN_ACCESS_CONTROL_FILE | grep "${PROJECT_NAME}_R" | grep $USER_NAME) ]] ; then
    	echo "Błąd: nie udało się usunąć użytkownika!"
    	exit 5
    fi
fi

if [[ "$GIT_ENABLED" -eq 1 ]] ; then
	cd "$GITOLITE_ADMIN_DIR"
	USERS_LIST="$(cat $GITOLITE_CONFIG_FILE | grep "^@${PROJECT_NAME}_R" | grep $USER_NAME)"
	NEW_USERS_LIST=$(echo -e "$USERS_LIST" | sed "s/${USER_NAME} //")
	sed -i "s/$USERS_LIST/$NEW_USERS_LIST/" "$GITOLITE_CONFIG_FILE"
	if [[ -n $(cat $GITOLITE_CONFIG_FILE | grep "$USERS_LIST $USER_NAME") ]] ; then
    	echo "Błąd: nie udało się usunąć użytkownika!"
    	exit 6
    fi

    git add "$GITOLITE_CONFIG_FILE"
    git commit "$GITOLITE_CONFIG_FILE" -m "Usuwam użytkownika $USER_NAME z projektu $PROJECT_NAME"
    git push origin master

    if [[ -z $(git log -n 1 | grep "Usuwam użytkownika $USER_NAME z projektu $PROJECT_NAME") ]] ; then
    	echo "Błąd: nie udało się usunąć użytkownika!"
    	git reset --hard
    	exit 7
    fi	
fi

if [[ "$TRAC_ENABLED" -eq 1 ]] ; then
    trac-admin "$TRAC_DIR" permission remove "$USER_NAME" '*'
fi