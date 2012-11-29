#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input $*
check_input_create_svn_repo

# Kopia obecnego configu
cp $SVN_ACCESS_CONTROL_FILE ${SVN_ACCESS_CONTROL_FILE}.swp

# Szukamy pierwszej wolnej linii i dodajemy grupę RW
LINE_NUMBER_FOR_USERS=$(cat $SVN_ACCESS_CONTROL_FILE | grep  ^$ -m 1 -n | sed 's/[^0-9].*//g')
sed -i "$LINE_NUMBER_FOR_USERS i $PROJECT_NAME = $PROJECT_OWNER," $SVN_ACCESS_CONTROL_FILE

# Jeśli prywatny, to dodajemy grupę read-only
if [[ $PROJECT_TYPE == "PRIVATE" ]] ; then
    ((LINE_NUMBER_FOR_USERS++))
    sed -i "$LINE_NUMBER_FOR_USERS i ${PROJECT_NAME}_read_only = " $SVN_ACCESS_CONTROL_FILE
fi

# Ustawiamy uprawnienia dla poszczególnych grup
echo "#${PROJECT_NAME}_SECTION_BEGIN" >> $SVN_ACCESS_CONTROL_FILE
echo "[$PROJECT_NAME:/]" >> $SVN_ACCESS_CONTROL_FILE
echo "@$PROJECT_NAME = rw" >> $SVN_ACCESS_CONTROL_FILE
if [[ $PROJECT_TYPE == "PUBLIC" ]] ; then
    echo "* = r" >> $SVN_ACCESS_CONTROL_FILE
else
    echo "@${PROJECT_NAME}_read_only = r" >> $SVN_ACCESS_CONTROL_FILE
fi
echo "#${PROJECT_NAME}_SECTION_END" >> $SVN_ACCESS_CONTROL_FILE
echo "" >> $SVN_ACCESS_CONTROL_FILE

# Tworzymy nowe repo
svnadmin create "$SVN_REPO_DIR"

# Sprawdzamy, czy repo istnieje
svnlook info "$SVN_REPO_DIR"
if [[ $? -ne 0 || ! -d $SVN_REPO_DIR ]] ; then
    echo "Błąd: nie udało się utworzyć repozytorium $SVN_REPO_DIR"
    # Cofamy zmiany jeśli problem
    mv ${SVN_ACCESS_CONTROL_FILE}.swp $SVN_ACCESS_CONTROL_FILE
    exit 1
fi

# Usuwamy plik tymczasowy jeśli ok
rm ${SVN_ACCESS_CONTROL_FILE}.swp