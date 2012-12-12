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

# Stworzenie TRAC
trac-admin "$TRAC_DIR" initenv "$PROJECT_NAME" "sqlite:db/trac.db"

# Konfiguracja
trac-admin "$TRAC_DIR" config set project url http://localhost/trac/"$PROJECT_NAME"
trac-admin "$TRAC_DIR" config set project descr "Szablon opisu dla projektu $PROJECT_NAME"

# Dodajemy repozytorium, jeśli istnieje
if [[ "$SVN_ENABLED" -eq 1 ]] ; then
	trac-admin "$TRAC_DIR" repository add "$PROJECT_NAME" "$SVN_REPO_DIR"
	trac-admin "$TRAC_DIR" repository resync '(default)'

	# Kopiujemy hooki do repozytorium SVN i ustawiamy
	cp "$TEMPLATES_DIR"/trac-post-commit-hook.py "$SVN_REPO_DIR"/hooks
	echo "#!/bin/sh" > "$SVN_REPO_DIR"/hooks/post-commit
	echo "REPOS=\$1" >> "$SVN_REPO_DIR"/hooks/post-commit
	echo "REV=\$2" >> "$SVN_REPO_DIR"/hooks/post-commit
	echo "TRAC_ENV=$SVN_REPO_DIR" >> "$SVN_REPO_DIR"/hooks/post-commit
	echo "/usr/bin/python $SVN_REPO_DIR /hooks/trac-post-commit-hook.py -p \$TRAC_ENV -r \$REV" >> "$SVN_REPO_DIR"/hooks/post-commit
	chmod +x "$SVN_REPO_DIR"/hooks/trac-post-commit-hook.py
	chmod +x "$SVN_REPO_DIR"/hooks/post-commit
fi

# Ustawienie uprawnień - usuwamy domyślne i potem ustawimy nasze
trac-admin "$TRAC_DIR" permission remove anonymous '*'
trac-admin "$TRAC_DIR" permission remove authenticated '*'

# Admin - zawsze
trac-admin "$TRAC_DIR" permission add "$PROJECT_OWNER" $TRAC_ADMIN_ACCESS_RIGHTS

if [[ "$PROJECT_TYPE" == "PUBLIC" ]] ; then
	# Jeśli publiczne, to dajemy nasze domyślne dla anonimowych i uwierzytelnionych
	trac-admin "$TRAC_DIR" permission add anonymous $TRAC_ANONYMOUS_ACCESS_RIGHTS
	trac-admin "$TRAC_DIR" permission add authenticated $TRAC_AUTHENTICATED_ACCESS_RIGHTS
elif [[ "$PROJECT_TYPE" == "PARTIALLY_PUBLIC" ]] ; then
	# Jeśli częściowo publiczne, udostępniamy tylko zalogowanym
	trac-admin "$TRAC_DIR" permission add authenticated $TRAC_AUTHENTICATED_ACCESS_RIGHTS
fi

chmod -R g+rwx "$TRAC_DIR"
