if [ $# -eq 0 ]
  then
    echo "Usage: ./buildcss_new.sh <username>"
    exit
fi
npm run build:css
cp build-out/css/admin.css ../css/silex
scp build-out/css/admin.css $1@ctat-new.pact.cs.cmu.edu:/usr0/rails/preview/current/public/tutordesk/css/silex/admin.css
