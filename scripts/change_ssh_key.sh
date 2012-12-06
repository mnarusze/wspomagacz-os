#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input "$@"
check_input_change_ssh_key

cd "$GITOLITE_KEYS_DIR"

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
	echo "Błąd: nie udało się zapisać zmian w repozytorium" > /dev/stderr
	git reset --hard
	exit 3
fi