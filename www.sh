#! /bin/bash

DIR=rustanddust.ch
rm  $DIR/*~
rsync -avz -e ssh ./$DIR erratic:/var/www/sites/
