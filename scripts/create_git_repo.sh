#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input $*
check_input_create_git_repo

cd "$GITOLITE_ADMIN_DIR"

# Kopiujemy aktualny config - jeśli coś pójdzie nie tak, wstawiamy go z powrotem
cp $GITOLITE_CONFIG_FILE ${GITOLITE_CONFIG_FILE}.${PROJECT_OWNER}.swp

# Szukamy pierwszej wolnej linii
LINE_NUMBER_FOR_GROUP=$(cat $GITOLITE_CONFIG_FILE | grep  ^$ -m 1 -n | sed 's/[^0-9].*//g')
sed -i "$LINE_NUMBER_FOR_GROUP i @${PROJECT_NAME}_RW+\t=\t$PROJECT_OWNER " $GITOLITE_CONFIG_FILE
((LINE_NUMBER_FOR_GROUP++))
sed -i "$LINE_NUMBER_FOR_GROUP i @${PROJECT_NAME}_RW\t=\t" $GITOLITE_CONFIG_FILE
((LINE_NUMBER_FOR_GROUP++))
sed -i "$LINE_NUMBER_FOR_GROUP i @${PROJECT_NAME}_R\t=\t" $GITOLITE_CONFIG_FILE

# Tworzymy projekt i zapisujemy opcje dostępu
echo "repo $PROJECT_NAME" >> "$GITOLITE_CONFIG_FILE"
echo -e "\tRW+\t\t=\t@${PROJECT_NAME}_RW+" >> "$GITOLITE_CONFIG_FILE"
echo -e "\tRW\t\t=\t@${PROJECT_NAME}_RW" >> "$GITOLITE_CONFIG_FILE"
echo -e "\tR\t\t=\t@${PROJECT_NAME}_R" >> "$GITOLITE_CONFIG_FILE"
if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
    echo -e "\tR\t\t=\tdaemon" >> "$GITOLITE_CONFIG_FILE"
fi

# Zapisujemy zmiany w Gitolite
git add $GITOLITE_CONFIG_FILE
git commit $GITOLITE_CONFIG_FILE -m "Dodano projekt $PROJECT_NAME typu $PROJECT_TYPE, właściciel: $PROJECT_OWNER"
git push origin master

if [[ -z "$(ssh git@localhost info | grep " $PROJECT_NAME ") " ]] ; then
    echo "Błąd: nie udało się utworzyć repo o nazwie $PROJECT_NAME!"
    # Wstawiamy config z powrotem
    mv ${GITOLITE_CONFIG_FILE}.swp $GITOLITE_CONFIG_FILE 
    git add $GITOLITE_CONFIG_FILE
    git commit $GITOLITE_CONFIG_FILE -m "Cofnięcie ostatnich zmian - brak repozytorium"
    git push origin master
    exit 1
fi
# Jeśli wszystko ok, usuwamy plik tymczasowy
rm ${GITOLITE_CONFIG_FILE}.${PROJECT_OWNER}.swp