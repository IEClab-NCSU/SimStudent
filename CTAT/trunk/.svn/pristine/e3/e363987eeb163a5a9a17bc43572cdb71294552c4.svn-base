destDir="/usr0/rails/preview/current/public/tutordesk/js/silex/"
if [ $# -eq 0 ]
  then
    echo "Usage: ./buildjs_new.sh <username> [<destDir>]"
    echo "where--"
    echo "  username is the user account on test site preview.ctat.cs.cmu.edu"
    echo "  destDir is the target directory; default ${destDir}"
    exit
  fi
if [ $# -gt 1 ]
  then
    destDir="$2"
  fi
echo "compiling..."
mkdir -p build-out build-out/js
npm run build:js:release
echo "//# sourceMappingURL=admin.js.map" >> build-out/js/admin.js
if [ $? -ne 0 ] 
  then
    echo "Build Failed, Exiting..."
    exit
fi
cp build-out/js/admin* ../js/silex
scp build-out/js/admin* "$1@preview.ctat.cs.cmu.edu:${destDir}"
