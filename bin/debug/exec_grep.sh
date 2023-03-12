
#!/usr/bin/env bash

TIMEOUT=1800
sizes=("1GB" "10GB" "100GB")
types=("normal" "debug")
RESTART="true"




. ./base_grep.sh 1GB 0 normal
exit 0

for exec in 1 2 3 4 5; do
    for size in ${sizes[@]}; do
        for type in ${types[@]}; do
            . ./base_grep.sh ${size} ${exec} ${type}
        done
    done
done

