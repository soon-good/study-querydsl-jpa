# mariadb docker container 중지 및 볼륨 삭제 스크립트

name_codingtest_mysql='codingtest-mariadb'

cnt_codingtest_mysql=`docker container ls --filter name=codingtest-mariadb | wc -l`
cnt_codingtest_mysql=$(($cnt_codingtest_mysql -1))

if [ $cnt_codingtest_mysql -eq 0 ]
then
    echo "'$name_codingtest_mysql' 컨테이너가 없습니다. 삭제를 진행하지 않습니다."

else
    echo "'$name_codingtest_mysql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    docker container stop codingtest-mariadb
    rm -rf ~/env/docker/codingtest/volumes/codingtest-mariadb/*
    echo "\n'$name_codingtest_mysql' 컨테이너 삭제를 완료했습니다.\n"
fi
