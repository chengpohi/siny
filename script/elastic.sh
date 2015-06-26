#!/bin/sh
#curl -XPOST "localhost:9200/chengpohi/tab/1" -d '{
#  "name": "Tools"
#}'

#curl -XPOST "localhost:9200/chengpohi/bookmark/_update" -d '{
#    "query" : { match_all: {} },
#    "script" : "ctx._source._tab_id = 1"
#}'

#curl -XPUT "localhost:9200/chengpohi/_mapping/bookmark" -d '
#{
#      "bookmark" : {
#        "properties" : {
#          "_tab_id" : {
#            "type" : "string"
#          },
#          "created_at" : {
#            "type" : "date",
#            "format" : "dateOptionalTime"
#          },
#          "name" : {
#            "type" : "string"
#          },
#          "url" : {
#            "type" : "string"
#          }
#        }
#      }
#}'

curl -XGET "localhost:9200/chengpohi/bookmark/_search?pretty" -d '{
	"query": {
		"term": {"_tab_id": "rose"}
	}
'}
