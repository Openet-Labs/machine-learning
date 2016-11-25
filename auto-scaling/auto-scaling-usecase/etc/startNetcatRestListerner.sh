#/**************************************************************************
# *
# * Copyright © Openet Telecom, Ltd. 
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *    http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# **************************************************************************/

#!/bin/bash

rm -f out
mkfifo out
trap "rm -f out" EXIT
while true
do
  cat out | nc -l 1500 > >( # parse the netcat output, to build the answer redirected to the pipe "out".
    export REQUEST=
    while read line
    do
      line=$(echo "$line" | tr -d '[\r\n]')

      if echo "$line" | grep -qE '^GET /' # if line starts with "GET /"
      then
        REQUEST=$(echo "$line" | cut -d ' ' -f2) # extract the request
      elif [ "x$line" = x ] # empty line / end of request
      then
        HTTP_200="HTTP/1.1 200 OK"
        HTTP_LOCATION="Location:"
        HTTP_404="HTTP/1.1 404 Not Found"
	CONTENT_TYPE="Content-Type: application/json"
        # call a script here
        # Note: REQUEST is exported, so the script can parse it (to answer 200/403/404 status code + content)
        if echo $REQUEST | grep -qE '\b/scale/\b'
        then
            printf "%s\n%s %s\n%s\n\n%s\n" "$HTTP_200" "$HTTP_LOCATION" $REQUEST "$CONTENT_TYPE" ${REQUEST#"/scale/"} > out
	    echo $REQUEST
        else
            printf "%s\n%s %s\n\n%s\n" "$HTTP_404" "$HTTP_LOCATION" $REQUEST "Resource $REQUEST NOT FOUND!" > out
        fi
      fi
    done
  )
done
