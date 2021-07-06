# mariadb docker container repl 접속 스크립트

name_codingtest_mysql='codingtest-mariadb'

cnt_codingtest_mysql=`docker container ls --filter name=codingtest-mariadb | wc -l`
cnt_codingtest_mysql=$(($cnt_codingtest_mysql -1))

if [ $cnt_codingtest_mysql -eq 0 ]
then
    echo "'$name_codingtest_mysql' 컨테이너가 없습니다. 컨테이너를 구동해주세요."

else
    echo "'$name_codingtest_mysql' 컨테이너의 BASH 쉘 접속을 시작합니다."
    docker container exec -it codingtest-mariadb sh
fi
