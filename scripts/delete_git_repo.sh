#!/bin/bash

. $SCRIPTS_MASTER_DIR/common_functions.sh

get_input "$@"
check_input_delete_git_repo

# Przenosimy do archiwum
mv $GIT_REPO_DIR $PROJECTS_ARCHIVE_DIR/$TODAY/git

# Kasujemy grupy
LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep "^@${PROJECT_NAME}_RW+" -m 1 -n | sed 's/[^0-9].*//g')
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep "^@${PROJECT_NAME}_RW" -m 1 -n | sed 's/[^0-9].*//g')
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep "^@${PROJECT_NAME}_R" -m 1 -n | sed 's/[^0-9].*//g')
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE

# Kasujemy uprawnienia do repo, szukamy linijki z "repo $PROJEKT" i kasujemy do następnego "repo XXX"
# 4 linijki, bo mamy jedną na nazwę repo + 3 na RW+,RW i R
# Jeśli piąta zawiera "daemon" lub "@users", to też kasujemy
LINE_NUMBER_TO_DELETE=$(cat $GITOLITE_CONFIG_FILE | grep "^repo ${PROJECT_NAME}$" -m 1 -n | sed 's/[^0-9].*//g')
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE
sed -i -e "${LINE_NUMBER_TO_DELETE}d" $GITOLITE_CONFIG_FILE

# Sprawdźmy, czy w configu nie ma żadnej linii z naszym repo  czy zawiera
# standardowe info o wszystkich repozytoriach (repo @all)
if [[ -n $(cat $GITOLITE_CONFIG_FILE | grep "repo ${PROJECT_NAME}_RW+$") \
	|| -n $(cat $GITOLITE_CONFIG_FILE | grep "repo ${PROJECT_NAME}_RW$") \
	|| -n $(cat $GITOLITE_CONFIG_FILE | grep "repo ${PROJECT_NAME}_R$") \
    || -n $(cat $GITOLITE_CONFIG_FILE | grep "@${PROJECT_NAME}\t=\t") \
    || -z $(cat $GITOLITE_CONFIG_FILE | grep "^repo @all$") ]] ; then
    echo "Błąd: nie udało się usunąć informacji o repo lub usunięto zbyt dużo." > /dev/stderr
    # No to przywracamy...
    mv $PROJECTS_ARCHIVE_DIR/$TODAY/git/${PROJECT_NAME}.git $GIT_REPO_DIR
    git reset --hard
    exit 7
fi

# OK - pushujemy zmiany
cd $GITOLITE_ADMIN_DIR
git add $GITOLITE_CONFIG_FILE
git commit $GITOLITE_CONFIG_FILE -m "Usuwam repozytorium $PROJECT_NAME"
git push origin master