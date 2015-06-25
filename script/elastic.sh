#!/bin/sh
#curl -XPOST "localhost:9200/chengpohi/tab/1" -d '{
#  "name": "Tools"
#}'

curl -XPOST "localhost:9200/chengpohi/bookmark/_update" -d '{
    "query" : { match_all: {} },
    "script" : "ctx._source._tab_id = 1"
}'
