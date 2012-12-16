#!/bin/bash

. $SCRIPTS_MASTER_DIR/common_functions.sh

get_input "$@"
check_input_change_ssh_key

cd "$GITOLITE_KEYS_DIR"

# Dodajemy siebie do listy użytkowników, nawet jeśli klucz jest pusty
USERS_LIST=$(cat $GITOLITE_CONFIG_FILE | grep ^@users)
if [[ -z $(echo "$USERS_LIST" | grep -P "\s$USER_NAME\s") ]] ; then
	sed -i "s/$USERS_LIST/${USERS_LIST}${USER_NAME} /" "$GITOLITE_CONFIG_FILE"
	git add "$GITOLITE_CONFIG_FILE"
	git commit "$GITOLITE_CONFIG_FILE" -m "Dodaję użytkownika $USER_NAME do listy w gitolite.conf"
	git push origin master
	if [[ $? -ne 0 ]] ; then
		echo "Błąd: nie udało się dodać użytkownika do listy w gitolite.conf" > /dev/stderr
		git reset --hard
		exit 4
	fi
fi

# W zależności od tego, czy plik istniał wcześniej czy nie,
# podajemy różny commit message w repozytorium Gitolite
if [[ -f $GITOLITE_KEY_FILE && -z "$USER_SSH_KEY" ]] ; then
	COMMIT_MESSAGE="Usuwam klucz dla $USER_NAME"
elif [[ -f $GITOLITE_KEY_FILE && -n "$USER_SSH_KEY" ]] ; then
	COMMIT_MESSAGE="Modyfikuję klucz dla $USER_NAME"
else
	COMMIT_MESSAGE="Ustawiam nowy klucz dla $USER_NAME"
fi

# Po prostu nadpisujemy plik nową wartością
echo "$USER_SSH_KEY" > $GITOLITE_KEY_FILE

# Zapisujemy zmiany w repozytorium
git add "$GITOLITE_KEY_FILE"
git commit "$GITOLITE_KEY_FILE" -m "$COMMIT_MESSAGE"

# Sprawdzamy, czy udał się commit - jeśli nie,
# to klucz jest identyczny, więc kończymy działanie
if [[ $? -eq 1 ]] ; then
	exit
fi

git push origin master

# Sprawdzamy, czy zmiany się spushowały
if [[ $? -ne 0 || -z $(git log -n 1 | grep "$COMMIT_MESSAGE") ]] ; then
	echo "Błąd: nie udało się zmienić klucza SSH" > /dev/stderr
	git reset --hard
	exit 5
fi