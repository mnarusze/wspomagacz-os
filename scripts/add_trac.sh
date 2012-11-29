#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input $*
check_input_add_trac

# Na razie description sypie resztę - oszukujemy system...
PROJECT_DESCRIPTION="Tymczasowe description dla $PROJECT_NAME"

# Dodatkowe backslashe aby skrypty obsłużyły nazwę repozytorium
SVN_REPO_DIR_FOR_INI=$(echo $SVN_REPO_DIR | sed 's/\//\\\//g')

# Plik konfiguracyjny dla naszego Trac'a
TRAC_INI="$TRAC_DIR"/conf/trac.ini

# Kopiujemy hooki do repozytorium SVN i ustawiamy
cp "$TEMPLATES_DIR"/trac-post-commit-hook.py "$SVN_REPO_DIR"/hooks
echo "#!/bin/sh" > "$SVN_REPO_DIR"/hooks/post-commit
echo "REPOS=\$1" >> "$SVN_REPO_DIR"/hooks/post-commit
echo "REV=\$2" >> "$SVN_REPO_DIR"/hooks/post-commit
echo "TRAC_ENV=$SVN_REPO_DIR" >> "$SVN_REPO_DIR"/hooks/post-commit
echo "/usr/bin/python $SVN_REPO_DIR /hooks/trac-post-commit-hook.py -p \$TRAC_ENV -r \$REV" >> "$SVN_REPO_DIR"/hooks/post-commit

chmod +x "$SVN_REPO_DIR"/hooks/trac-post-commit-hook.py
chmod +x "$SVN_REPO_DIR"/hooks/post-commit

# Kopiowanie szablonu do naszego Traca
cp -r "$TEMPLATES_DIR"/kask-template "$TRAC_DIR"

PROJECT_NAME_UP=$(echo "$PROJECT_NAME" | tr "a-z" "A-Z")

# Konfiguracja Traca
/usr/bin/perl -p -i -e "s/repository_dir = \/var\/www\/svn\/test/repository_dir = $SVN_REPO_DIR_FOR_INI/g" "$TRAC_INI"
/usr/bin/perl -p -i -e "s/descr = KASK project template/descr = $PROJECT_DESCRIPTION/g" "$TRAC_INI"
/usr/bin/perl -p -i -e "s/name = KASK_TEMPLATE_NOT_FOR_ACTUAL_USAGE/name = $PROJECT_NAME_UP/g" "$TRAC_INI"
/usr/bin/perl -p -i -e "s/url = /url = https:\/\/kask.eti.pg.gda.pl\/trac\/$PROJECT_NAME\//g" "$TRAC_INI"
/usr/bin/perl -p -i -e "s/folder_name = kask-template/folder_name = $PROJECT_NAME/g" "$TRAC_INI"
/usr/bin/perl -p -i -e "s/link = https:\/\/lab527.eti.pg.gda.pl\/trac\/kask-template/link = https:\/\/kask.eti.pg.gda.pl\/trac\/$PROJECT_NAME/g" "$TRAC_INI"

# Zaczytanie aktualnego stanu repozytorium (track odwoluje sie bezposrednio po systemie plikow!)
trac-admin "$TRAC_DIR" repository resync '(default)'

# Sprawdźmy, czy wszystko ok
if [[ $? -ne 0 ]] ; then
    echo "Błąd: nie udało się dodać Traca w $TRAC_DIR dla $PROJECT_NAME" > /dev/stderr
    exit 7
fi