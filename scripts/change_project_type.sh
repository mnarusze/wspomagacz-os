#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input "$@"
check_input_change_project_type

if [[ "$SVN_ENABLED" -eq 1 ]] ; then
	# Najpierw przenoszenie z jednego pliku do drugiego
	# Przy okazji przenosimy źródła
	if [[ "$PROJECT_PREVIOUS_TYPE" == "PUBLIC" || "$PROJECT_TYPE" == "PUBLIC" ]] ; then
		# Przenosimy grupy
		LINE_NUMBER_FOR_GROUP=$(cat $SVN_ACCESS_CONTROL_FILE | grep ^$ -m 1 -n | sed 's/[^0-9].*//g')
		PROJECT_GROUP_RW=$(cat $SVN_PREVIOUS_ACCESS_CONTROL_FILE | grep "^${PROJECT_NAME}_RW =")
		PROJECT_GROUP_RW_LINE=$(cat $SVN_PREVIOUS_ACCESS_CONTROL_FILE | grep -n "^$PROJECT_GROUP_RW" | sed 's/[^0-9].*//g')
		sed -i "$LINE_NUMBER_FOR_GROUP i $PROJECT_GROUP_RW" "$SVN_ACCESS_CONTROL_FILE"
		sed -i "${PROJECT_GROUP_RW_LINE}d" "$SVN_PREVIOUS_ACCESS_CONTROL_FILE"
		((LINE_NUMBER_FOR_GROUP++))
		
		PROJECT_GROUP_R=$(cat $SVN_PREVIOUS_ACCESS_CONTROL_FILE | grep "^${PROJECT_NAME}_R =")
		PROJECT_GROUP_R_LINE=$(cat $SVN_PREVIOUS_ACCESS_CONTROL_FILE | grep -n "^$PROJECT_GROUP_R" | sed 's/[^0-9].*//g')
		sed -i "$LINE_NUMBER_FOR_GROUP i $PROJECT_GROUP_R" "$SVN_ACCESS_CONTROL_FILE"
		sed -i "${PROJECT_GROUP_R_LINE}d" "$SVN_PREVIOUS_ACCESS_CONTROL_FILE"

		# Przenosimy uprawnienia
		ACCESS_RIGHTS=$(sed -n "/#${PROJECT_NAME}_SECTION_BEGIN/,/#${PROJECT_NAME}_SECTION_END/p" $SVN_PREVIOUS_ACCESS_CONTROL_FILE)
		echo "$ACCESS_RIGHTS" >> "$SVN_ACCESS_CONTROL_FILE"
		sed -i "/#${PROJECT_NAME}_SECTION_BEGIN/,/#${PROJECT_NAME}_SECTION_END/d" $SVN_PREVIOUS_ACCESS_CONTROL_FILE

		# Przenosimy repo
		mv $SVN_PREVIOUS_REPO_DIR $SVN_REPO_DIR
	fi
	# Potem modyfikacja, czyli najpierw usuwamy, potem dodajemy
	sed -i "/#${PROJECT_NAME}_SECTION_BEGIN/,/#${PROJECT_NAME}_SECTION_END/d" $SVN_ACCESS_CONTROL_FILE
	
	# Ustawiamy uprawnienia dla poszczególnych grup
	echo "#${PROJECT_NAME}_SECTION_BEGIN" >> $SVN_ACCESS_CONTROL_FILE
	echo "[$PROJECT_NAME:/]" >> $SVN_ACCESS_CONTROL_FILE
	echo "@${PROJECT_NAME}_RW = rw" >> $SVN_ACCESS_CONTROL_FILE
	echo "@${PROJECT_NAME}_R = r" >> $SVN_ACCESS_CONTROL_FILE

	# Globalne read-only jeśli publiczny
	if [[ $PROJECT_TYPE == "PUBLIC" || $PROJECT_TYPE == "PARTIALLY_PUBLIC" ]] ; then
	    echo "* = r" >> $SVN_ACCESS_CONTROL_FILE
	fi

	echo "#${PROJECT_NAME}_SECTION_END" >> $SVN_ACCESS_CONTROL_FILE
fi

if [[ "$GIT_ENABLED" -eq 1 ]] ; then
	cd "$GITOLITE_ADMIN_DIR"

    READ_ONLY_USERS_LIST=$(cat $GITOLITE_CONFIG_FILE | grep -P "^@${PROJECT_NAME}_R\t")

	# Najpierw usuwamy...
	if [[ $PROJECT_PREVIOUS_TYPE == "PUBLIC" ]] ; then
		NEW_READ_ONLY_USERS_LIST=$(echo "$READ_ONLY_USERS_LIST" | sed "s/daemon[ ]//")
	elif [[ $PROJECT_PREVIOUS_TYPE == "PARTIALLY_PUBLIC" ]] ; then
		NEW_READ_ONLY_USERS_LIST=$(echo "$READ_ONLY_USERS_LIST" | sed "s/@users[ ]//")
	else
		NEW_READ_ONLY_USERS_LIST="$READ_ONLY_USERS_LIST"
	fi

	# Następnie dodajemy potrzebne uprawnienia
	if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
		NEW_READ_ONLY_USERS_LIST="${NEW_READ_ONLY_USERS_LIST}daemon "
	elif [[ $PROJECT_TYPE == "PARTIALLY_PUBLIC" ]] ; then
		NEW_READ_ONLY_USERS_LIST="${NEW_READ_ONLY_USERS_LIST}@users "
	fi

	# Podmieniamy listę użytkowników read-only
	sed -i "s/$READ_ONLY_USERS_LIST/$NEW_READ_ONLY_USERS_LIST/" "$GITOLITE_CONFIG_FILE"

    git add "$GITOLITE_CONFIG_FILE"
    git commit "$GITOLITE_CONFIG_FILE" -m "Modyfikuję typ projektu $PROJECT_NAME z $PROJECT_PREVIOUS_TYPE na \
    	$PROJECT_TYPE"
    git push origin master

    if [[ -z $(git log -n 1 | grep "Modyfikuję typ projektu $PROJECT_NAME") ]] ; then
    	echo "Błąd: nie udało się zmodyfikować projektu!"
    	git reset --hard
    	exit 8
    fi	
fi