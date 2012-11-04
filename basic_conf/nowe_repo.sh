#!/bin/bash

SVN="";
TRAC="";
NAME="";
DESCRIPTION="";

printHelp () {
echo "Skrypt tworzący track i repozytorium svn"
echo "Wywołanie: nowe_repo.sh <-t> <-s> <-d opis> <-n nazwa_repozytorium>"
echo "Parametry:"
echo "t - tworzy środowisko trac"
echo "s - tworzy repozytorium svn"
echo "d - opis instancji trac"
}

while getopts ":tshn:d:" optname
  do
    case "$optname" in
      "h")
        printHelp
        exit 0;
        ;;
      "t")
        TRAC="1";
        ;;
      "s")
        SVN="1";
        ;;
      "n")
        NAME=$OPTARG
        ;;
      "d")
        DESCRIPTION=$OPTARG
        ;;
      "?")
        echo "Unknown option $OPTARG"
        ;;
      ":")
        echo "Nie podano nazwy!"
        printHelp
        exit 1;
        ;;
      *)
      # Should not occur
        echo "Unknown error while processing options"
        ;;
    esac
  done

if [[($TRAC -eq "") && ($SVN -eq "")]]; then
  echo "Nie wybrano żadnej opcji!"
  printHelp
  exit 1;
fi

#tu juz nie ma ifow wiec opcje sa ignorowane tak naprawde

#tworzenie SVN
svnadmin create /var/www/svn/$NAME

#kopiowanie pliku wiazaocego svn z trac (jest w katalogu templates)
#i ustawianie w nim odpowiednich wpisow
cp /root/bin/templates/trac-post-commit-hook.py /var/www/svn/$NAME/hooks
echo '#!/bin/sh' > /var/www/svn/$NAME/hooks/post-commit
echo 'REPOS="$1"' >> /var/www/svn/$NAME/hooks/post-commit
echo 'REV="$2"' >> /var/www/svn/$NAME/hooks/post-commit
echo 'TRAC_ENV="/var/www/trac/'$NAME'"' >> /var/www/svn/$NAME/hooks/post-commit
echo '/usr/bin/python /var/www/svn/'$NAME'/hooks/trac-post-commit-hook.py -p "$TRAC_ENV" -r "$REV"' >> /var/www/svn/$NAME/hooks/post-commit

chmod +x /var/www/svn/$NAME/hooks/trac-post-commit-hook.py
chmod +x /var/www/svn/$NAME/hooks/post-commit
chown apache:apache /var/www/svn/$NAME -R
chmod o-rwx /var/www/svn/$NAME -R

#kopiowanie szablonu (tez nagralem do templates)
cp -r /var/www/trac/kask-template /var/www/trac/$NAME
chown apache:apache /var/www/trac/$NAME -R
chmod o-rwx /var/www/trac/$NAME -R

# i modyfikacja jego pliku konfiguracyjnego (trac.ini)
/usr/bin/perl -p -i -e "s/repository_dir = \/var\/www\/svn\/test/repository_dir = \/var\/www\/svn\/$NAME/g" /var/www/trac/$NAME/conf/trac.ini
/usr/bin/perl -p -i -e "s/descr = KASK project template/descr = $DESCRIPTION/g" /var/www/trac/$NAME/conf/trac.ini
NAME_UP=`echo $NAME | tr '[:lower:]' '[:upper:]'`
/usr/bin/perl -p -i -e "s/name = KASK_TEMPLATE_NOT_FOR_ACTUAL_USAGE/name = $NAME_UP/g" /var/www/trac/$NAME/conf/trac.ini
/usr/bin/perl -p -i -e "s/url = /url = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME\//g" /var/www/trac/$NAME/conf/trac.ini
/usr/bin/perl -p -i -e "s/folder_name = kask-template/folder_name = $NAME/g" /var/www/trac/$NAME/conf/trac.ini
/usr/bin/perl -p -i -e "s/link = https:\/\/lab527.eti.pg.gda.pl\/trac\/kask-template/link = https:\/\/kask.eti.pg.gda.pl\/trac\/$NAME/g" /var/www/trac/$NAME/conf/trac.ini

# to tylko do przegladu czy skrypt zrobil ok, mozna wywalic
#nano -w /var/www/trac/$NAME/conf/trac.ini

# zaczytanie aktualnego stanu repozytorium (track odwoluje sie bezposrednio po systemie plikow!)
trac-admin /var/www/trac/$NAME repository resync '(default)'
