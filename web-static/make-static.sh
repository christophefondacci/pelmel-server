#!/bin/sh

cd ../web-proto/src/main/webapp
tar cvf ../../../../web-static/static.tar js css jwplayer styles
cd ../../../../web-static
gzip -f static.tar
scp static.tar.gz root@tg_front1:/var/www

