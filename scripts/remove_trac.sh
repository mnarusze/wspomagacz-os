#!/bin/bash

. /home/maryl/NetBeansProjects/wspomagacz-os/scripts/common_functions.sh

get_input $*
check_input_remove_trac

mv "$TRAC_DIR" "$PROJECTS_ARCHIVE_DIR/$TODAY/trac"

if [[ ! -d "$PROJECTS_ARCHIVE_DIR/$TODAY/trac/$PROJECT_NAME" ]] ; then
	echo "Błąd: nie udało się przenieść katalogu TRAC do archiwum"
	exit 5
fi