if [ $# -eq 0 ]
  then
    echo "Usage: ./buildjs_new.sh <username>"
    exit
fi
echo "compiling..."
npm run build:js:release
echo "//# sourceMappingURL=admin.js.map" >> build-out/js/admin.js
if [ $? -ne 0 ] 
  then
    echo "Build Failed, Exiting..."
    exit
fi
cp build-out/js/admin* ../js/silex
scp build-out/js/admin* $1@ctat-new.pact.cs.cmu.edu:/usr0/rails/preview/current/public/tutordesk/js/silex/
