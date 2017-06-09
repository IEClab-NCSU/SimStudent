echo "compiling..."
npm run build:js:release
echo "//# sourceMappingURL=admin.js.map" >> build-out/js/admin.js
REM scp build-out/js/admin* mdb91@ctat-new.pact.cs.cmu.edu:/usr0/rails/preview/current/public/tutordesk/js/silex
