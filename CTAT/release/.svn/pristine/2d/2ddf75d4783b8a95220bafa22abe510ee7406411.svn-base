$bash

rm -f OneBigJS.js
find . -name .svn -prune -o -name old_code -prune -o -name "*.js" -print0 | xargs -0 wc
find . -name .svn -prune -o -name old_code -prune -o -name "*.js" -print | ( \
	while read f; do
		printf "//\n//*** start of %s ***\n//\n" "$f" >> OneBigJS.js
		typeset -i len=$(wc -l <OneBigJS.js)
		printf "%6d %s\n" $((1+$len)) "$f"
		cat "$f" >> OneBigJS.js
	done
)
wc OneBigJS.js
